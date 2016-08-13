package com.sharathp.blabber.models;

import com.yahoo.squidb.annotations.Implements;
import com.yahoo.squidb.annotations.ViewModelSpec;
import com.yahoo.squidb.annotations.ViewQuery;
import com.yahoo.squidb.sql.Join;
import com.yahoo.squidb.sql.Property;
import com.yahoo.squidb.sql.Query;

@ViewModelSpec(className="LikeWithUser", viewName="likes_and_tweets")
@Implements(interfaceClasses = {ITweetWithUser.class})
public class LikeWithUserSpec {

    @ViewQuery
    public static final Query QUERY = Query.select().from(Like.TABLE)
            .join(Join.inner(Tweet.TABLE, Like.TWEET_ID.eq(Tweet.ID)),
                    Join.inner(User.TABLE, Tweet.USER_ID.eq(User.ID)));

    public static final Property.LongProperty USER_LIKE_ID = Like.USER_LIKE_ID;

    public static final Property.LongProperty ID = Tweet.ID;

    public static final Property.StringProperty TEXT = Tweet.TEXT;

    public static final Property.IntegerProperty FAVORITE_COUNT = Tweet.FAVORITE_COUNT;

    public static final Property.IntegerProperty RETWEET_COUNT = Tweet.RETWEET_COUNT;

    public static final Property.BooleanProperty FAVORITED = Tweet.FAVORITED;

    public static final Property.BooleanProperty RETWEETED = Tweet.RETWEETED;

    public static final Property.StringProperty IN_REPLY_TO_SCREEN_NAME = Tweet.IN_REPLY_TO_SCREEN_NAME;

    public static final Property.LongProperty IN_REPLY_TO_STATUS_ID = Tweet.IN_REPLY_TO_STATUS_ID;

    public static final Property.LongProperty CREATED_AT = Tweet.CREATED_AT;

    public static final Property.StringProperty IMAGE_URL = Tweet.IMAGE_URL;

    public static final Property.StringProperty VIDEO_URL = Tweet.VIDEO_URL;

    public static final Property.IntegerProperty IMAGE_WIDTH = Tweet.IMAGE_WIDTH;

    public static final Property.IntegerProperty IMAGE_HEIGHT = Tweet.IMAGE_HEIGHT;

    public static final Property.DoubleProperty VIDEO_ASPECT_RATIO = Tweet.VIDEO_ASPECT_RATIO;

    // user info

    public static final Property.LongProperty USER_ID = User.ID;

    public static final Property.StringProperty USER_REAL_NAME = User.REAL_NAME;

    public static final Property.StringProperty USER_SCREEN_NAME = User.SCREEN_NAME;

    public static final Property.StringProperty USER_IMAGE_URL = User.PROFILE_IMAGE_URL;

    // retweeted status

    public static final Property.LongProperty RETWEETED_STATUS_ID = Tweet.RETWEETED_STATUS_ID;

    public static final Property.LongProperty RETWEETED_USER_ID = Tweet.RETWEETED_USER_ID;

    public static final Property.StringProperty RETWEETED_USER_NAME = Tweet.RETWEETED_USER_NAME;

    public static final Property.StringProperty RETWEETED_SCREEN_NAME = Tweet.RETWEETED_SCREEN_NAME;

    public static final Property.StringProperty RETWEETED_PROFILE_IMAGE_URL = Tweet.RETWEETED_PROFILE_IMAGE_URL;

    public static final Property.IntegerProperty RETWEETED_FAVORITE_COUNT = Tweet.RETWEETED_FAVORITE_COUNT;
}
