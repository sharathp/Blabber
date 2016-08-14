package com.sharathp.blabber.views.adapters;

import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.views.TweetContentCallback;

public interface TweetCallback extends TweetContentCallback {

    void onTweetSelected(ITweetWithUser tweet);

    void onTweetReplied(ITweetWithUser tweet);

    void onProfileImageSelected(ITweetWithUser tweet);

    void onFavorited(ITweetWithUser tweet);

    void onUnfavorited(ITweetWithUser tweet);

    void onRetweeted(ITweetWithUser tweet);

    void onUnRetweeted(ITweetWithUser tweet);
}
