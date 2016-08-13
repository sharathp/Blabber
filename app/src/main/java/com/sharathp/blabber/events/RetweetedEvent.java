package com.sharathp.blabber.events;

public class RetweetedEvent {
    private final long tweetId;
    private final boolean success;

    public RetweetedEvent(final long tweetId,
                          final boolean success) {
        this.tweetId = tweetId;
        this.success = success;
    }

    public long getTweetId() {
        return tweetId;
    }

    public boolean isSuccess() {
        return success;
    }
}