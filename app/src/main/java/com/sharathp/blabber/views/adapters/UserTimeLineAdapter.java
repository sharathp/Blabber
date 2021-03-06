package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.UserTimeLineTweetWithUser;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class UserTimeLineAdapter extends BaseTweetsAdapter<UserTimeLineTweetWithUser, SquidViewHolder<UserTimeLineTweetWithUser>> {

    public UserTimeLineAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, UserTimeLineTweetWithUser.ID);
    }

    @Override
    protected TweetViewHolder<UserTimeLineTweetWithUser> getTweetViewHolder(final View view) {
        return new TweetViewHolder<>(view, mTweetCallback, new UserTimeLineTweetWithUser());
    }

    @Override
    protected LoadingItemHolder<UserTimeLineTweetWithUser> getLoadingItemViewHolder(final View view) {
        return new LoadingItemHolder<>(view, new UserTimeLineTweetWithUser());
    }
}
