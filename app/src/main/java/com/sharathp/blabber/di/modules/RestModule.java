package com.sharathp.blabber.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.util.DateUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RestModule {

    @Provides
    @NonNull
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .setDateFormat(DateUtils.DATE_FORMAT_TWITTER)
                .create();
    }

    @Provides
    @NonNull
    @Singleton
    public TwitterClient provideTwitterClient(final Context context) {
        return new TwitterClient(context);
    }
}
