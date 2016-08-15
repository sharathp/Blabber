package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityDirectMessagesBinding;
import com.sharathp.blabber.repositories.LocalPreferencesDAO;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.repositories.rest.resources.MessageResource;
import com.sharathp.blabber.util.NetworkUtils;
import com.sharathp.blabber.views.DividerItemDecoration;
import com.sharathp.blabber.views.EndlessRecyclerViewScrollListener;
import com.sharathp.blabber.views.adapters.MessageCallback;
import com.sharathp.blabber.views.adapters.MessagesAdapter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;

public class DirectMessagesActivity extends AppCompatActivity implements MessageCallback {
    private static final String TAG = DirectMessagesActivity.class.getSimpleName();
    protected static final int DEFAULT_COUNT = 20;

    @Inject
    protected TwitterClient mTwitterClient;

    @Inject
    Gson mGson;

    @Inject
    LocalPreferencesDAO mLocalPreferencesDAO;

    private ActivityDirectMessagesBinding mBinding;
    private LinearLayoutManager mLayoutManager;
    private MessagesAdapter mMessagesAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private Long mMax;

    private AsyncHttpResponseHandler mPastMessagesHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
            Log.e(TAG, "Unable to retrieve past messages", throwable);
            onPastItemsRetrieved(false, Collections.emptyList(), -1L);
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
            final Type responseType = new TypeToken<List<MessageResource>>() {
            }.getType();
            final List<MessageResource> messageResources = mGson.fromJson(responseString, responseType);
            Long maxId = -1L;
            if (messageResources.size() > 0) {
                maxId = messageResources.get(messageResources.size() - 1).getId();
            }
            onPastItemsRetrieved(true, messageResources, maxId);
        }
    };

    private AsyncHttpResponseHandler mRefreshMessagesHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
            onRefreshed(false, Collections.emptyList(), -1L);
            Log.e(TAG, "Unable to refresh messages", throwable);
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
            final Type responseType = new TypeToken<List<MessageResource>>() {
            }.getType();
            final List<MessageResource> messageResources = mGson.fromJson(responseString, responseType);
            Long maxId = -1L;
            if (messageResources.size() > 0) {
                maxId = messageResources.get(messageResources.size() - 1).getId();
            }
            onRefreshed(true, messageResources, maxId);
        }
    };


    public static Intent createIntent(final Context context) {
        final Intent intent = new Intent(context, DirectMessagesActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_direct_messages);

        BlabberApplication.from(this).getComponent().inject(this);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_direct_messages);
        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        initViews();
        showLoading();
        refreshMessages();
    }

    private void initViews() {
        mMessagesAdapter = new MessagesAdapter(this, mLocalPreferencesDAO.getUserId());
        final RecyclerView messageRecyclerView = mBinding.rvMessages;
        mLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setAdapter(mMessagesAdapter);
        messageRecyclerView.setLayoutManager(mLayoutManager);
        messageRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mBinding.srlMessages.setOnRefreshListener(() -> {
            mMax = -1L;
            mMessagesAdapter.clearAll();
            refreshMessages();
        });
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                retrievePastMessages();
            }
        };
        mBinding.rvMessages.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    private void showLoading() {
        mBinding.rvMessages.setVisibility(View.GONE);

        mBinding.flMessageContainer.setVisibility(View.VISIBLE);
        mBinding.tvMessagesMessage.setVisibility(View.GONE);
        mBinding.pbAllMessagesLoadingBar.setVisibility(View.VISIBLE);
    }

    private void showNoMessages() {
        mBinding.rvMessages.setVisibility(View.GONE);

        mBinding.flMessageContainer.setVisibility(View.VISIBLE);
        mBinding.pbAllMessagesLoadingBar.setVisibility(View.GONE);
        mBinding.tvMessagesMessage.setVisibility(View.VISIBLE);
        mBinding.tvMessagesMessage.setText(R.string.message_no_messages);
    }

    private void hideMessageContainer() {
        mBinding.flMessageContainer.setVisibility(View.GONE);
        mBinding.tvMessagesMessage.setVisibility(View.GONE);
        mBinding.pbAllMessagesLoadingBar.setVisibility(View.GONE);

        mBinding.rvMessages.setVisibility(View.VISIBLE);
    }

    private void markNoMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(true);
        mMessagesAdapter.setEndReached();
    }

    private void markMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(false);
        mMessagesAdapter.clearEndReached();
    }

    private void refreshMessages() {
        if (! NetworkUtils.isOnline(this)) {
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        mTwitterClient.getDirectMessages(DEFAULT_COUNT, -1L, mRefreshMessagesHandler);
    }

    private void retrievePastMessages() {
        if (! NetworkUtils.isOnline(this)) {
            markNoMoreItemsToLoad();
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        mTwitterClient.getDirectMessages(DEFAULT_COUNT, mMax - 1, mPastMessagesHandler);
    }

    protected void onRefreshed(final boolean success,
                               final List<MessageResource> resourceList,
                               final Long max) {
        mMax = max;
        mBinding.srlMessages.setRefreshing(false);

        if (! success) {
            Toast.makeText(this, R.string.message_refresh_messages_failed, Toast.LENGTH_SHORT).show();
            showNoMessages();
            markNoMoreItemsToLoad();
            return;
        }

        if (resourceList.isEmpty()) {
            // show no tweets message
            showNoMessages();
            markNoMoreItemsToLoad();
        } else {
            hideMessageContainer();

            if (mMax != null && mMax > 0) {
                markMoreItemsToLoad();
            } else {
                markNoMoreItemsToLoad();
            }
            mMessagesAdapter.addItems(resourceList);
        }
    }

    protected void onPastItemsRetrieved(final boolean success,
                                        final List<MessageResource> resourceList,
                                        final Long max) {
        mMax = max;

        if (success) {
            if (resourceList.isEmpty()) {
                markNoMoreItemsToLoad();
            } else {
                mMessagesAdapter.addItems(resourceList);
            }
        } else {
            markNoMoreItemsToLoad();
            Toast.makeText(this, R.string.message_past_messages_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageSelected(final MessageResource messageResource) {
        Toast.makeText(this, "Message Selected", Toast.LENGTH_SHORT).show();
    }
}
