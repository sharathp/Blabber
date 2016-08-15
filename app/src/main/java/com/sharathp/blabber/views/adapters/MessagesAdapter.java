package com.sharathp.blabber.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sharathp.blabber.R;
import com.sharathp.blabber.repositories.rest.resources.MessageResource;
import com.sharathp.blabber.views.adapters.viewholders.MessageItemHolder;
import com.sharathp.blabber.views.adapters.viewholders.MessageLoadingItemHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_LOADING = 1;

    private final List<MessageResource> mItems = new ArrayList<>();

    private final MessageCallback mMessageCallback;
    private final long mLoggedInUserId;

    private boolean mIsEndReached;

    // weak reference to not avoid garbage collection the instance..
    private WeakReference<MessageLoadingItemHolder> mLoadingItemHolder;

    public MessagesAdapter(final MessageCallback messageCallback, final long loggedInUserId) {
        mMessageCallback = messageCallback;
        mLoggedInUserId = loggedInUserId;
    }

    @Override
    public int getItemViewType(final int position) {
        if (isPositionForLoadingIndicator(position)) {
            return TYPE_LOADING;
        }

        return TYPE_MESSAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_MESSAGE: {
                final View view = inflater.inflate(R.layout.item_message, parent, false);
                return new MessageItemHolder(view, mMessageCallback, mLoggedInUserId);
            }
            case TYPE_LOADING: {
                final View view = inflater.inflate(R.layout.view_message_loading, parent, false);
                return new MessageLoadingItemHolder(view);
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
            mLoadingItemHolder = new WeakReference<>((MessageLoadingItemHolder) holder);
            // bind it here since the cursor is already exhausted at this point and super class would complain about it..
            if (mIsEndReached) {
                ((MessageLoadingItemHolder)holder).showEndOfFeedReached();
            }
            return;
        }

        ((MessageItemHolder)holder).bind(mItems.get(position));
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
        final MessageLoadingItemHolder loadingItemHolder = getLoadingItemHolder();
        if (loadingItemHolder != null) {
            loadingItemHolder.showEndOfFeedReached();
        }
    }

    public void clearEndReached() {
        mIsEndReached = false;
        final MessageLoadingItemHolder loadingItemHolder = getLoadingItemHolder();
        if (loadingItemHolder != null) {
            loadingItemHolder.hideEndOfFeedReached();
        }
    }

    private MessageLoadingItemHolder getLoadingItemHolder() {
        if (mLoadingItemHolder == null) {
            return null;
        }
        return mLoadingItemHolder.get();
    }

    private boolean isPositionForLoadingIndicator(final int position) {
        return (position == mItems.size());
    }

    public void addItems(final List<MessageResource> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mItems.clear();
        notifyDataSetChanged();
    }
}
