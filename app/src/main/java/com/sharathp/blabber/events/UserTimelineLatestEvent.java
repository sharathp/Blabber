package com.sharathp.blabber.events;

public class UserTimelineLatestEvent {
    private final long userId;
    private final int tweetsCount;
    private final boolean success;

    public UserTimelineLatestEvent(final long userId, final int tweetsCount,
                                          final boolean success) {
        this.userId = userId;
        this.tweetsCount = tweetsCount;
        this.success = success;
    }

    public long getUserId() {
        return userId;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }

    public boolean isSuccess() {
        return success;
    }
}
