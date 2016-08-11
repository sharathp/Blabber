package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.views.adapters.base.BaseLoadingItemHolder;
import com.sharathp.blabber.views.adapters.base.BaseTweetViewHolder;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class TweetsAdapter extends BaseTweetsAdapter<TweetWithUser, SquidViewHolder<TweetWithUser>> {

    public TweetsAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, TweetWithUser.ID);
    }

    @Override
    protected BaseTweetViewHolder<TweetWithUser> getTweetViewHolder(final View view) {
        return new BaseTweetViewHolder<>(view, mTweetCallback, new TweetWithUser());
    }

    @Override
    protected BaseLoadingItemHolder<TweetWithUser> getLoadingItemViewHolder(final View view) {
        return new BaseLoadingItemHolder<>(view, new TweetWithUser());
    }
}
