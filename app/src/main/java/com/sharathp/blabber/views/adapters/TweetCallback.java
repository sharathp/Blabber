package com.sharathp.blabber.views.adapters;

import com.sharathp.blabber.models.ITweetWithUser;

public interface TweetCallback {

    void onTweetSelected(ITweetWithUser tweet);

    void onTweetReplied(ITweetWithUser tweet);

    void onProfileImageSelected(ITweetWithUser tweet);

    void onFavorited(ITweetWithUser tweet);

    void onUnfavorited(ITweetWithUser tweet);

    void onRetweeted(ITweetWithUser tweet);

    void onUnRetweeted(ITweetWithUser tweet);

    void onUserScreenNameSelected(String userScreeName);

    void onHashSpanSelected(String hash);
}
