package com.sharathp.blabber.models;

import android.net.Uri;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className="Tweet", tableName="tweet")
public class TweetSpec {

    public static final Uri CONTENT_URI = Uri.parse("content://com.sharathp.blabber/tweets");

    @PrimaryKey
    @ColumnSpec(name = "_id")
    public long id;

    public boolean favorited;

    public int favoriteCount;

    @ColumnSpec(constraints = "NOT NULL")
    public long createdAt;

    public String inReplyToScreenName;

    public long inReplyToStatusId;

    @ColumnSpec(constraints = "NOT NULL")
    public String text;

    public int retweetCount;

    public boolean retweeted;

    // TODO - replace this with retweetedStatus's id
//    public Tweet retweetedStatus;

    @ColumnSpec(constraints = "NOT NULL")
    public long userId;

    public String imageUrl;

    public int imageWidth;

    public int imageHeight;

    public String videoUrl;

    public int[] videoAspectRatio;

    // retweet related data
    // TODO - use a foreign key here instead
    public long retweetedStatusId;

    public long retweetedUserId;

    public String retweetedUserName;

    public String retweetedScreenName;

    public String retweetedProfileImageUrl;
}
