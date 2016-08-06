package com.sharathp.blabber.events;

public class TweetsPastRetrievedEvent {
    private final int tweetsCount;
    private final boolean success;

    public TweetsPastRetrievedEvent(final int tweetsCount, final boolean success) {
        this.tweetsCount = tweetsCount;
        this.success = success;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }

    public boolean isSuccess() {
        return success;
    }
}
