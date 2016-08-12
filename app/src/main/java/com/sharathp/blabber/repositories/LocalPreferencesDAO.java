package com.sharathp.blabber.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalPreferencesDAO {
    private static final String KEY_USER_ID = "BLABBER_PREF_USER_ID";
    private static final String KEY_USER_REAL_NAME = "BLABBER_PREF_USER_REAL_NAME";
    private static final String KEY_USER_SCREEN_NAME = "BLABBER_PREF_USER_SCREEN_NAME";
    private static final String KEY_USER_PROFILE_IMAGE_URL = "BLABBER_PREF_USER_PROFILE_IMAGE_URL";

    private final SharedPreferences mSharedPreferences;
    private final Context mContext;

    @Inject
    public LocalPreferencesDAO(final SharedPreferences sharedPreferences, final Context context) {
        this.mSharedPreferences = sharedPreferences;
        mContext = context;
    }

    public void setUserId(final long userId) {
        mSharedPreferences
                .edit()
                .putLong(KEY_USER_ID, userId)
                .commit();
    }

    public void setUserRealName(final String userRealName) {
        mSharedPreferences
                .edit()
                .putString(KEY_USER_REAL_NAME, userRealName)
                .commit();
    }

    public void setUserScreenName(final String userScreenName) {
        mSharedPreferences
                .edit()
                .putString(KEY_USER_SCREEN_NAME, userScreenName)
                .commit();
    }

    public void setUserProfileImageUrl(final String userProfileImageUrl) {
        mSharedPreferences
                .edit()
                .putString(KEY_USER_PROFILE_IMAGE_URL, userProfileImageUrl)
                .commit();
    }

    public Long getUserId() {
        return mSharedPreferences.getLong(KEY_USER_ID, -1);
    }

    public String getUserRealName() {
        return mSharedPreferences.getString(KEY_USER_REAL_NAME, "");
    }

    public String getUserScreenName() {
        return mSharedPreferences.getString(KEY_USER_SCREEN_NAME, "");
    }

    public String getUserProfileImageUrl() {
        return mSharedPreferences.getString(KEY_USER_PROFILE_IMAGE_URL, "");
    }
}
