package com.sharathp.blabber.repositories.db;

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
import com.yahoo.squidb.android.AndroidOpenHelper;
import com.yahoo.squidb.data.ISQLiteDatabase;
import com.yahoo.squidb.data.ISQLiteOpenHelper;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.data.TableModel;
import com.yahoo.squidb.sql.Table;
import com.yahoo.squidb.sql.View;

public class BlabberDatabase extends SquidDatabase {
    private static final String DB_NAME = "blabber.db";
    private static final int VERSION = 1;
    private Context mContext;

    public BlabberDatabase(final Context context) {
        mContext = context;
    }

    @Override
    public String getName() {
        return DB_NAME;
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected Table[] getTables() {
        return new Table[]{
                Tweet.TABLE, User.TABLE, UserTimeline.TABLE,
                Mentions.TABLE, HomeTimeline.TABLE, Like.TABLE,
                Search.TABLE
        };
    }

    @Override
    protected View[] getViews() {
        return new View[]{
            UserTimeLineTweetWithUser.VIEW,
                MentionsWithUser.VIEW, HomeTimelineWithUser.VIEW,
                LikeWithUser.VIEW, SearchWithUser.VIEW
        };
    }

    @Override
    protected boolean onUpgrade(final ISQLiteDatabase db, final int oldVersion, final int newVersion) {
        return true;
    }

    @Override
    protected ISQLiteOpenHelper createOpenHelper(final String databaseName,
                                                 final OpenHelperDelegate delegate,
                                                 final int version) {
        return new AndroidOpenHelper(mContext, databaseName, delegate, version);
    }

    // SquidDatabase doesn't expose #insertRow(TableModel) method, this simply makes that method public
    public boolean insertWithGivenId(TableModel item) {
        return insertRow(item);
    }
}
