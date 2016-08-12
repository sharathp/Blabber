package com.sharathp.blabber.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.FragmentTimelineBinding;
import com.sharathp.blabber.events.TweetsPastRetrievedEvent;
import com.sharathp.blabber.events.TweetsRefreshedEvent;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.NetworkUtils;
import com.sharathp.blabber.views.DividerItemDecoration;
import com.sharathp.blabber.views.EndlessRecyclerViewScrollListener;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.sharathp.blabber.views.adapters.TweetsAdapter;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Order;
import com.yahoo.squidb.sql.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class HomeTimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<TweetWithUser>> {
    private static final int TWEET_ITEM_LOADER_ID = 0;

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    private TweetCallback mCallback;

    private FragmentTimelineBinding mBinding;
    private LinearLayoutManager mLayoutManager;
    private TweetsAdapter mTweetsAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    public static HomeTimelineFragment createInstance() {
        return new HomeTimelineFragment();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (! (context instanceof TweetCallback)) {
            throw new IllegalArgumentException("context must implement: " + TweetCallback.class.getName());
        }
        mCallback = (TweetCallback) context;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlabberApplication.from(getActivity()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoading();
        refreshTweets();
        getLoaderManager().initLoader(TWEET_ITEM_LOADER_ID, null, this);
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

    @Override
    public Loader<SquidCursor<TweetWithUser>> onCreateLoader(final int id, final Bundle args) {
        final Query query = Query.select(TweetWithUser.PROPERTIES)
                .orderBy(Order.desc(TweetWithUser.CREATED_AT));
        return mTwitterDAO.getTweets(query);
    }

    @Override
    public void onLoadFinished(final Loader<SquidCursor<TweetWithUser>> loader, final SquidCursor<TweetWithUser> data) {
        if (data.getCount() > 0) {
            // hide no tweets message
            hideMessageContainer();
            markMoreItemsToLoad();
        } else {
            // show no tweets message
            showNoTweetsMessage();
            markNoMoreItemsToLoad();
        }
        mTweetsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<SquidCursor<TweetWithUser>> loader) {
        mTweetsAdapter.swapCursor(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final TweetsRefreshedEvent event) {
        mBinding.srlTweets.setRefreshing(false);
        if (! event.isSuccess()) {
            Toast.makeText(getActivity(), R.string.message_refresh_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final TweetsPastRetrievedEvent event) {
        if (event.isSuccess()) {
            if (event.getTweetsCount() == 0) {
                markNoMoreItemsToLoad();
            }
        } else {
            markNoMoreItemsToLoad();
            Toast.makeText(getActivity(), R.string.message_past_tweets_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        mTweetsAdapter = new TweetsAdapter(mCallback);
        final RecyclerView moviesRecyclerView = mBinding.rvTweets;
        mLayoutManager = new LinearLayoutManager(getActivity());
        moviesRecyclerView.setAdapter(mTweetsAdapter);
        moviesRecyclerView.setLayoutManager(mLayoutManager);
        moviesRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mBinding.srlTweets.setOnRefreshListener(() -> deleteAndRefreshTweets());
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                retrievePastTweets();
            }
        };
        mBinding.rvTweets.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    private void deleteAndRefreshTweets() {
        if (! NetworkUtils.isOnline(getContext())) {
            Toast.makeText(getActivity(), R.string.message_no_internet, Toast.LENGTH_LONG).show();
            mBinding.srlTweets.setRefreshing(false);
            return;
        }

        final Intent intent = UpdateTimelineService.createIntentForDeleteExistingItemsAndRetrieveLasterItems(getActivity());
        getActivity().startService(intent);
        markMoreItemsToLoad();
    }

    private void refreshTweets() {
        if (! NetworkUtils.isOnline(getContext())) {
            Toast.makeText(getActivity(), R.string.message_no_internet, Toast.LENGTH_LONG).show();
            mBinding.srlTweets.setRefreshing(false);
            return;
        }

        final Intent intent = UpdateTimelineService.createIntentForLatestItems(getActivity());
        getActivity().startService(intent);
        markMoreItemsToLoad();
    }

    private void retrievePastTweets() {
        if (! NetworkUtils.isOnline(getContext())) {
            markNoMoreItemsToLoad();
            Toast.makeText(getActivity(), R.string.message_no_internet, Toast.LENGTH_LONG).show();
            return;
        }

        final Intent intent = UpdateTimelineService.createIntentForPastItems(getActivity());
        getActivity().startService(intent);
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
        mTweetsAdapter.setEndReached();
    }

    private void markMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(false);
        mTweetsAdapter.clearEndReached();
    }
}