package com.sharathp.blabber.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityTweetsBinding;
import com.sharathp.blabber.events.TweetRefreshRequiredEvent;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.fragments.TimelineFragment;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.views.adapters.TweetCallback;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TweetsActivity extends AppCompatActivity implements TweetCallback, ComposeFragment.ComposeCallback {
    private static final String TAG = TweetsActivity.class.getSimpleName();
    private static final int INDEX_HOME = 0;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private FloatingActionButton mComposeFab;

    private ActionBarDrawerToggle mDrawerToggle;

    @Inject
    EventBus mEventBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BlabberApplication.from(this).getComponent().inject(this);

        final ActivityTweetsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_tweets);
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

        showHome();
    }

    private void showHome() {
        // simulate clicking which shows home
        selectDrawerItem(mDrawer.getMenu().getItem(INDEX_HOME));
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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
        Fragment fragment;
        switch(menuItem.getItemId()) {
            case R.id.nav_home: {
                fragment = TimelineFragment.createInstance();
                mComposeFab.setVisibility(View.VISIBLE);
                break;
            }
            default: {
                Log.w(TAG, "Unknown menu item: " + menuItem.getTitle());
                fragment = TimelineFragment.createInstance();
                mComposeFab.setVisibility(View.VISIBLE);
                break;
            }
        }

        // Insert the fragment by replacing any existing fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();

        menuItem.setChecked(true);
        getSupportActionBar().setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onTweetSelected(final TweetWithUser tweet) {
        if (! requestWritePermissions()) {
            return;
        }

        final Intent intent = TweetDetailActivity.createIntent(this, tweet);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop service
        stopService(UpdateTimelineService.createIntentForLatestItems(this));
    }

    @Override
    public void onTweetSubmitted(final String tweet) {
        mEventBus.post(new TweetRefreshRequiredEvent());
    }

    @Override
    public void onTweetReplied(final TweetWithUser tweet) {
        openCompose(tweet);
    }

    private void openCompose(final TweetWithUser tweetWithUser) {
        final FragmentManager fm = getSupportFragmentManager();
        final ComposeFragment composeFragment = ComposeFragment.createInstance(tweetWithUser);
        composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
        composeFragment.show(fm, "compose_fragment");
        composeFragment.setCallback(this);
    }

    private boolean requestWritePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                return true;
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        return false;
    }
}
