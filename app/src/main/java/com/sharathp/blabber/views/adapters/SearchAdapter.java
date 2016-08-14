package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.SearchWithUser;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class SearchAdapter extends BaseTweetsAdapter<SearchWithUser, SquidViewHolder<SearchWithUser>> {

    public SearchAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, SearchWithUser.ID);
    }

    @Override
    protected TweetViewHolder<SearchWithUser> getTweetViewHolder(final View view) {
        return new TweetViewHolder<>(view, mTweetCallback, new SearchWithUser());
    }

    @Override
    protected LoadingItemHolder<SearchWithUser> getLoadingItemViewHolder(final View view) {
        return new LoadingItemHolder<>(view, new SearchWithUser());
    }
}
