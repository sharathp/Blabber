package com.sharathp.blabber.di.modules;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.sharathp.blabber.models.HomeTimeline;
import com.sharathp.blabber.models.Like;
import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.Search;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.models.UserTimeline;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.db.BlabberDatabase;
import com.sharathp.blabber.repositories.impl.SQLiteTwitterDAO;
import com.yahoo.squidb.android.UriNotifier;
import com.yahoo.squidb.data.AbstractModel;
import com.yahoo.squidb.data.DataChangedNotifier;
import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.sql.SqlTable;
import com.yahoo.squidb.sql.Table;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @NonNull
    @Singleton
    public TwitterDAO provideTwitterDAO(@NonNull final SQLiteTwitterDAO sqliteTwitterDAO) {
        return sqliteTwitterDAO;
    }

    @Provides
    @NonNull
    @Singleton
    public BlabberDatabase provideBlabberDatabase(@NonNull final Context context) {
        final BlabberDatabase database = new BlabberDatabase(context);
        final ContentResolver contentResolver = context.getContentResolver();

        // Setting up a UriNotifier will sent ContentObserver notifications to Uris on table writes
        registerSimpleDataChangedNotifier(contentResolver, database, Tweet.TABLE, Tweet.CONTENT_URI);
        registerSimpleDataChangedNotifier(contentResolver, database, User.TABLE, User.CONTENT_URI);
        registerSimpleDataChangedNotifier(contentResolver, database, UserTimeline.TABLE, UserTimeline.CONTENT_URI);
        registerSimpleDataChangedNotifier(contentResolver, database, Mentions.TABLE, Mentions.CONTENT_URI);
        registerSimpleDataChangedNotifier(contentResolver, database, HomeTimeline.TABLE, HomeTimeline.CONTENT_URI);
        registerSimpleDataChangedNotifier(contentResolver, database, Like.TABLE, Like.CONTENT_URI);
        registerSimpleDataChangedNotifier(contentResolver, database, Search.TABLE, Search.CONTENT_URI);
        return database;
    }

    private void registerSimpleDataChangedNotifier(final ContentResolver contentResolver,
                                                   final SquidDatabase database,
                                                   final Table table,
                                                   final Uri uri) {
        database.registerDataChangedNotifier(new UriNotifier(contentResolver, table) {
            @Override
            protected boolean accumulateNotificationObjects(final Set<Uri> uris, final SqlTable<?> table, final SquidDatabase database,
                                                            final DataChangedNotifier.DBOperation operation, final AbstractModel modelValues, final long rowId) {
                return uris.add(uri);
            }
        });
    }
}