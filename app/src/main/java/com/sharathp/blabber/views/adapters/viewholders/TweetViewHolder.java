package com.sharathp.blabber.views.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;
import com.sharathp.blabber.models.ITweetWithUser;
import com.sharathp.blabber.util.BlabberDateUtils;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.views.DynamicHeightImageView;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.yahoo.squidb.data.AbstractModel;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetViewHolder<T extends AbstractModel & ITweetWithUser> extends SquidViewHolder<T> {
    private final TweetCallback mTweetCallback;

    @BindView(R.id.iv_retweet_image)
    ImageView mRetweetImageView;

    @BindView(R.id.tv_retweeted)
    TextView mRetweetedName;

    @BindView(R.id.iv_profile_image)
    DynamicHeightImageView mProfileImageView;

    @BindView(R.id.tv_screen_name)
    TextView mScreenNameTextView;

    @BindView(R.id.tv_real_name)
    TextView mRealNameTextView;

    @BindView(R.id.tv_time)
    TextView mTimeTextView;

    @BindView(R.id.tv_content)
    TextView mContentTextView;

    @BindView(R.id.iv_media_image)
    DynamicHeightImageView mMediaImageView;

    @BindView(R.id.iv_reply_action)
    ImageView mReplyActionImageView;

    @BindView(R.id.iv_retweet_action)
    ImageView mRetweetActionImageView;

    @BindView(R.id.tv_retweet_count)
    TextView mRetweetCountTextView;

    @BindView(R.id.iv_like_action)
    ImageView mLikeActionImageView;

    @BindView(R.id.tv_like_count)
    TextView mLikeCountTextView;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mTweetCallback != null) {
                mTweetCallback.onTweetSelected(item);
            }
        }
    };

    private View.OnClickListener mReplyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mTweetCallback != null) {
                mTweetCallback.onTweetReplied(item);
            }
        }
    };

    public TweetViewHolder(final View itemView, final TweetCallback tweetCallback, final T item) {
        super(itemView, item);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(mOnClickListener);
        mReplyActionImageView.setOnClickListener(mReplyClickListener);

        mTweetCallback = tweetCallback;
    }

    public final void bindTweet() {
        String userName = item.getUserRealName();
        String screenName = item.getUserScreenName();
        String profileImageUrl = item.getUserImageUrl();

        if (item.getRetweetedUserName() != null) {
            // show retweeted & image,
            mRetweetImageView.setVisibility(View.VISIBLE);
            mRetweetedName.setVisibility(View.VISIBLE);

            userName = item.getRetweetedUserName();
            screenName = item.getRetweetedScreenName();
            profileImageUrl = item.getRetweetedProfileImageUrl();

            // TODO - compare self to check who retweeted and then use "You" if self retweeted
            mRetweetedName.setText(String.format(itemView.getContext().getResources().getString(R.string.str_pattern_retweeted_name), item.getUserRealName()));
        } else {
            // hide retweeted & image
            mRetweetImageView.setVisibility(View.GONE);
            mRetweetedName.setVisibility(View.GONE);
        }

        mRealNameTextView.setText(userName);
        mScreenNameTextView.setText("@" + screenName);
        mTimeTextView.setText(BlabberDateUtils.getTwitterRelativeTime(new Date(item.getCreatedAt())));
        mContentTextView.setText(item.getText());

        Glide.clear(mProfileImageView);
        ImageUtils.loadProfileImage(itemView.getContext(), mProfileImageView, profileImageUrl);

        if (item.getImageUrl() != null) {
            mMediaImageView.setVisibility(View.VISIBLE);
            mMediaImageView.setHeightRatio((float) item.getImageHeight() / (float) item.getImageWidth());
            Glide.clear(mMediaImageView);
            ImageUtils.loadImage(itemView.getContext(), mMediaImageView, item.getImageUrl());
        } else {
            mMediaImageView.setVisibility(View.GONE);
        }

        setLikes();
        setRetweets();
    }

    private void setLikes() {
        mLikeCountTextView.setText(Integer.toString(item.getFavoriteCount()));
        if (item.getFavorited()) {
            mLikeActionImageView.setImageResource(R.drawable.ic_like_active);
        } else {
            mLikeActionImageView.setImageResource(R.drawable.ic_like);
        }
    }

    private void setRetweets() {
        mRetweetCountTextView.setText(Integer.toString(item.getRetweetCount()));
        if (item.getRetweeted()) {
            mRetweetActionImageView.setImageResource(R.drawable.ic_retweet_active);
        } else {
            mRetweetActionImageView.setImageResource(R.drawable.ic_retweet);
        }
    }
}
