package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityHomeBinding;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.fragments.HomeTimelineFragment;
import com.sharathp.blabber.fragments.MentionsFragment;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.repositories.LocalPreferencesDAO;
import com.sharathp.blabber.repositories.rest.TwitterClient;
import com.sharathp.blabber.repositories.rest.resources.UserResource;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.util.PermissionUtils;
import com.sharathp.blabber.views.adapters.TweetCallback;

import javax.inject.Inject;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity implements TweetCallback, ComposeFragment.ComposeCallback {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int INDEX_HOME = 0;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private FloatingActionButton mComposeFab;

    private ActionBarDrawerToggle mDrawerToggle;

    @Inject
    LocalPreferencesDAO mLocalPreferencesDAO;

    @Inject
    TwitterClient mTwitterClient;

    @Inject
    Gson mGson;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BlabberApplication.from(this).getComponent().inject(this);

        final ActivityHomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mToolbar = binding.toolbar;
        mDrawerLayout = binding.drawerLayout;
        mDrawer = binding.nvDrawer;
        mComposeFab = binding.fabFilter;

        mComposeFab.setOnClickListener(view -> openCompose(null));
        mComposeFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_compose_background)));

        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setSupportActionBar(mToolbar);
        setupDrawerContent();

        setHomeInfo();

        binding.vpHome.setAdapter(new HomePagerAdapter(getSupportFragmentManager(), this));

        binding.tlHome.setupWithViewPager(binding.vpHome);

        mTwitterClient.getLoggedInUserDetails(new TextHttpResponseHandler() {
            @Override
            public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                Toast.makeText(HomeActivity.this, R.string.error_profile_retrieval, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                final UserResource userResource = mGson.fromJson(responseString, UserResource.class);
                mLocalPreferencesDAO.setUserId(userResource.getId());
                mLocalPreferencesDAO.setUserRealName(userResource.getName());
                mLocalPreferencesDAO.setUserScreenName(userResource.getScreenName());
                mLocalPreferencesDAO.setUserProfileImageUrl(userResource.getProfileImageUrl());
                populateLoggedInUserDetails();
            }
        });
    }

    private void populateLoggedInUserDetails() {
        final View headerLayout = mDrawer.getHeaderView(0);

        final ImageView profileImageView = (ImageView) headerLayout.findViewById(R.id.iv_profile_image);
        final TextView profileNameTextView = (TextView) headerLayout.findViewById(R.id.tv_profile_name);

        final String userName = mLocalPreferencesDAO.getUserRealName();
        final String userImageUrl = mLocalPreferencesDAO.getUserProfileImageUrl();

        profileNameTextView.setText(userName);
        ImageUtils.loadTweetsListProfileImageWithRounderCorners(this, profileImageView, userImageUrl);
    }

    private void setHomeInfo() {
        final MenuItem homeMenuItem = mDrawer.getMenu().getItem(INDEX_HOME);
        homeMenuItem.setChecked(true);
        setTitle(homeMenuItem.getTitle());
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            populateLoggedInUserDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent() {
        mDrawer.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(final MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_home: {
                // already on home, do nothing
                break;
            }
            case R.id.nav_profile: {
                final Intent intent = UserProfileActivity.createIntent(this, mLocalPreferencesDAO.getUserId());
                startActivity(intent);
                break;
            }
            default: {
                Log.w(TAG, "Unknown menu item: " + menuItem.getTitle());
                mComposeFab.setVisibility(View.VISIBLE);
                break;
            }
        }
        mDrawerLayout.closeDrawers();
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
        startActivity(intent);
    }

    @Override
    public void onUnfavorited(final ITweetWithUser tweet) {
        final Intent intent = UpdateTimelineService.createIntentForUnFavorite(this, tweet.getId());
        startActivity(intent);
    }

    @Override
    public void onRetweeted(final ITweetWithUser tweet) {
        final Intent intent = UpdateTimelineService.createIntentForRetweet(this, tweet.getId());
        startActivity(intent);
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

    private void openCompose(final ITweetWithUser tweetWithUser) {
        final FragmentManager fm = getSupportFragmentManager();
        final ComposeFragment composeFragment = ComposeFragment.createInstance(tweetWithUser);
        composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
        composeFragment.show(fm, "compose_fragment");
        composeFragment.setCallback(this);
    }

    static class HomePagerAdapter extends FragmentPagerAdapter {
        private static final int POSITION_HOME_TIMELINE = 0;
        private static final int POSITION_MENTIONS = 1;

        private final int PAGE_COUNT = 2;
        private int tabTitleIds[] = new int[]{R.string.home_tab_title_home_timeline,
                R.string.home_tab_title_mentions};
        private Context mContext;

        HomePagerAdapter(final FragmentManager fm, final Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case POSITION_HOME_TIMELINE: {
                    return HomeTimelineFragment.createInstance();
                }
                case POSITION_MENTIONS: {
                    return MentionsFragment.createInstance();
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
