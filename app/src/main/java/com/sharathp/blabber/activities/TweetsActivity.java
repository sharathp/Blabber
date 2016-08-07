package com.sharathp.blabber.activities;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import android.widget.TextView;

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
    private TextView mToolbarTitleTextView;

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
        mToolbarTitleTextView = binding.tvToolbarTitle;

        mComposeFab.setOnClickListener(view -> {
            final FragmentManager fm = getSupportFragmentManager();
            final ComposeFragment composeFragment = ComposeFragment.createInstance();
            composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
            composeFragment.show(fm, "compose_fragment");
            composeFragment.setCallback(this);
        });
        mComposeFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fab_compose_background)));

        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        mToolbarTitleTextView.setText(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onTweetSelected(TweetWithUser tweet) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop service
        stopService(UpdateTimelineService.createIntentForLatestItems(this));
    }

    @Override
    public void onTweetSubmitted(String tweet) {
        mEventBus.post(new TweetRefreshRequiredEvent());
    }
}
