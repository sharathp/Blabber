package com.sharathp.blabber.repositories.impl;

import android.content.Context;

import com.sharathp.blabber.models.HomeTimeline;
import com.sharathp.blabber.models.HomeTimelineWithUser;
import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.models.UserTimeLineTweetWithUser;
import com.sharathp.blabber.models.UserTimeline;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.db.BlabberDatabase;
import com.yahoo.squidb.android.AndroidTableModel;
import com.yahoo.squidb.sql.Order;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import java.util.Collection;
import java.util.List;

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
    public SquidSupportCursorLoader<HomeTimelineWithUser> getTweets(final Query query) {
        final SquidSupportCursorLoader<HomeTimelineWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, HomeTimelineWithUser.class, query);
        loader.setNotificationUri(HomeTimeline.CONTENT_URI);
        return loader;
    }

    @Override
    public SquidSupportCursorLoader<UserTimeLineTweetWithUser> getUserTimeline(final Query query) {
        final SquidSupportCursorLoader<UserTimeLineTweetWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, UserTimeLineTweetWithUser.class, query);
        loader.setNotificationUri(UserTimeline.CONTENT_URI);
        return loader;
    }

    @Override
    public SquidSupportCursorLoader<MentionsWithUser> getMentions(final Query query) {
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
    public UserTimeLineTweetWithUser getLatestUserTimeline(final Long userId) {
        final Query query = Query.select(UserTimeLineTweetWithUser.PROPERTIES)
                .where(UserTimeLineTweetWithUser.USER_TIME_LINE_ID.eq(userId))
                .orderBy(Order.desc(UserTimeLineTweetWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(UserTimeLineTweetWithUser.class, query);
    }

    @Override
    public UserTimeLineTweetWithUser getEarliestUserTimeline(final Long userId) {
        final Query query = Query.select(UserTimeLineTweetWithUser.PROPERTIES)
                .where(UserTimeLineTweetWithUser.USER_TIME_LINE_ID.eq(userId))
                .orderBy(Order.asc(UserTimeLineTweetWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(UserTimeLineTweetWithUser.class, query);
    }

    @Override
    public HomeTimelineWithUser getLatestHomeTimeline() {
        final Query query = Query.select(HomeTimelineWithUser.PROPERTIES)
                .orderBy(Order.desc(HomeTimelineWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(HomeTimelineWithUser.class, query);
    }

    @Override
    public HomeTimelineWithUser getEarliestHomeTimeline() {
        final Query query = Query.select(HomeTimelineWithUser.PROPERTIES)
                .orderBy(Order.asc(HomeTimelineWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(HomeTimelineWithUser.class, query);
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
    public boolean checkAndInsertUserTimelines(final List<UserTimeline> userTimelines) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final UserTimeline userTimeline: userTimelines) {
                final Query query = Query.select(UserTimeline.PROPERTIES)
                        .where(UserTimeline.TWEET_ID.eq(userTimeline.getId()));

                // insert if it doesn't exist
                if(mDatabase.fetchByQuery(UserTimeline.class, query) == null) {
                    success = mDatabase.persist(userTimeline);
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
    public boolean checkAndInsertHomeTimelines(final List<HomeTimeline> homeTimelines) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final HomeTimeline homeTimeline: homeTimelines) {
                final Query query = Query.select(HomeTimeline.PROPERTIES)
                        .where(HomeTimeline.TWEET_ID.eq(homeTimeline.getId()));

                // insert if it doesn't exist
                if(mDatabase.fetchByQuery(HomeTimeline.class, query) == null) {
                    success = mDatabase.persist(homeTimeline);
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