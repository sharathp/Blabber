package com.sharathp.blabber.repositories.impl;

import android.content.Context;

import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.db.BlabberDatabase;
import com.yahoo.squidb.android.AndroidTableModel;
import com.yahoo.squidb.sql.Order;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import java.util.Collection;

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
    public SquidSupportCursorLoader<MentionsWithUser> getMentions(Query query) {
        final SquidSupportCursorLoader<MentionsWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, MentionsWithUser.class, query);
        loader.setNotificationUri(Mentions.CONTENT_URI);
        return loader;
    }

    @Override
    public MentionsWithUser getLatestMention() {
        final Query query = Query.select(MentionsWithUser.PROPERTIES)
                .orderBy(Order.desc(MentionsWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(MentionsWithUser.class, query);
    }

    @Override
    public MentionsWithUser getEarliestMention() {
        final Query query = Query.select(MentionsWithUser.PROPERTIES)
                .orderBy(Order.asc(MentionsWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(MentionsWithUser.class, query);
    }

    @Override
    public Tweet getLatestTweet() {
        final Query query = Query.select(Tweet.PROPERTIES)
                .orderBy(Order.desc(Tweet.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(Tweet.class, query);
    }

    @Override
    public Tweet getEarliestTweet() {
        final Query query = Query.select(Tweet.PROPERTIES)
                .orderBy(Order.asc(Tweet.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(Tweet.class, query);
    }

    @Override
    public boolean checkAndInsertUsers(final Collection<User> users) {
        return checkAndInsertElements(users);
    }

    @Override
    public boolean checkAndInsertTweets(final Collection<Tweet> tweets) {
        return checkAndInsertElements(tweets);
    }

    @Override
    public boolean checkAndInsertMentions(final Collection<Mentions> mentions) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final Mentions mention: mentions) {
                final Query query = Query.select(Mentions.PROPERTIES)
                                    .where(Mentions.TWEET_ID.eq(mention.getId()));

                // insert if it doesn't exist
                if(mDatabase.fetchByQuery(Mentions.class, query) == null) {
                    success = mDatabase.persist(mention);
                } else {
                    success = true;
                }

                // short-circuit and exit the loop
                if (! success) {
                    break;
                }
            }
            if (success) {
                mDatabase.setTransactionSuccessful();
            }
        } finally {
            mDatabase.endTransaction();
        }
        return success;
    }

    @Override
    public boolean deleteExistingTweets() {
        mDatabase.deleteAll(Tweet.class);
        return true;
    }

    private <T extends AndroidTableModel> boolean checkAndInsertElements(final Collection<T> models) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final T model: models) {
                // insert if the row doesn't exist, else update
                if (mDatabase.fetch(model.getClass(), model.getId(), model.getIdProperty()) == null) {
                    success = mDatabase.insertWithGivenId(model);
                } else {
                    success = mDatabase.saveExisting(model);
                }
                // short-circuit and exit the loop
                if (! success) {
                    break;
                }
            }
            if (success) {
                mDatabase.setTransactionSuccessful();
            }
        } finally {
            mDatabase.endTransaction();
        }
        return success;
    }
}