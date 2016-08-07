package com.sharathp.blabber.repositories.rest.resources;

import com.google.gson.annotations.SerializedName;
import com.sharathp.blabber.models.Tweet;

import java.util.Date;

public class TweetResource {
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_IMAGE = "photo";

    public static final String SUFFIX_MP4 = ".mp4";

    @SerializedName("id_str")
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

    @SerializedName("in_reply_to_status_id_str")
    long inReplyToStatusId;

    @SerializedName("text")
    String text;

    @SerializedName("retweet_count")
    int retweetCount;

    @SerializedName("retweeted")
    boolean retweeted;

    @SerializedName("retweeted_status")
    TweetResource retweetedStatus;

    @SerializedName("extended_entities")
    EntitiesResource mExtendedEntitiesResource;

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

    public EntitiesResource getExtendedEntitiesResource() {
        return mExtendedEntitiesResource;
    }

    public void setExtendedEntitiesResource(EntitiesResource extendedEntitiesResource) {
        mExtendedEntitiesResource = extendedEntitiesResource;
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

        if (retweetedStatus != null) {
            tweet.setRetweetedStatusId(retweetedStatus.getId());
            tweet.setRetweetedUserId(retweetedStatus.getUser().getId());
            tweet.setRetweetedUserName(retweetedStatus.getUser().getName());
            tweet.setRetweetedScreenName(retweetedStatus.getUser().getScreenName());
            tweet.setRetweetedProfileImageUrl((retweetedStatus.getUser().getProfileImageUrl()));
        }

        if (mExtendedEntitiesResource != null
                && mExtendedEntitiesResource.getMedia() != null
                && ! mExtendedEntitiesResource.getMedia().isEmpty()) {
            final EntitiesResource.MediaResource mediaResource = mExtendedEntitiesResource.getMedia().get(0);

            final String mediaType = mediaResource.getType();

            if (TYPE_IMAGE.equals(mediaType)) {
                tweet.setImageUrl(mediaResource.getMediaUrl());
            }

            if (TYPE_VIDEO.equals(mediaType)) {
                tweet.setVideoUrl(getVideoUrl(mediaResource.getVideoInfo()));
            }
        }

        return tweet;
    }

    private String getVideoUrl(final EntitiesResource.VideoInfoResource videoInfoResource) {
        if (videoInfoResource == null ||
                videoInfoResource.getVariants() == null ||
                videoInfoResource.getVariants().isEmpty()) {
            return null;
        }

        // try to find the .mp4 url
        for (final EntitiesResource.VideoInfoVariantResource resource : videoInfoResource.getVariants()) {
            if (resource.getUrl() != null && resource.getUrl().endsWith(SUFFIX_MP4)) {
                return resource.getUrl();
            }
        }

        // .. else return the first url
        return videoInfoResource.getVariants().get(0).getUrl();
    }
}
