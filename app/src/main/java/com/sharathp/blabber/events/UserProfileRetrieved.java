package com.sharathp.blabber.events;

public class UserProfileRetrieved {
    private final long userId;
    private final boolean success;

    public UserProfileRetrieved(final long userId, final boolean success) {
        this.userId = userId;
        this.success = success;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isSuccess() {
        return success;
    }
}