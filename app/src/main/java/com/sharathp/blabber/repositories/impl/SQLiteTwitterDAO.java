package com.sharathp.blabber.repositories.impl;

import android.content.Context;

import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.db.BlabberDatabase;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import javax.inject.Inject;

public class SQLiteTwitterDAO implements TwitterDAO {
    private final Context mContext;
    private final BlabberDatabase mDatabase;

    @Inject
    public SQLiteTwitterDAO(final Context context, final BlabberDatabase database) {
        mContext = context;
        mDatabase = database;
    }

    @Override
    public SquidSupportCursorLoader<TweetWithUser> getTweets(final Query query) {
        final SquidSupportCursorLoader<TweetWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, TweetWithUser.class, query);
        loader.setNotificationUri(Tweet.CONTENT_URI);
        return loader;
    }

    @Override
    public Tweet getLatestTweet() {
        return null;
    }

    @Override
    public boolean insertTweet(final Tweet tweet, final User user) {
        return false;
    }
}