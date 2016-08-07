package com.sharathp.blabber.fragments;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.FragmentComposeBinding;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.repositories.rest.resources.UserResource;
import com.sharathp.blabber.util.ImageUtils;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;

public class ComposeFragment extends DialogFragment {

    private FragmentComposeBinding mBinding;
    private int mMaxCharacterCount;

    private ComposeCallback mCallback;

    @Inject
    Gson mGson;

    @Inject
    TwitterClient mTwitterClient;

    public static ComposeFragment createInstance() {
        return new ComposeFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxCharacterCount = Integer.parseInt(getString(R.string.max_characters_tweet));
        BlabberApplication.from(getActivity()).getComponent().inject(this);
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

        mBinding.btnSubmit.setOnClickListener(v -> submitTweet());

        // dismiss the screen on tapping close
        mBinding.ivClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO - save user details into preferences instead of calling every time
        mTwitterClient.getLoggedInUserDetails(new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                Toast.makeText(getActivity(), R.string.error_profile_retrieval, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                final UserResource userResource = mGson.fromJson(responseString, UserResource.class);
                loadLoggedInUserDetails(userResource);
            }
        });
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
        // show spinner
        mBinding.flLoading.setVisibility(View.VISIBLE);
        // disable edittext to dismiss keyboard
        mBinding.etTweetContent.setEnabled(false);

        final String tweet = mBinding.etTweetContent.getText().toString();

        mTwitterClient.submitTweet(tweet, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                // hide spinner
                mBinding.flLoading.setVisibility(View.GONE);

                // enable edit text again
                mBinding.etTweetContent.setEnabled(true);

                // give feedback to the user
                Toast.makeText(getActivity(), R.string.error_tweet_submit, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                dismiss();

                if (mCallback != null) {
                    mCallback.onTweetSubmitted(tweet);
                }
            }
        });
    }

    private void loadLoggedInUserDetails(final UserResource loggedInUser) {
        ImageUtils.loadImage(getActivity(), mBinding.ivProfileImage, loggedInUser.getProfileImageUrl());
    }

    public interface ComposeCallback {
        void onTweetSubmitted(final String tweet);
    }
}