package com.sharathp.blabber.events;

public class SearchResultsPastEvent {
    private final String query;
    private final int tweetsCount;
    private final boolean success;
    private final long maxId;

    public SearchResultsPastEvent(final String query, final int tweetsCount,
                                     final boolean success, final long maxId) {
        this.query = query;
        this.tweetsCount = tweetsCount;
        this.success = success;
        this.maxId = maxId;
    }

    public String getQuery() {
        return query;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getMaxId() {
        return maxId;
    }
}
