package com.sharathp.blabber.events;

public class StatusSubmittedEvent {
    private final boolean success;
    private final String status;

    public StatusSubmittedEvent(final String status, final boolean success) {
        this.status = status;
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }
}
