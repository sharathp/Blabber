package com.sharathp.blabber.repositories.impl;

import android.content.Context;

import com.sharathp.blabber.models.HomeTimeline;
import com.sharathp.blabber.models.HomeTimelineWithUser;
import com.sharathp.blabber.models.Like;
import com.sharathp.blabber.models.LikeWithUser;
import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.Search;
import com.sharathp.blabber.models.SearchWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.models.UserTimeLineTweetWithUser;
import com.sharathp.blabber.models.UserTimeline;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.db.BlabberDatabase;
import com.yahoo.squidb.android.AndroidTableModel;
import com.yahoo.squidb.sql.Delete;
import com.yahoo.squidb.sql.Order;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import java.util.Arrays;
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
    public SquidSupportCursorLoader<HomeTimelineWithUser> getHomeTimeline() {
        final Query query = Query.select(HomeTimelineWithUser.PROPERTIES)
                .orderBy(Order.desc(HomeTimelineWithUser.CREATED_AT));
        final SquidSupportCursorLoader<HomeTimelineWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, HomeTimelineWithUser.class, query);
        loader.setNotificationUri(HomeTimeline.CONTENT_URI);
        loader.setNotificationUri(Tweet.CONTENT_URI);
        return loader;
    }

    @Override
    public SquidSupportCursorLoader<UserTimeLineTweetWithUser> getUserTimeline(final Long userId) {
        final Query query = Query.select(UserTimeLineTweetWithUser.PROPERTIES)
                .where(UserTimeLineTweetWithUser.USER_TIME_LINE_ID.eq(userId))
                .orderBy(Order.desc(UserTimeLineTweetWithUser.CREATED_AT));
        final SquidSupportCursorLoader<UserTimeLineTweetWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, UserTimeLineTweetWithUser.class, query);
        loader.setNotificationUri(UserTimeline.CONTENT_URI);
        loader.setNotificationUri(Tweet.CONTENT_URI);
        return loader;
    }

    @Override
    public SquidSupportCursorLoader<LikeWithUser> getUserLikes(final Long userId) {
        final Query query = Query.select(LikeWithUser.PROPERTIES)
                .where(LikeWithUser.USER_LIKE_ID.eq(userId))
                .orderBy(Order.desc(LikeWithUser.CREATED_AT));
        final SquidSupportCursorLoader<LikeWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, LikeWithUser.class, query);
        loader.setNotificationUri(Like.CONTENT_URI);
        loader.setNotificationUri(Tweet.CONTENT_URI);
        return loader;
    }

    @Override
    public SquidSupportCursorLoader<MentionsWithUser> getMentions() {
        final Query query = Query.select(MentionsWithUser.PROPERTIES)
                .orderBy(Order.desc(MentionsWithUser.CREATED_AT));
        final SquidSupportCursorLoader<MentionsWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, MentionsWithUser.class, query);
        loader.setNotificationUri(Mentions.CONTENT_URI);
        loader.setNotificationUri(Tweet.CONTENT_URI);
        return loader;
    }

    @Override
    public SquidSupportCursorLoader<SearchWithUser> getSearches(final String queryString) {
        final Query query = Query.select(SearchWithUser.PROPERTIES)
                .where(SearchWithUser.SEARCH_QUERY.eq(queryString))
                .orderBy(Order.desc(SearchWithUser.CREATED_AT));
        final SquidSupportCursorLoader<SearchWithUser> loader = new SquidSupportCursorLoader<>(mContext, mDatabase, SearchWithUser.class, query);
        loader.setNotificationUri(Search.CONTENT_URI);
        loader.setNotificationUri(Tweet.CONTENT_URI);
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
    public LikeWithUser getLatestUserLike(final Long userId) {
        final Query query = Query.select(LikeWithUser.PROPERTIES)
                .where(LikeWithUser.USER_LIKE_ID.eq(userId))
                .orderBy(Order.desc(LikeWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(LikeWithUser.class, query);
    }

    @Override
    public LikeWithUser getEarliestUserLike(final Long userId) {
        final Query query = Query.select(LikeWithUser.PROPERTIES)
                .where(LikeWithUser.USER_LIKE_ID.eq(userId))
                .orderBy(Order.asc(LikeWithUser.CREATED_AT))
                .limit(1);
        return mDatabase.fetchByQuery(LikeWithUser.class, query);
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
            for (final Mentions mention : mentions) {
                final Query query = Query.select(Mentions.PROPERTIES)
                        .where(Mentions.TWEET_ID.eq(mention.getTweetId()));

                // insert if it doesn't exist
                if (mDatabase.fetchByQuery(Mentions.class, query) == null) {
                    success = mDatabase.persist(mention);
                } else {
                    success = true;
                }

                // short-circuit and exit the loop
                if (!success) {
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
    public boolean checkAndInsertUserTimelines(final long userId, final List<UserTimeline> userTimelines) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final UserTimeline userTimeline : userTimelines) {
                final Query query = Query.select(UserTimeline.PROPERTIES)
                        .where(UserTimeline.USER_TIME_LINE_ID.eq(userId))
                        .where(UserTimeline.TWEET_ID.eq(userTimeline.getTweetId()));

                // insert if it doesn't exist
                if (mDatabase.fetchByQuery(UserTimeline.class, query) == null) {
                    success = mDatabase.persist(userTimeline);
                } else {
                    success = true;
                }

                // short-circuit and exit the loop
                if (!success) {
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
            for (final HomeTimeline homeTimeline : homeTimelines) {
                final Query query = Query.select(HomeTimeline.PROPERTIES)
                        .where(HomeTimeline.TWEET_ID.eq(homeTimeline.getId()));

                // insert if it doesn't exist
                if (mDatabase.fetchByQuery(HomeTimeline.class, query) == null) {
                    success = mDatabase.persist(homeTimeline);
                } else {
                    success = true;
                }

                // short-circuit and exit the loop
                if (!success) {
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
    public boolean checkAndInsertLikes(final Long userId, final Collection<Like> likes) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final Like like : likes) {
                final Query query = Query.select(Like.PROPERTIES)
                        .where(Like.USER_LIKE_ID.eq(userId))
                        .where(Like.TWEET_ID.eq(like.getTweetId()));

                // insert if it doesn't exist
                if (mDatabase.fetchByQuery(Like.class, query) == null) {
                    success = mDatabase.persist(like);
                } else {
                    success = true;
                }

                // short-circuit and exit the loop
                if (!success) {
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
    public boolean checkAndInsertSearches(final Collection<Search> searches) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final Search search : searches) {
                final Query query = Query.select(Search.PROPERTIES)
                        .where(Search.QUERY.eq(search.getQuery()))
                        .where(Search.TWEET_ID.eq(search.getTweetId()));

                // insert if it doesn't exist
                if (mDatabase.fetchByQuery(Search.class, query) == null) {
                    success = mDatabase.persist(search);
                } else {
                    success = true;
                }

                // short-circuit and exit the loop
                if (!success) {
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

    @Override
    public User getUser(final Long userId) {
        return mDatabase.fetch(User.class, userId);
    }

    public User getUserByScreenName(final String screenName) {
        return mDatabase.fetchByQuery(User.class, Query.select(User.PROPERTIES)
                .where(User.SCREEN_NAME.eq(screenName)));
    }

    @Override
    public boolean updateTweet(final Tweet tweet) {
        return checkAndInsertTweets(Arrays.asList(tweet));
    }

    @Override
    public Tweet getTweet(final Long tweetId) {
        return mDatabase.fetch(Tweet.class, tweetId);
    }

    @Override
    public int deleteLikesByUser(final Long userId, final Long tweetId) {
        return mDatabase.delete(Delete.from(Like.TABLE)
                .where(Like.USER_LIKE_ID.eq(userId)
                        .and(Like.TWEET_ID.eq(tweetId))));
    }

    @Override
    public int deleteAllSearchData() {
        return mDatabase.delete(Delete.from(Search.TABLE));
    }

    @Override
    public void deleteDatabase() {
        mDatabase.recreate();
    }

    private <T extends AndroidTableModel> boolean checkAndInsertElements(final Collection<T> models) {
        boolean success = true;
        mDatabase.beginTransaction();
        try {
            for (final T model : models) {
                // insert if the row doesn't exist, else update
                if (mDatabase.fetch(model.getClass(), model.getId(), model.getIdProperty()) == null) {
                    success = mDatabase.insertWithGivenId(model);
                } else {
                    success = mDatabase.saveExisting(model);
                }
                // short-circuit and exit the loop
                if (!success) {
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