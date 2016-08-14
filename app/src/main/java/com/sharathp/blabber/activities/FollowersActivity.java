package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sharathp.blabber.BlabberApplication;

public class FollowersActivity extends BaseFolloweeActivity {

    public static Intent createIntent(final Context context, final Long userId) {
        return createIntent(context, FollowersActivity.class, userId);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlabberApplication.from(this).getComponent().inject(this);
        getSupportActionBar().setTitle("Following");
    }

    @Override
    protected void doRetrievePastFollowees(final Long userId, final Long nextCursor, final AsyncHttpResponseHandler handler) {
        mTwitterClient.getFollowers(userId, DEFAULT_COUNT, nextCursor, handler);
    }

    @Override
    protected void doRefreshFollowees(final Long userId, final AsyncHttpResponseHandler handler) {
        mTwitterClient.getFollowers(userId, DEFAULT_COUNT, -1L, handler);
    }
}
