package com.sharathp.blabber.models;

public interface ITweetWithUser {

    Long getId();

    String getText();

    Integer getFavoriteCount();

    Integer getRetweetCount();

    Boolean getFavorited();

    Boolean getRetweeted();

    String getInReplyToScreenName();

    Long getInReplyToStatusId();

    Long getCreatedAt();

    String getImageUrl();

    String getVideoUrl();

    Integer getImageWidth();

    Integer getImageHeight();

    Double getVideoAspectRatio();

    Long getUserId();

    String getUserRealName();

    String getUserScreenName();

    String getUserImageUrl();

    Long getRetweetedStatusId();

    Long getRetweetedUserId();

    String getRetweetedUserName();

    String getRetweetedScreenName();

    String getRetweetedProfileImageUrl();

    Integer getRetweetedFavoriteCount();
}
