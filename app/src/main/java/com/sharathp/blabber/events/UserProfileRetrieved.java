package com.sharathp.blabber.events;

public class UserProfileRetrieved {
    private final long userId;
    private final String userScreenName;
    private final boolean success;

    public UserProfileRetrieved(final long userId, final String userScreenName, final boolean success) {
        this.userId = userId;
        this.userScreenName = userScreenName;
        this.success = success;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public boolean isSuccess() {
        return success;
    }
}