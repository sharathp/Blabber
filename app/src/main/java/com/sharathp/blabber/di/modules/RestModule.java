package com.sharathp.blabber.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sharathp.blabber.repositories.rest.TwitterClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RestModule {

    @Provides
    @NonNull
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @NonNull
    @Singleton
    public TwitterClient provideTwitterClient(final Context context) {
        return new TwitterClient(context);
    }
}
