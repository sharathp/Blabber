package com.sharathp.blabber.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharathp.blabber.R;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.views.adapters.viewholders.LoadingItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.TweetViewHolder;

import java.lang.ref.WeakReference;
import java.util.List;

public class TweetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TWEET = 0;
    private static final int TYPE_LOADING = 1;

    private boolean mIsEndReached;

    // weak reference to not avoid garbage collection the instance..
    private WeakReference<LoadingItemHolder> mLoadingItemHolder;

    private List<Tweet> mTweets;
    private final TweetCallback mTweetCallback;

    public TweetListAdapter(final List<Tweet> tweets, final TweetCallback tweetCallback) {
        mTweets = tweets;
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
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();

        final LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_TWEET: {
                final View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
                return new TweetViewHolder(tweetView, mTweetCallback);
            }
            case TYPE_LOADING: {
                final View view = inflater.inflate(R.layout.view_articles_loading, parent, false);
                return new LoadingItemHolder(view);
            }
            default: {
                throw new IllegalArgumentException("Invalid viewType: " + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // this is loading indicator, so, do nothing
        if (isPositionForLoadingIndicator(position)) {
            // maintain a reference to holder to handle the case where mEndOfFeedReached is set
            // after the view is bound here
            mLoadingItemHolder = new WeakReference<>((LoadingItemHolder)holder);
            // bind it here since the cursor is already exhausted at this point and super class would complain about it..
            if (mIsEndReached) {
                ((LoadingItemHolder)holder).showEndOfFeedReached();
            }
            return;
        }

        final Tweet tweet = mTweets.get(position);
        ((TweetViewHolder)holder).bind(tweet);
    }

    @Override
    public int getItemCount() {
        if (mTweets == null || mTweets.isEmpty()) {
            return 0;
        }

        // to show the spinner
        return mTweets.size() + 1;
    }

    private boolean isPositionForLoadingIndicator(final int position) {
        // last item is the loading indicator
        return (position == mTweets.size());
    }

    public void setArticles(final List<Tweet> tweets) {
        if (tweets != null) {
            mTweets = tweets;
        } else {
            tweets.clear();
        }
        notifyDataSetChanged();
    }

    public void addTweets(final List<Tweet> tweets) {
        mTweets.addAll(tweets);
        notifyDataSetChanged();
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
}