package com.sharathp.blabber.events;

public class MentionsPastRetrievedEvent {
    private final int tweetsCount;
    private final boolean success;

    public MentionsPastRetrievedEvent(final int tweetsCount, final boolean success) {
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
