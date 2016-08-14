package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;

public class FolloweeResource {

    @SerializedName("id_str")
    long id;

    @SerializedName("name")
    String name;

    @SerializedName("screen_name")
    String screenName;

    @SerializedName("profile_image_url")
    String profileImageUrl;

    @SerializedName("description")
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}