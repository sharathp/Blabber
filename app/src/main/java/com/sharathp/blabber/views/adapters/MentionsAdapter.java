package com.sharathp.blabber.views.adapters;

import android.view.View;

import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.views.adapters.base.BaseTweetsAdapter;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class MentionsAdapter extends BaseTweetsAdapter<MentionsWithUser, SquidViewHolder<MentionsWithUser>> {

    public MentionsAdapter(final TweetCallback tweetCallback) {
        super(tweetCallback, TweetWithUser.ID);
    }

    @Override
    protected TweetViewHolder<MentionsWithUser> getTweetViewHolder(final View view) {
        return new TweetViewHolder<>(view, mTweetCallback, new MentionsWithUser());
    }

    @Override
    protected LoadingItemHolder<MentionsWithUser> getLoadingItemViewHolder(final View view) {
        return new LoadingItemHolder<>(view, new MentionsWithUser());
    }
}
