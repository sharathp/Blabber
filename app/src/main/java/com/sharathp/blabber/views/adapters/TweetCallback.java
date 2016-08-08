package com.sharathp.blabber.views.adapters;

import com.sharathp.blabber.models.TweetWithUser;

public interface TweetCallback {
    void onTweetSelected(TweetWithUser tweet);

    void onTweetReplied(TweetWithUser tweet);
}
