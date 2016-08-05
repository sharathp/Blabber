package com.sharathp.blabber.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.sharathp.blabber.models.TweetWithUser;
import com.yahoo.squidb.data.SquidCursor;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<TweetWithUser>> {
    private static final int FEED_ITEM_LOADER_ID = 0;
    private static final int VISIBLE_THRESHOLD = 5;

    @Inject
    EventBus mEventBus;

    private Callback mCallback;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if ( !(context instanceof Callback)) {
            throw new IllegalArgumentException("context must implement: " + Callback.class.getName());
        }
        mCallback = (Callback) context;
    }


    @Override
    public Loader<SquidCursor<TweetWithUser>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<SquidCursor<TweetWithUser>> loader, SquidCursor<TweetWithUser> data) {

    }

    @Override
    public void onLoaderReset(Loader<SquidCursor<TweetWithUser>> loader) {

    }

    /**
     * Required interface for hosting activities.
     */
    public interface Callback {
        void onTweetSelected(TweetWithUser tweetWithUser);
    }
}