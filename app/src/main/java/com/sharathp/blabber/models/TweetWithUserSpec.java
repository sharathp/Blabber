package com.sharathp.blabber.models;

import com.yahoo.squidb.annotations.ViewModelSpec;
import com.yahoo.squidb.annotations.ViewQuery;
import com.yahoo.squidb.sql.Property;
import com.yahoo.squidb.sql.Query;

@ViewModelSpec(className="TweetWithUser", viewName="tweets_and_users")
public class TweetWithUserSpec {

    @ViewQuery
    public static final Query QUERY = Query.select().from(Tweet.TABLE)
            .leftJoin(User.TABLE, Tweet.USER_ID.eq(User.ID));

    public static final Property.LongProperty ID = Tweet.ID;

    public static final Property.StringProperty TEXT = Tweet.TEXT;

    public static final Property.IntegerProperty FAVORITE_COUNT = Tweet.FAVORITE_COUNT;

    public static final Property.IntegerProperty RETWEET_COUNT = Tweet.RETWEET_COUNT;

    public static final Property.BooleanProperty FAVORITED = Tweet.FAVORITED;

    public static final Property.BooleanProperty RETWEETED = Tweet.RETWEETED;

    public static final Property.StringProperty IN_REPLY_TO_SCREEN_NAME = Tweet.IN_REPLY_TO_SCREEN_NAME;

    public static final Property.LongProperty IN_REPLY_TO_STATUS_ID = Tweet.IN_REPLY_TO_STATUS_ID;

    public static final Property.LongProperty CREATED_AT = Tweet.CREATED_AT;

    public static final Property.LongProperty USER_ID = User.ID;

    public static final Property.StringProperty USER_REAL_NAME = User.REAL_NAME;

    public static final Property.StringProperty USER_SCREEN_NAME = User.SCREEN_NAME;

    public static final Property.StringProperty USER_IMAGE_URL = User.PROFILE_IMAGE_URL;
}
