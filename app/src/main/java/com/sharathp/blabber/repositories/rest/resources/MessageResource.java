package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MessageResource {

    @SerializedName("id_str")
    long id;

    @SerializedName("created_at")
    Date createdAt;

    @SerializedName("text")
    String text;

    User sender;

    User recipient;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public static class User {

        @SerializedName("id_str")
        long id;

        @SerializedName("profile_image_url")
        String profileImageUrl;

        @SerializedName("name")
        String realName;

        @SerializedName("screen_name")
        String screenName;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getScreenName() {
            return screenName;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }
    }

}
