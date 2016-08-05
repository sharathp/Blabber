package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;

public class UserResource {

    @SerializedName("id")
    long id;

    @SerializedName("name")
    String name;

    @SerializedName("screen_name")
    String screenName;

    @SerializedName("profile_image_url")
    String profileImageUrl;

    @SerializedName("followers_count")
    int followersCount;

    @SerializedName("description")
    String description;

    @SerializedName("profile_background_image_url")
    String profileBackgroundImageUrl;

    @SerializedName("friends_count")
    int friendsCount;

    @SerializedName("following")
    boolean following;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public void setProfileBackgroundImageUrl(String profileBackgroundImageUrl) {
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
