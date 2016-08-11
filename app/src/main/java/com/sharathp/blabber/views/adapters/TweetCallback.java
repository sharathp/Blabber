package com.sharathp.blabber.views.adapters;

import com.sharathp.blabber.models.ITweetWithUser;

public interface TweetCallback {

    void onTweetSelected(ITweetWithUser tweet);

    void onTweetReplied(ITweetWithUser tweet);
}
