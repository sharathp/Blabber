package com.sharathp.blabber.fragments;

import android.content.Context;
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

import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.FragmentTimelineBinding;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.views.EndlessRecyclerViewScrollListener;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.sharathp.blabber.views.adapters.TweetsAdapter;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Order;
import com.yahoo.squidb.sql.Query;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<SquidCursor<TweetWithUser>> {
    private static final int TWEET_ITEM_LOADER_ID = 0;
    private static final int VISIBLE_THRESHOLD = 5;

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    private TweetCallback mCallback;

    private FragmentTimelineBinding mBinding;
    private TweetsAdapter mTweetsAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (!(context instanceof Callback)) {
            throw new IllegalArgumentException("context must implement: " + Callback.class.getName());
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
        // TODO - show no tweets if no rows are retrieved
        mTweetsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<SquidCursor<TweetWithUser>> loader) {
        mTweetsAdapter.swapCursor(null);
    }

    private void initViews() {
        mTweetsAdapter = new TweetsAdapter(mCallback);
        final RecyclerView moviesRecyclerView = mBinding.rvTweets;
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        moviesRecyclerView.setAdapter(mTweetsAdapter);
        moviesRecyclerView.setLayoutManager(layoutManager);
        mBinding.srlTweets.setOnRefreshListener(() -> refreshTweets());
    }

    private void refreshTweets() {
        // TODO - implement refreshing tweets
    }

    /**
     * Required interface for hosting activities.
     */
    public interface Callback {
        void onTweetSelected(TweetWithUser tweetWithUser);
    }
}