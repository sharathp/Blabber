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
import com.sharathp.blabber.events.HomeTimelineLatestEvent;
import com.sharathp.blabber.events.HomeTimelinePastEvent;
import com.sharathp.blabber.models.HomeTimelineWithUser;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.NetworkUtils;
import com.sharathp.blabber.views.DividerItemDecoration;
import com.sharathp.blabber.views.EndlessRecyclerViewScrollListener;
import com.sharathp.blabber.views.adapters.HomeTimeLineAdapter;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.yahoo.squidb.data.SquidCursor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class HomeTimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<HomeTimelineWithUser>> {
    private static final int TWEET_ITEM_LOADER_ID = 0;

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    private TweetCallback mCallback;

    private FragmentTimelineBinding mBinding;
    private LinearLayoutManager mLayoutManager;
    private HomeTimeLineAdapter mHomeTimeLineAdapter;
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
        refreshHomeTimeline();
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
    public Loader<SquidCursor<HomeTimelineWithUser>> onCreateLoader(final int id, final Bundle args) {
        return mTwitterDAO.getHomeTimeline();
    }

    @Override
    public void onLoadFinished(final Loader<SquidCursor<HomeTimelineWithUser>> loader, final SquidCursor<HomeTimelineWithUser> data) {
        if (data.getCount() > 0) {
            // hide no tweets message
            hideMessageContainer();
            markMoreItemsToLoad();
        } else {
            // show no tweets message
            showNoTweetsMessage();
            markNoMoreItemsToLoad();
        }
        mHomeTimeLineAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<SquidCursor<HomeTimelineWithUser>> loader) {
        mHomeTimeLineAdapter.swapCursor(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final HomeTimelineLatestEvent event) {
        mBinding.srlTweets.setRefreshing(false);
        if (! event.isSuccess()) {
            Toast.makeText(getActivity(), R.string.message_refresh_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final HomeTimelinePastEvent event) {
        if (event.isSuccess()) {
            if (event.getTweetsCount() == 0) {
                markNoMoreItemsToLoad();
            }
        } else {
            markNoMoreItemsToLoad();
            Toast.makeText(getActivity(), R.string.message_past_tweets_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        mHomeTimeLineAdapter = new HomeTimeLineAdapter(mCallback);
        final RecyclerView tweetsRecyclerView = mBinding.rvTweets;
        mLayoutManager = new LinearLayoutManager(getActivity());
        tweetsRecyclerView.setAdapter(mHomeTimeLineAdapter);
        tweetsRecyclerView.setLayoutManager(mLayoutManager);
        tweetsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mBinding.srlTweets.setOnRefreshListener(() -> refreshHomeTimeline());
        mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(final int page, final int totalItemsCount) {
                retrievePastTweets();
            }
        };
        mBinding.rvTweets.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    private void refreshHomeTimeline() {
        if (! NetworkUtils.isOnline(getContext())) {
            Toast.makeText(getActivity(), R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            mBinding.srlTweets.setRefreshing(false);
            return;
        }

        final Intent intent = UpdateTimelineService.createIntentForLatestHomeTimeline(getActivity());
        getActivity().startService(intent);
        markMoreItemsToLoad();
    }

    private void retrievePastTweets() {
        if (! NetworkUtils.isOnline(getContext())) {
            markNoMoreItemsToLoad();
            Toast.makeText(getActivity(), R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        final Intent intent = UpdateTimelineService.createIntentForPastHomeTimeline(getActivity());
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
        mHomeTimeLineAdapter.setEndReached();
    }

    private void markMoreItemsToLoad() {
        mEndlessRecyclerViewScrollListener.setEndReached(false);
        mHomeTimeLineAdapter.clearEndReached();
    }
}