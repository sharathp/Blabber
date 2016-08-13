package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityUserProfileBinding;
import com.sharathp.blabber.events.UserProfileRetrieved;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.fragments.LikesFragment;
import com.sharathp.blabber.fragments.MediaFragment;
import com.sharathp.blabber.fragments.UserTimelineFragment;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.util.NetworkUtils;
import com.sharathp.blabber.util.PermissionUtils;
import com.sharathp.blabber.util.ViewUtils;
import com.sharathp.blabber.views.adapters.TweetCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class UserProfileActivity extends AppCompatActivity implements TweetCallback,
        ComposeFragment.ComposeCallback, AppBarLayout.OnOffsetChangedListener {
    private static final String EXTRA_USER_ID = UserProfileActivity.class.getSimpleName() + ":USER_ID";
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 30;

    @Inject
    TwitterDAO mTwitterDAO;

    @Inject
    TwitterClient mTwitterClient;

    @Inject
    EventBus mEventBus;

    private int mMaxScrollSize;
    private boolean mIsAvatarShown = true;
    private ActivityUserProfileBinding mBinding;
    private long mUserId;
    private User mUser;

    public static Intent createIntent(final Context context, final Long userId) {
        final Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlabberApplication.from(this).getComponent().inject(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);

        mUserId = getIntent().getLongExtra(EXTRA_USER_ID, -1);
//        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (NetworkUtils.isOnline(this)) {
            final Intent intent = UpdateTimelineService.createIntentForUserProfile(this, mUserId);
            startService(intent);
        } else {
            mBinding.pbLoadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.message_no_internet, Toast.LENGTH_SHORT).show();
            tryRetrievingOfflineData();
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
    public void onProfileImageSelected(final ITweetWithUser tweet) {
        long userId = tweet.getUserId();

        if (tweet.getRetweetedUserId() != null && tweet.getRetweetedUserId() > 1) {
            userId = tweet.getRetweetedUserId();
        }

        final Intent intent = UserProfileActivity.createIntent(this, userId);
        startActivity(intent);
    }

    @Override
    public void onOffsetChanged(final AppBarLayout appBarLayout, final int i) {
        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }

        final int percentage = Math.abs(i * 100 / mMaxScrollSize);

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            mBinding.ivProfile.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            mBinding.ivProfile.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final UserProfileRetrieved event) {
        mBinding.pbLoadingProgressBar.setVisibility(View.GONE);
        if (event.isSuccess()) {
            mUser = mTwitterDAO.getUser(mUserId);
            populateUserProfile();
        } else {
            Toast.makeText(this, R.string.message_profile_failed, Toast.LENGTH_SHORT).show();
            tryRetrievingOfflineData();
        }
    }

    private void populateUserProfile() {
        mBinding.vpProfile.setVisibility(View.VISIBLE);
        mBinding.appbarLayout.setVisibility(View.VISIBLE);
        mBinding.vpProfile.setAdapter(new ProfilePagerAdapter(getSupportFragmentManager(), this, mUser.getId()));
        mBinding.tlProfile.setupWithViewPager(mBinding.vpProfile);
        mBinding.appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = mBinding.appbarLayout.getTotalScrollRange();

        String url = mUser.getProfileBackgroundImageUrl();

        if (! TextUtils.isEmpty(mUser.getProfileBannerUrl())) {
            url = mUser.getProfileBannerUrl();
        }

        ImageUtils.loadImage(this, mBinding.ivProfileBackdrop, url);
        ImageUtils.loadProfileImageWithRounderCorners(this, mBinding.ivProfile, mUser.getProfileImageUrl());

        mBinding.tvName.setText(mUser.getRealName());
        mBinding.tvUserName.setText("@" + mUser.getScreenName());

        if (TextUtils.isEmpty(mUser.getDescription())) {
            mBinding.tvUserDesc.setVisibility(View.GONE);
        } else {
            mBinding.tvUserDesc.setText(mUser.getDescription());
        }

        setFollowing();
        setFollowers();
    }

    private void tryRetrievingOfflineData() {
        mUser = mTwitterDAO.getUser(mUserId);

        if (mUser == null) {
            mBinding.tvProfileError.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.warning_no_offline_profile, Toast.LENGTH_SHORT).show();
            return;
        }

        populateUserProfile();
    }

    private void setFollowing() {
        final SpannableStringBuilder spannable = ViewUtils.getSpannedText(this, getString(R.string.text_following), mUser.getFriendsCount());
        mBinding.tvFollowing.setText(spannable);
    }

    private void setFollowers() {
        final SpannableStringBuilder spannable = ViewUtils.getSpannedText(this, getString(R.string.text_followers), mUser.getFollowersCount());
        mBinding.tvFollowers.setText(spannable);
    }

    private void openCompose(final ITweetWithUser tweetWithUser) {
        final FragmentManager fm = getSupportFragmentManager();
        final ComposeFragment composeFragment = ComposeFragment.createInstance(tweetWithUser);
        composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
        composeFragment.show(fm, "compose_fragment");
        composeFragment.setCallback(this);
    }

    static class ProfilePagerAdapter extends FragmentPagerAdapter {
        private static final int POSITION_USER_TIMELINE = 0;
        private static final int POSITION_MEDIA = 1;
        private static final int POSITION_FAVORITES = 2;

        private final int PAGE_COUNT = 3;
        private int tabTitleIds[] = new int[]{R.string.profile_tab_title_tweets,
                R.string.profile_tab_title_media, R.string.profile_tab_title_likes};

        private final Context mContext;
        private final long mUserId;

        ProfilePagerAdapter(final FragmentManager fm, final Context context, final long userId) {
            super(fm);
            mContext = context;
            mUserId = userId;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case POSITION_USER_TIMELINE: {
                    return UserTimelineFragment.createInstance(mUserId);
                }
                case POSITION_MEDIA: {
                    return MediaFragment.createInstance(mUserId);
                }
                case POSITION_FAVORITES: {
                    return LikesFragment.createInstance(mUserId);
                }
            }
            throw new IllegalStateException("Unknown tab position: " + position);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return mContext.getResources().getString(tabTitleIds[position]);
        }
    }
}