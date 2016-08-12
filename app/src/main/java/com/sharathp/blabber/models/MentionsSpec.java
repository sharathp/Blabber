package com.sharathp.blabber.models;

import android.net.Uri;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

// almost like many-many table between user and tweets
@TableModelSpec(className="Mentions", tableName="user_mentions", tableConstraint = "UNIQUE (userMentionId, tweetId)")
public class MentionsSpec {

    public static final Uri CONTENT_URI = Uri.parse("content://com.sharathp.blabber/mentions");

    // id of the user whose timeline is being viewed
    @ColumnSpec(constraints = "NOT NULL")
    public long userMentionId;

    @ColumnSpec(constraints = "NOT NULL")
    public long tweetId;
}
