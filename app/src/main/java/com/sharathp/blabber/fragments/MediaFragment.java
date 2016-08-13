package com.sharathp.blabber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MediaFragment extends Fragment {
    private static final String ARG_USER_ID = MediaFragment.class.getSimpleName() + ":USER_ID";

    public static MediaFragment createInstance(final Long userId) {
        final MediaFragment mediaFragment = new MediaFragment();
        final Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        mediaFragment.setArguments(args);
        return mediaFragment;
    }
}
