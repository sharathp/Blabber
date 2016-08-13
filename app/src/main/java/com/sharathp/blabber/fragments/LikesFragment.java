package com.sharathp.blabber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class LikesFragment extends Fragment {
    private static final String ARG_USER_ID = LikesFragment.class.getSimpleName() + ":USER_ID";


    public static LikesFragment createInstance(final Long userId) {
        final LikesFragment likesFragment = new LikesFragment();
        final Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        likesFragment.setArguments(args);
        return likesFragment;
    }
}
