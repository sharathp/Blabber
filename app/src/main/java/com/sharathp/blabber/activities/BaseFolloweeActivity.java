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
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityFolloweeBinding;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.repositories.rest.resources.FolloweeListResource;
import com.sharathp.blabber.repositories.rest.resources.FolloweeResource;
import com.sharathp.blabber.util.NetworkUtils;
import com.sharathp.blabber.views.DividerItemDecoration;
import com.sharathp.blabber.views.EndlessRecyclerViewScrollListener;
import com.sharathp.blabber.views.adapters.FolloweeAdapter;
import com.sharathp.blabber.views.adapters.FolloweeCallback;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;

public abstract class BaseFolloweeActivity extends AppCompatActivity implements FolloweeCallback {
    public static final String EXTRA_USER_ID = BaseFolloweeActivity.class.getSimpleName() + ":USER_ID";
    protected static final int DEFAULT_COUNT = 20;

    private static final String TAG = BaseFolloweeActivity.class.getSimpleName();

    @Inject
    protected TwitterClient mTwitterClient;

    @Inject
    Gson mGson;

    private ActivityFolloweeBinding mBinding;
    private LinearLayoutManager mLayoutManager;
    private FolloweeAdapter mFolloweeAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private Long mCursor;

    private Long mUserId;

    protected final static Intent createIntent(final Context context,
                                               final Class<? extends BaseFolloweeActivity> clazz, final long userId) {
        final Intent intent = new Intent(context, clazz);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    private AsyncHttpResponseHandler mPastFolloweeHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
            Log.e(TAG, "Unable to retrieve past followees ids", throwable);
            onPastItemsRetrieved(false, Collections.emptyList(), -1L);
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
            final FolloweeListResource followeeListResource = mGson.fromJson(responseString, FolloweeListResource.class);

            mTwitterClient.getUsers(followeeListResource.getUserIds(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                    Log.e(TAG, "Unable to retrieve past followees", throwable);
                    onPastItemsRetrieved(false, Collections.emptyList(), -1L);
                }

                @Override
                public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                    final Type responseType = new TypeToken<List<FolloweeResource>>() {
                    }.getType();
                    final List<FolloweeResource> followeeResources = mGson.fromJson(responseString, responseType);
                    onPastItemsRetrieved(true, followeeResources, followeeListResource.getNextCursor());
                }
            });
        }
    };

    private AsyncHttpResponseHandler mRefreshFolloweeHandler = new TextHttpResponseHandler() {
        @Override
        public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
            onRefreshed(false, Collections.emptyList(), -1L);
            Log.e(TAG, "Unable to refresh followees ids", throwable);
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
            final FolloweeListResource followeeListResource = mGson.fromJson(responseString, FolloweeListResource.class);

            mTwitterClient.getUsers(followeeListResource.getUserIds(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                    Log.e(TAG, "Unable to refresh followees", throwable);
                    onRefreshed(false, Collections.emptyList(), -1L);
                }

                @Override
                public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                    final Type responseType = new TypeToken<List<FolloweeResource>>() {
                    }.getType();
                    final List<FolloweeResource> followeeResources = mGson.fromJson(responseString, responseType);
                    onRefreshed(true, followeeResources, followeeListResource.getNextCursor());
                }
            });
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_followee);
        mUserId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
        setSupportActionBar(mBinding.toolbar);
        initViews();
    }

    @Override
    protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showLoading();
        refreshFollowees();
    }

    @Override
    public void onFolloweeSelected(Long userId) {
        final Intent intent = UserProfileActivity.createIntent(this, userId);
        startActivity(intent);
    }

    private void initViews() {
        mFolloweeAdapter = new FolloweeAdapter(this);
        final RecyclerView followeeRecyclerView = mBinding.rvFollowees;
        mLayoutManager = new LinearLayoutManager(this);
        followeeRecyclerView.setAdapter(mFolloweeAdapter);
        followeeRecyclerView.setLayoutManager(mLayoutManager);
        followeeRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mBinding.srlFollowees.setOnRefreshListener(() -> {
            mCursor = -1L;
            mFolloweeAdapter.clearAll();
            refreshFollowees();
        });
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                retrievePastFollowees();
            }
        };
        mBinding.rvFollowees.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    private void showLoading() {
        mBinding.rvFollowees.setVisibility(View.GONE);

        mBinding.flMessageContainer.setVisibility(View.VISIBLE);
        mBinding.tvFolloweesMessage.setVisibility(View.GONE);
        mBinding.pbAllFolloweesLoadingBar.setVisibility(View.VISIBLE);
    }

    private void showNoFolloweesMessage() {
        mBinding.rvFollowees.setVisibility(View.GONE);

        mBinding.flMessageContainer.setVisibility(View.VISIBLE);
        mBinding.pbAllFolloweesLoadingBar.setVisibility(View.GONE);
        mBinding.tvFolloweesMessage.setVisibility(View.VISIBLE);
        mBinding.tvFolloweesMessage.setText(getNoFolloweesMessage());
    }

    private void hideMessageContainer() {
        mBinding.flMessageContainer.setVisibility(View.GONE);
        mBinding.tvFolloweesMessage.setVisibility(View.GONE);
        mBinding.pbAllFolloweesLoadingBar.setVisibility(View.GONE);

        mBinding.rvFollowees.setVisibility(View.VISIBLE);
    }

    private void markNoMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(true);
        mFolloweeAdapter.setEndReached();
    }

    private void markMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(false);
        mFolloweeAdapter.clearEndReached();
    }

    private void refreshFollowees() {
        if (! NetworkUtils.isOnline(this)) {
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        doRefreshFollowees(mUserId, mRefreshFolloweeHandler);
    }

    private void retrievePastFollowees() {
        if (! NetworkUtils.isOnline(this)) {
            markNoMoreItemsToLoad();
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        doRetrievePastFollowees(mUserId, mCursor, mPastFolloweeHandler);
    }

    protected void onRefreshed(final boolean success,
                               final List<FolloweeResource> resourceList,
                               final Long cursor) {
        mCursor = cursor;
        mBinding.srlFollowees.setRefreshing(false);

        if (! success) {
            Toast.makeText(this, R.string.message_refresh_followees_failed, Toast.LENGTH_SHORT).show();
            showNoFolloweesMessage();
            markNoMoreItemsToLoad();
            return;
        }

        if (resourceList.isEmpty()) {
            // show no tweets message
            showNoFolloweesMessage();
            markNoMoreItemsToLoad();
        } else {
            hideMessageContainer();

            if (mCursor != null && mCursor > 0) {
                markMoreItemsToLoad();
            } else {
                markNoMoreItemsToLoad();
            }
            mFolloweeAdapter.addItems(resourceList);
        }
    }

    protected void onPastItemsRetrieved(final boolean success,
                                        final List<FolloweeResource> resourceList,
                                        final Long cursor) {
        mCursor = cursor;

        if (success) {
            if (resourceList.isEmpty()) {
                markNoMoreItemsToLoad();
            } else {
                mFolloweeAdapter.addItems(resourceList);
            }
        } else {
            markNoMoreItemsToLoad();
            Toast.makeText(this, R.string.message_past_followees_failed, Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract void doRetrievePastFollowees(Long userId, Long nextCursor, AsyncHttpResponseHandler handler);

    protected abstract void doRefreshFollowees(Long userId, AsyncHttpResponseHandler handler);

    protected abstract String getNoFolloweesMessage();
}