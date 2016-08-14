package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FolloweeListResource {

    @SerializedName("ids")
    List<Long> userIds;

    @SerializedName("next_cursor_str")
    Long nextCursor;

    public Long getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Long nextCursor) {
        this.nextCursor = nextCursor;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}