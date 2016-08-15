package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.malmstein.fenster.controller.MediaFensterPlayerController;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityDetailTweetBinding;
import com.sharathp.blabber.events.FavoritedEvent;
import com.sharathp.blabber.events.RetweetedEvent;
import com.sharathp.blabber.events.UnRetweetedEvent;
import com.sharathp.blabber.events.UnfavoritedEvent;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.repositories.TwitterDAO;
import com.sharathp.blabber.service.UpdateTimelineService;
import com.sharathp.blabber.util.BlabberDateUtils;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.util.ViewUtils;
import com.sharathp.blabber.views.TweetContentCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import javax.inject.Inject;

public class TweetDetailActivity  extends AppCompatActivity implements ComposeFragment.ComposeCallback, TweetContentCallback {
    public static final String EXTRA_TWEET = TweetDetailActivity.class.getSimpleName() + ":TWEET";

    @Inject
    EventBus mEventBus;

    @Inject
    TwitterDAO mTwitterDAO;

    private ITweetWithUser mTweetWithUser;
    private ActivityDetailTweetBinding mBinding;

    private boolean mFavorited;
    private boolean mRetweeted;

    public static Intent createIntent(final Context context, final ITweetWithUser tweetWithUser) {
        final Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(EXTRA_TWEET, (Parcelable) tweetWithUser);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlabberApplication.from(this).getComponent().inject(this);
        mTweetWithUser = getIntent().getParcelableExtra(EXTRA_TWEET);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_tweet);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindTweet();
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mTweetWithUser.getVideoUrl() == null) {
            return;
        }

        // readjust height & play in next frame
        mBinding.flVideo.post(() -> {
            final Display display = getWindowManager().getDefaultDisplay();
            final int width = display.getWidth();
            final int newHeight = (int) ((double) width / mTweetWithUser.getVideoAspectRatio());
            mBinding.flVideo.getLayoutParams().height = newHeight;

            //  play video
            mBinding.playVideoTexture.setMediaController(mBinding.playVideoController);
            mBinding.playVideoTexture.setVideo(mTweetWithUser.getVideoUrl(),
                    MediaFensterPlayerController.DEFAULT_VIDEO_START);
            mBinding.playVideoTexture.start();
        });
    }

    private void bindTweet() {
        mFavorited = mTweetWithUser.getFavorited();
        mRetweeted = mTweetWithUser.getRetweeted();

        String userName = mTweetWithUser.getUserRealName();
        String screenName = mTweetWithUser.getUserScreenName();
        String profileImageUrl = mTweetWithUser.getUserImageUrl();

        if (mTweetWithUser.getRetweetedUserName() != null) {
            // show retweeted & image,
            mBinding.ivRetweetImage.setVisibility(View.VISIBLE);
            mBinding.tvRetweeted.setVisibility(View.VISIBLE);

            userName = mTweetWithUser.getRetweetedUserName();
            screenName = mTweetWithUser.getRetweetedScreenName();
            profileImageUrl = mTweetWithUser.getRetweetedProfileImageUrl();

            // TODO - compare self to check who retweeted and then use "You" if self retweeted
            mBinding.tvRetweeted.setText(String.format(getResources().getString(R.string.str_pattern_retweeted_name), mTweetWithUser.getUserRealName()));
        } else {
            // hide retweeted & image
            mBinding.ivRetweetImage.setVisibility(View.GONE);
            mBinding.tvRetweeted.setVisibility(View.GONE);
        }

        mBinding.tvRealName.setText(userName);
        mBinding.tvScreenName.setText("@" + screenName);
        mBinding.tvTime.setText(BlabberDateUtils.getDetailPageTime(new Date(mTweetWithUser.getCreatedAt())));
        mBinding.tvContent.setText(mTweetWithUser.getText());

        ViewUtils.addContentSpans(mBinding.tvContent, this);

        Glide.clear(mBinding.ivProfileImage);
        ImageUtils.loadTweetsListProfileImageWithRounderCorners(this, mBinding.ivProfileImage, profileImageUrl);

        if (mTweetWithUser.getImageUrl() != null) {
            mBinding.ivMediaImage.setVisibility(View.VISIBLE);
            mBinding.ivMediaImage.setHeightRatio((float) mTweetWithUser.getImageHeight() / (float) mTweetWithUser.getImageWidth());
            Glide.clear(mBinding.ivMediaImage);
            ImageUtils.loadImageWithRounderCorners(this, mBinding.ivMediaImage, mTweetWithUser.getImageUrl());
        } else {
            mBinding.ivMediaImage.setVisibility(View.GONE);
        }

        if (mTweetWithUser.getVideoUrl() != null) {
            mBinding.flVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.flVideo.setVisibility(View.GONE);
        }

        setLikes(mTweetWithUser.getFavoriteCount(), mTweetWithUser.getRetweetedUserName(),
                mTweetWithUser.getRetweetedFavoriteCount(), mTweetWithUser.getFavorited());
        setRetweets(mTweetWithUser.getRetweeted(),mTweetWithUser.getRetweetCount() );

        mBinding.ivReplyAction.setOnClickListener(view -> {
            final FragmentManager fm = getSupportFragmentManager();
            final ComposeFragment composeFragment = ComposeFragment.createInstance(mTweetWithUser);
            composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
            composeFragment.show(fm, "compose_fragment");
            composeFragment.setCallback(this);
        });

        mBinding.ivRetweetAction.setOnClickListener(view -> {
            if (mRetweeted) {
                startService(UpdateTimelineService.createIntentForUnRetweet(this, mTweetWithUser.getId()));
            } else {
                startService(UpdateTimelineService.createIntentForRetweet(this, mTweetWithUser.getId()));
            }
        });

        mBinding.ivLikeAction.setOnClickListener(view -> {
            if (mFavorited) {
                // already favorited, unfavorite
                startService(UpdateTimelineService.createIntentForUnFavorite(this, mTweetWithUser.getId()));
            } else {
                // unfavorited, favorite
                startService(UpdateTimelineService.createIntentForFavorite(this, mTweetWithUser.getId()));
            }
        });


        mBinding.ivShareAction.setOnClickListener(view -> {
            shareTweetLink();
        });
    }

    private void setLikes(final Integer favoriteCount, final String retweetedUserName,
                          final Integer retweetedFavoriteCount, final boolean favorited) {
        int correctCount = favoriteCount;
        if (retweetedUserName != null) {
            correctCount = retweetedFavoriteCount;
        }
        final SpannableStringBuilder spannable = ViewUtils.getSpannedText(this, getString(R.string.text_likes), correctCount);
        mBinding.tvLikes.setText(spannable);
        if (favorited) {
            mBinding.ivLikeAction.setImageResource(R.drawable.ic_like_active);
        } else {
            mBinding.ivLikeAction.setImageResource(R.drawable.ic_like);
        }
    }

    private void setRetweets(final boolean retweeted, final Integer retweetCount) {
        final SpannableStringBuilder spannable = ViewUtils.getSpannedText(this, getString(R.string.text_retweets), retweetCount);
        mBinding.tvRetweets.setText(spannable);
        if (retweeted) {
            mBinding.ivRetweetAction.setImageResource(R.drawable.ic_retweet_active);
        } else {
            mBinding.ivRetweetAction.setImageResource(R.drawable.ic_retweet);
        }
    }

    private void updateLikes() {
        final Tweet tweet = mTwitterDAO.getTweet(mTweetWithUser.getId());
        mFavorited = tweet.isFavorited();
        setLikes(tweet.getFavoriteCount(), tweet.getRetweetedUserName(),
                tweet.getRetweetedFavoriteCount(), tweet.isFavorited());
    }

    private void updateRetweet() {
        final Tweet tweet = mTwitterDAO.getTweet(mTweetWithUser.getId());
        mRetweeted = tweet.isRetweeted();
        setRetweets(tweet.isRetweeted(), tweet.getRetweetCount());
    }

    private void shareTweetLink() {
        final Intent shareIntent = getShareIntent();
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_intent_title)));
    }

    private Intent getShareIntent() {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        Long statusId = mTweetWithUser.getId();
        String screenName = mTweetWithUser.getUserScreenName();

        if (mTweetWithUser.getRetweetedStatusId() != null && mTweetWithUser.getRetweetedStatusId() > 0) {
            screenName = mTweetWithUser.getRetweetedScreenName();
            statusId = mTweetWithUser.getRetweetedStatusId();
        }

        final String url = "https://twitter.com/" + screenName + "/status/" + statusId + "?s=09";
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format(getResources().getString(R.string.share_intent_text), "@" + screenName, url));
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format(getResources().getString(R.string.share_intent_subject), "@" + screenName));
        return shareIntent;
    }

    @Override
    public void onTweetSubmitted(final String tweet) {
        // no-op
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final FavoritedEvent event) {
        // not applicable for this tweet
        if (! mTweetWithUser.getId().equals(event.getTweetId())) {
            return;
        }

        if (! event.isSuccess()) {
            Toast.makeText(this, R.string.message_favorite_failed, Toast.LENGTH_SHORT).show();
        } else {
            updateLikes();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final UnfavoritedEvent event) {
        // not applicable for this tweet
        if (! mTweetWithUser.getId().equals(event.getTweetId())) {
            return;
        }

        if (! event.isSuccess()) {
            Toast.makeText(this, R.string.message_unfavorite_failed, Toast.LENGTH_SHORT).show();
        } else {
            updateLikes();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final RetweetedEvent event) {
        // not applicable for this tweet
        if (! mTweetWithUser.getId().equals(event.getTweetId())) {
            return;
        }

        if (! event.isSuccess()) {
            Toast.makeText(this, R.string.message_retweet_failed, Toast.LENGTH_SHORT).show();
        } else {
            updateRetweet();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final UnRetweetedEvent event) {
        // not applicable for this tweet
        if (! mTweetWithUser.getId().equals(event.getTweetId())) {
            return;
        }

        if (! event.isSuccess()) {
            Toast.makeText(this, R.string.message_unretweet_failed, Toast.LENGTH_SHORT).show();
        } else {
            updateRetweet();
        }
    }

    @Override
    public void onUserScreenNameSelected(final String userScreenName) {
        final Intent intent = UserProfileActivity.createIntent(this, userScreenName);
        startActivity(intent);
    }

    @Override
    public void onHashSpanSelected(final String hash) {
        final Intent intent = SearchActivity.createIntent(this, hash);
        startActivity(intent);
    }
}