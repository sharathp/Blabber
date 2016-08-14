package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivitySearchBinding;
import com.sharathp.blabber.events.SearchResultsPastEvent;
import com.sharathp.blabber.events.SearchResultsRefreshEvent;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.models.SearchWithUser;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.NetworkUtils;
import com.sharathp.blabber.util.PermissionUtils;
import com.sharathp.blabber.views.DividerItemDecoration;
import com.sharathp.blabber.views.EndlessRecyclerViewScrollListener;
import com.sharathp.blabber.views.adapters.SearchAdapter;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.yahoo.squidb.data.SquidCursor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<SquidCursor<SearchWithUser>>, TweetCallback,
        ComposeFragment.ComposeCallback {
    private static final int TWEET_ITEM_LOADER_ID = 0;
    public static final String EXTRA_QUERY = SearchActivity.class.getSimpleName() + ":QUERY";

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    private TweetCallback mCallback;
    private ActivitySearchBinding mBinding;
    private LinearLayoutManager mLayoutManager;
    private SearchAdapter mSearchAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;
    private Long mMax;
    private String mQuery;

    public static Intent createIntent(final Context context, final String query) {
        final Intent intent = new Intent(context, SearchActivity.class);
        if (! TextUtils.isEmpty(query)) {
            intent.putExtra(EXTRA_QUERY, query);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mQuery = getIntent().getStringExtra(EXTRA_QUERY);
        initViews();
        if (! TextUtils.isEmpty(mQuery)) {
            search();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        mEventBus.unregister(this);
        super.onStop();
    }

    private void initViews() {
        mSearchAdapter = new SearchAdapter(mCallback);
        final RecyclerView moviesRecyclerView = mBinding.rvTweets;
        mLayoutManager = new LinearLayoutManager(this);
        moviesRecyclerView.setAdapter(mSearchAdapter);
        moviesRecyclerView.setLayoutManager(mLayoutManager);
        moviesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mBinding.srlTweets.setOnRefreshListener(() -> refreshSearchResults());
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                retrievePastSearchResults();
            }
        };
        mBinding.rvTweets.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    private void search() {
        if (! NetworkUtils.isOnline(this)) {
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        mMax = 0L;
        getSupportLoaderManager().destroyLoader(TWEET_ITEM_LOADER_ID);
        getSupportLoaderManager().restartLoader(TWEET_ITEM_LOADER_ID, null, this);

        final Intent intent = null;
        startService(intent);
        markMoreItemsToLoad();
    }

    private void refreshSearchResults() {
        if (! NetworkUtils.isOnline(this)) {
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            mBinding.srlTweets.setRefreshing(false);
            return;
        }

        search();
    }

    private void retrievePastSearchResults() {
        if (! NetworkUtils.isOnline(this)) {
            markNoMoreItemsToLoad();
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO - kick-off an intent to force clear database and start fresh
        final Intent intent = null;
        startService(intent);
    }

    @Override
    public Loader<SquidCursor<SearchWithUser>> onCreateLoader(final int id, final Bundle args) {
        return mTwitterDAO.getSearches(mQuery);
    }

    @Override
    public void onLoadFinished(final Loader<SquidCursor<SearchWithUser>> loader, SquidCursor<SearchWithUser> data) {
        if (data.getCount() > 0) {
            // hide no tweets message
            hideMessageContainer();
            markMoreItemsToLoad();
        } else {
            // show no tweets message
            showNoTweetsMessage();
            markNoMoreItemsToLoad();
        }
        mSearchAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<SquidCursor<SearchWithUser>> loader) {
        mSearchAdapter.swapCursor(null);
    }

    @Override
    public void onTweetSelected(final ITweetWithUser tweet) {
        if (!PermissionUtils.requestWritePermissions(this)) {
            return;
        }

        final Intent intent = TweetDetailActivity.createIntent(this, tweet);
        startActivity(intent);
    }

    @Override
    public void onTweetSubmitted(final String tweet) {
        // no-op
    }

    @Override
    public void onTweetReplied(final ITweetWithUser tweet) {
        openCompose(tweet);
    }

    @Override
    public void onFavorited(final ITweetWithUser tweet) {
        final Intent intent = UpdateTimelineService.createIntentForFavorite(this, tweet.getId());
        startService(intent);
    }

    @Override
    public void onUnfavorited(final ITweetWithUser tweet) {
        final Intent intent = UpdateTimelineService.createIntentForUnFavorite(this, tweet.getId());
        startService(intent);
    }

    @Override
    public void onRetweeted(final ITweetWithUser tweet) {
        final Intent intent = UpdateTimelineService.createIntentForRetweet(this, tweet.getId());
        startService(intent);
    }

    @Override
    public void onUnRetweeted(final ITweetWithUser tweet) {
        final Intent intent = UpdateTimelineService.createIntentForUnRetweet(this, tweet.getId());
        startService(intent);
    }

    @Override
    public void onProfileImageSelected(final ITweetWithUser tweet) {
        long userId = tweet.getUserId();

        if (tweet.getRetweetedUserId() != null && tweet.getRetweetedUserId() > 1) {
            userId = tweet.getRetweetedUserId();
        }

        final Intent intent = UserProfileActivity.createIntent(this, userId);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final SearchResultsRefreshEvent event) {
        mBinding.srlTweets.setRefreshing(false);
        if (! event.isSuccess()) {
            Toast.makeText(this, R.string.message_search_refresh_failed, Toast.LENGTH_SHORT).show();
        }
        mMax = event.getMaxId();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final SearchResultsPastEvent event) {
        if (event.isSuccess()) {
            if (event.getTweetsCount() == 0) {
                markNoMoreItemsToLoad();
            }
        } else {
            markNoMoreItemsToLoad();
            Toast.makeText(this, R.string.message_past_tweets_failed, Toast.LENGTH_SHORT).show();
        }
        mMax = event.getMaxId();
    }

    private void openCompose(final ITweetWithUser tweetWithUser) {
        final FragmentManager fm = getSupportFragmentManager();
        final ComposeFragment composeFragment = ComposeFragment.createInstance(tweetWithUser);
        composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
        composeFragment.show(fm, "compose_fragment");
        composeFragment.setCallback(this);
    }

    private void showLoading() {
        mBinding.rvTweets.setVisibility(View.INVISIBLE);

        mBinding.flMessageContainer.setVisibility(View.VISIBLE);
        mBinding.tvTweetsMessage.setVisibility(View.INVISIBLE);
        mBinding.pbAllTweetsLoadingBar.setVisibility(View.VISIBLE);
    }

    private void showNoTweetsMessage() {
        mBinding.rvTweets.setVisibility(View.INVISIBLE);

        mBinding.flMessageContainer.setVisibility(View.VISIBLE);
        mBinding.pbAllTweetsLoadingBar.setVisibility(View.INVISIBLE);
        mBinding.tvTweetsMessage.setVisibility(View.VISIBLE);
    }

    private void hideMessageContainer() {
        mBinding.flMessageContainer.setVisibility(View.INVISIBLE);
        mBinding.tvTweetsMessage.setVisibility(View.INVISIBLE);
        mBinding.pbAllTweetsLoadingBar.setVisibility(View.INVISIBLE);

        mBinding.rvTweets.setVisibility(View.VISIBLE);
    }

    private void markNoMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(true);
        mSearchAdapter.setEndReached();
    }

    private void markMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(false);
        mSearchAdapter.clearEndReached();
    }
}