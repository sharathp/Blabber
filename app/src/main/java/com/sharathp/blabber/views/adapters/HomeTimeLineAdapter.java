package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.HomeTimelineWithUser;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class HomeTimeLineAdapter extends BaseTweetsAdapter<HomeTimelineWithUser, SquidViewHolder<HomeTimelineWithUser>> {

    public HomeTimeLineAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, TweetWithUser.ID);
    }

    @Override
    protected TweetViewHolder<HomeTimelineWithUser> getTweetViewHolder(final View view) {
        return new TweetViewHolder<>(view, mTweetCallback, new HomeTimelineWithUser());
    }

    @Override
    protected LoadingItemHolder<HomeTimelineWithUser> getLoadingItemViewHolder(final View view) {
        return new LoadingItemHolder<>(view, new HomeTimelineWithUser());
    }
}
