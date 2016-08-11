package com.sharathp.blabber.views.adapters.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharathp.blabber.R;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;
import com.yahoo.squidb.data.AbstractModel;
import com.yahoo.squidb.recyclerview.SquidRecyclerAdapter;
import com.yahoo.squidb.recyclerview.SquidViewHolder;
import com.yahoo.squidb.sql.Property;

import java.lang.ref.WeakReference;

public abstract class BaseTweetsAdapter<M extends AbstractModel & ITweetWithUser, V extends SquidViewHolder<M>> extends SquidRecyclerAdapter<M, V> {
    private static final int TYPE_TWEET = 0;
    private static final int TYPE_LOADING = 1;

    protected final TweetCallback mTweetCallback;
    private boolean mIsEndReached;

    // weak reference to not avoid garbage collection the instance..
    private WeakReference<LoadingItemHolder<M>> mLoadingItemHolder;

    public BaseTweetsAdapter(final TweetCallback tweetCallback, final Property<Long> idProperty) {
        super(idProperty);
        mTweetCallback = tweetCallback;
    }

    @Override
    public int getItemViewType(final int position) {
        if (isPositionForLoadingIndicator(position)) {
            return TYPE_LOADING;
        }

        return TYPE_TWEET;
    }

    @Override
    public V onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();

        final LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_TWEET: {
                final View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
                return getTweetViewHolder(tweetView);
            }
            case TYPE_LOADING: {
                final View view = inflater.inflate(R.layout.view_tweets_loading, parent, false);
                return getLoadingItemViewHolder(view);
            }
            default: {
                throw new IllegalArgumentException("Invalid viewType: " + viewType);
            }
        }
    }

    @Override
    public void onBindSquidViewHolder(final V holder, final int position) {
        // if we are here, it should be always TweetViewHolder
        ((TweetViewHolder)holder).bindTweet();
    }

    // over-ridden to "handle" the LoadingItemHolder, if not simply calls the super class
    @Override
    public void onBindViewHolder(final V holder, final int position) {
        // this is refresh indicator, so, do nothing
        if (isPositionForLoadingIndicator(position)) {
            // maintain a weak reference to holder to handle the edge case where mEndOfFeedReached is set
            // after the view is bound here
            mLoadingItemHolder = new WeakReference<>((LoadingItemHolder)holder);
            // bind it here since the cursor is already exhausted at this point and super class would complain about it..
            if (mIsEndReached) {
                ((LoadingItemHolder)holder).showEndOfFeedReached();
            }
            return;
        }

        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        final int actualCount = super.getItemCount();
        if (actualCount == 0) {
            return actualCount;
        }
        // last item is for the loading indicator or end-of-feed indicator
        return actualCount + 1;
    }

    public void setEndReached() {
        mIsEndReached = true;
        final LoadingItemHolder loadingItemHolder = getLoadingItemHolder();
        if (loadingItemHolder != null) {
            loadingItemHolder.showEndOfFeedReached();
        }
    }

    public void clearEndReached() {
        mIsEndReached = false;
        final LoadingItemHolder loadingItemHolder = getLoadingItemHolder();
        if (loadingItemHolder != null) {
            loadingItemHolder.hideEndOfFeedReached();
        }
    }

    private LoadingItemHolder getLoadingItemHolder() {
        if (mLoadingItemHolder == null) {
            return null;
        }
        return mLoadingItemHolder.get();
    }

    private boolean isPositionForLoadingIndicator(final int position) {
        return (position == super.getItemCount());
    }

    protected abstract V getTweetViewHolder(final View view);

    protected abstract V getLoadingItemViewHolder(final View view);
}
