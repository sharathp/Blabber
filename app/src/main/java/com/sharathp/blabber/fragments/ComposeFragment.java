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

import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.FragmentComposeBinding;

public class ComposeFragment extends DialogFragment {
    private FragmentComposeBinding mBinding;
    private int mMaxCharacterCount;

    public static ComposeFragment createInstance() {
        return new ComposeFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxCharacterCount = Integer.parseInt(getString(R.string.max_characters_tweet));
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
}