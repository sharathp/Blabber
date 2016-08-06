package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;
import com.sharathp.blabber.models.Tweet;

import java.util.Date;

public class TweetResource {

    @SerializedName("id")
    long id;

    @SerializedName("favorited")
    boolean favorited;

    @SerializedName("favorite_count")
    int favoriteCount;

    @SerializedName("user")
    UserResource user;

    @SerializedName("created_at")
    Date createdAt;

    @SerializedName("in_reply_to_screen_name")
    String inReplyToScreenName;

    @SerializedName("in_reply_to_status_id")
    long inReplyToStatusId;

    @SerializedName("text")
    String text;

    @SerializedName("retweet_count")
    int retweetCount;

    @SerializedName("retweeted")
    boolean retweeted;

    @SerializedName("retweeted_status")
    TweetResource retweetedStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public TweetResource getRetweetedStatus() {
        return retweetedStatus;
    }

    public void setRetweetedStatus(TweetResource retweetedStatus) {
        this.retweetedStatus = retweetedStatus;
    }

    public Tweet convertToTweet() {
        final Tweet tweet = new Tweet();
        tweet.setId(id);
        tweet.setIsFavorited(favorited);
        tweet.setFavoriteCount(favoriteCount);
        tweet.setUserId(user.getId());
        tweet.setCreatedAt(createdAt.getTime());
        tweet.setInReplyToScreenName(inReplyToScreenName);
        tweet.setInReplyToStatusId(inReplyToStatusId);
        tweet.setText(text);
        tweet.setRetweetCount(retweetCount);
        tweet.setIsRetweeted(retweeted);
        return tweet;
    }
}
