package com.sharathp.blabber.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharathp.blabber.R;
import com.sharathp.blabber.repositories.rest.resources.FolloweeResource;
import com.sharathp.blabber.views.adapters.viewholders.FolloweeItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.FolloweeLoadingItemHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FolloweeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOLLOWEE = 0;
    private static final int TYPE_LOADING = 1;

    private final List<FolloweeResource> mItems = new ArrayList<>();

    private final FolloweeCallback mFolloweeCallback;
    private boolean mIsEndReached;

    // weak reference to not avoid garbage collection the instance..
    private WeakReference<FolloweeLoadingItemHolder> mLoadingItemHolder;

    public FolloweeAdapter(final FolloweeCallback followeeCallback) {
        mFolloweeCallback = followeeCallback;
    }

    @Override
    public int getItemViewType(final int position) {
        if (isPositionForLoadingIndicator(position)) {
            return TYPE_LOADING;
        }

        return TYPE_FOLLOWEE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_FOLLOWEE: {
                final View view = inflater.inflate(R.layout.item_followee, parent, false);
                return new FolloweeItemHolder(view, mFolloweeCallback);
            }
            case TYPE_LOADING: {
                final View view = inflater.inflate(R.layout.view_followee_loading, parent, false);
                return new FolloweeLoadingItemHolder(view);
            }
            default: {
                throw new IllegalArgumentException("Invalid viewType: " + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // this is refresh indicator, so, do nothing
        if (isPositionForLoadingIndicator(position)) {
            // maintain a weak reference to holder to handle the edge case where mEndOfFeedReached is set
            // after the view is bound here
            mLoadingItemHolder = new WeakReference<>((FolloweeLoadingItemHolder) holder);
            // bind it here since the cursor is already exhausted at this point and super class would complain about it..
            if (mIsEndReached) {
                ((FolloweeLoadingItemHolder)holder).showEndOfFeedReached();
            }
            return;
        }

        ((FolloweeItemHolder)holder).bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        if (mItems.isEmpty()) {
            return 0;
        }

        // last item is for the loading indicator or end-of-feed indicator
        return mItems.size() + 1;
    }

    public void setEndReached() {
        mIsEndReached = true;
        final FolloweeLoadingItemHolder loadingItemHolder = getLoadingItemHolder();
        if (loadingItemHolder != null) {
            loadingItemHolder.showEndOfFeedReached();
        }
    }

    public void clearEndReached() {
        mIsEndReached = false;
        final FolloweeLoadingItemHolder loadingItemHolder = getLoadingItemHolder();
        if (loadingItemHolder != null) {
            loadingItemHolder.hideEndOfFeedReached();
        }
    }

    private FolloweeLoadingItemHolder getLoadingItemHolder() {
        if (mLoadingItemHolder == null) {
            return null;
        }
        return mLoadingItemHolder.get();
    }

    private boolean isPositionForLoadingIndicator(final int position) {
        return (position == mItems.size());
    }

    public void addItems(final List<FolloweeResource> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mItems.clear();
        notifyDataSetChanged();
    }
}
