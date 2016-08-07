package com.sharathp.blabber.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;
import com.sharathp.blabber.databinding.ActivityDetailTweetBinding;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.util.BlabberDateUtils;
import com.sharathp.blabber.util.ImageUtils;

import java.util.Date;

public class TweetDetailActivity  extends AppCompatActivity {
    public static final String EXTRA_TWEET = TweetDetailActivity.class.getSimpleName() + ":TWEET";

    private TweetWithUser mTweetWithUser;
    private ActivityDetailTweetBinding mBinding;

    public static Intent createIntent(final Context context, final TweetWithUser tweetWithUser) {
        final Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(EXTRA_TWEET, tweetWithUser);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTweetWithUser = getIntent().getParcelableExtra(EXTRA_TWEET);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_tweet);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindTweet();
    }

    private void bindTweet() {
        mBinding.tvRealName.setText(mTweetWithUser.getUserRealName());
        mBinding.tvScreenName.setText("@" + mTweetWithUser.getUserScreenName());
        mBinding.tvTime.setText(BlabberDateUtils.getDetailPageTime(new Date(mTweetWithUser.getCreatedAt())));
        mBinding.tvContent.setText(mTweetWithUser.getText());

        Glide.clear(mBinding.ivProfileImage);

        ImageUtils.loadImage(this, mBinding.ivProfileImage, mTweetWithUser.getUserImageUrl());
        setLikes();
        setRetweets();
    }

    private void setLikes() {
        final SpannableStringBuilder spannable = getSpannedText(getString(R.string.text_retweets), mTweetWithUser.getFavoriteCount());
        mBinding.tvLikes.setText(spannable);
    }

    private void setRetweets() {
        final SpannableStringBuilder spannable = getSpannedText(getString(R.string.text_likes), mTweetWithUser.getFavoriteCount());
        mBinding.tvRetweets.setText(spannable);
    }

    private SpannableStringBuilder getSpannedText(final String label, final int count) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(Integer.toString(count));
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.detail_text_likes_retweets_numbers)), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" ").append(label);
        return spannableStringBuilder;
    }
}