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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.View;

import com.bumptech.glide.Glide;
import com.malmstein.fenster.controller.MediaFensterPlayerController;
import com.sharathp.blabber.BlabberApplication;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityDetailTweetBinding;
import com.sharathp.blabber.fragments.ComposeFragment;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.util.BlabberDateUtils;
import com.sharathp.blabber.util.ImageUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import javax.inject.Inject;

public class TweetDetailActivity  extends AppCompatActivity implements ComposeFragment.ComposeCallback {
    public static final String EXTRA_TWEET = TweetDetailActivity.class.getSimpleName() + ":TWEET";

    @Inject
    EventBus mEventBus;

    private ITweetWithUser mTweetWithUser;
    private ActivityDetailTweetBinding mBinding;

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

        setLikes();
        setRetweets();

        mBinding.ivReplyAction.setOnClickListener(view -> {
            final FragmentManager fm = getSupportFragmentManager();
            final ComposeFragment composeFragment = ComposeFragment.createInstance(mTweetWithUser);
            composeFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullScreen);
            composeFragment.show(fm, "compose_fragment");
            composeFragment.setCallback(this);
        });
    }

    private void setLikes() {
        final SpannableStringBuilder spannable = getSpannedText(getString(R.string.text_retweets), mTweetWithUser.getFavoriteCount());
        mBinding.tvLikes.setText(spannable);
        if (mTweetWithUser.getFavorited()) {
            mBinding.ivLikeAction.setImageResource(R.drawable.ic_like_active);
        }
    }

    private void setRetweets() {
        final SpannableStringBuilder spannable = getSpannedText(getString(R.string.text_likes), mTweetWithUser.getRetweetCount());
        mBinding.tvRetweets.setText(spannable);
        if (mTweetWithUser.getRetweeted()) {
            mBinding.ivRetweetAction.setImageResource(R.drawable.ic_retweet_active);
        }
    }

    private SpannableStringBuilder getSpannedText(final String label, final int count) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(Integer.toString(count));
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.detail_text_likes_retweets_numbers)), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" ").append(label);
        return spannableStringBuilder;
    }

    @Override
    public void onTweetSubmitted(final String tweet) {
        // no-op
    }
}