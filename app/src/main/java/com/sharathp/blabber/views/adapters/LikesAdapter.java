package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.LikeWithUser;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class LikesAdapter extends BaseTweetsAdapter<LikeWithUser, SquidViewHolder<LikeWithUser>> {

    public LikesAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, TweetWithUser.ID);
    }

    @Override
    protected TweetViewHolder<LikeWithUser> getTweetViewHolder(final View view) {
        return new TweetViewHolder<>(view, mTweetCallback, new LikeWithUser());
    }

    @Override
    protected LoadingItemHolder<LikeWithUser> getLoadingItemViewHolder(final View view) {
        return new LoadingItemHolder<>(view, new LikeWithUser());
    }
}
