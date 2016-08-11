package com.sharathp.blabber.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.FragmentComposeBinding;
import com.sharathp.blabber.events.StatusSubmittedEvent;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.repositories.LocalPreferencesDAO;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.util.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class ComposeFragment extends DialogFragment {
    public static String ARG_REPLY_TO_TWEET = ComposeFragment.class.getSimpleName() + ":REPLY_TO_TWEET";

    private FragmentComposeBinding mBinding;
    private int mMaxCharacterCount;
    private TweetWithUser mReplyTo;

    private ComposeCallback mCallback;

    @Inject
    LocalPreferencesDAO mLocalPreferencesDAO;

    @Inject
    EventBus mEventBus;

    public static ComposeFragment createInstance(final ITweetWithUser tweetWithUser) {
        final ComposeFragment fragment = new ComposeFragment();
        if (tweetWithUser != null) {
            final Bundle args = new Bundle();
            args.putParcelable(ARG_REPLY_TO_TWEET, (Parcelable) tweetWithUser);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxCharacterCount = Integer.parseInt(getString(R.string.max_characters_tweet));
        BlabberApplication.from(getActivity()).getComponent().inject(this);

        if (getArguments() != null) {
            mReplyTo = getArguments().getParcelable(ARG_REPLY_TO_TWEET);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.etTweetContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start, final int count, final int after) {
                // no-op
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start, final int count, final int after) {
                final int remainingCharCount = mMaxCharacterCount - charSequence.length();
                if (charSequence.length() == 0 || remainingCharCount < 0) {
                    mBinding.btnSubmit.setEnabled(false);
                } else {
                    mBinding.btnSubmit.setEnabled(true);
                }

                mBinding.tvTweetCharacterCount.setText(Integer.toString(remainingCharCount));

                if (remainingCharCount < 0) {
                    mBinding.tvTweetCharacterCount.setTextColor(getResources().getColor(R.color.compose_tweet_character_count_max_reached));
                } else {
                    mBinding.tvTweetCharacterCount.setTextColor(getResources().getColor(R.color.compose_tweet_character_count));
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                // no-op
            }
        });

        if (mReplyTo != null) {
            mBinding.tvReplyTo.setVisibility(View.VISIBLE);
            mBinding.ivDownIcon.setVisibility(View.VISIBLE);
            mBinding.tvReplyTo.setText(String.format(getResources().getString(R.string.str_pattern_reply_to), mReplyTo.getUserRealName()));
            mBinding.etTweetContent.setText("@" + mReplyTo.getUserScreenName() + " ");
            mBinding.etTweetContent.setSelection(mBinding.etTweetContent.getText().length());
        }

        mBinding.btnSubmit.setOnClickListener(v -> submitTweet());

        // dismiss the screen on tapping close
        mBinding.ivClose.setOnClickListener(v -> dismiss());

        ImageUtils.loadProfileImage(getActivity(), mBinding.ivProfileImage, mLocalPreferencesDAO.getUserProfileImageUrl());
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        final ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    public void setCallback(final ComposeCallback callback) {
        mCallback = callback;
    }

    private void submitTweet() {
        if (! NetworkUtils.isOnline(getActivity())) {
            Toast.makeText(getContext(), R.string.message_no_internet, Toast.LENGTH_LONG).show();
            return;
        }

        // show spinner
        mBinding.flLoading.setVisibility(View.VISIBLE);
        // disable edittext to dismiss keyboard
        mBinding.etTweetContent.setEnabled(false);

        final String tweet = mBinding.etTweetContent.getText().toString();

        Long inReplyToStatusId = null;

        if (mReplyTo != null) {
            inReplyToStatusId = mReplyTo.getId();
        }

        final Intent tweetService = UpdateTimelineService.createIntentForTweeting(getActivity(), tweet, inReplyToStatusId);
        getActivity().startService(tweetService);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final StatusSubmittedEvent event) {
        if (event.isSuccess()) {
            dismiss();

            if (mCallback != null) {
                mCallback.onTweetSubmitted(event.getStatus());
            }
        } else {
            // hide spinner
            mBinding.flLoading.setVisibility(View.GONE);

            // enable edit text again
            mBinding.etTweetContent.setEnabled(true);

            // give feedback to the user
            Toast.makeText(getActivity(), R.string.error_tweet_submit, Toast.LENGTH_LONG).show();
        }
    }

    public interface ComposeCallback {
        void onTweetSubmitted(final String tweet);
    }
}