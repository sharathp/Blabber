package com.sharathp.blabber.models;

import android.net.Uri;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.PrimaryKey;
import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className="User", tableName="user")
public class UserSpec {

    public static final Uri CONTENT_URI = Uri.parse("content://com.sharathp.blabber/users");

    @PrimaryKey
    @ColumnSpec(name = "_id")
    public long id;

    @ColumnSpec(constraints = "NOT NULL")
    public String realName;

    @ColumnSpec(constraints = "NOT NULL")
    public String screenName;

    public String profileImageUrl;

    public int followersCount;

    public String description;

    public String profileBackgroundImageUrl;

    public String profileBannerUrl;

    public int friendsCount;

    public boolean following;
}
