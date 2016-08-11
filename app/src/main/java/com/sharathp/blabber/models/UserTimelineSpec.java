package com.sharathp.blabber.models;

import android.net.Uri;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

// almost like many-many table between user and tweets
@TableModelSpec(className="UserTimeline", tableName="user_timeline")
public class UserTimelineSpec {

    public static final Uri CONTENT_URI = Uri.parse("content://com.sharathp.blabber/user-timelines");

    // id of the user whose timeline is being viewed
    @ColumnSpec(constraints = "NOT NULL")
    public long userTimeLineId;

    @ColumnSpec(constraints = "NOT NULL")
    public long tweetId;
}
