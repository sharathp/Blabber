package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class TweetsAdapter extends BaseTweetsAdapter<TweetWithUser, SquidViewHolder<TweetWithUser>> {

    public TweetsAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, TweetWithUser.ID);
    }

    @Override
    protected TweetViewHolder<TweetWithUser> getTweetViewHolder(final View view) {
        return new TweetViewHolder<>(view, mTweetCallback, new TweetWithUser());
    }

    @Override
    protected LoadingItemHolder<TweetWithUser> getLoadingItemViewHolder(final View view) {
        return new LoadingItemHolder<>(view, new TweetWithUser());
    }
}
