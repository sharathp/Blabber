package com.sharathp.blabber.views.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.util.BlabberDateUtils;
import com.sharathp.blabber.views.DynamicHeightImageView;
import com.sharathp.blabber.views.adapters.TweetCallback;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetViewHolder extends SquidViewHolder<TweetWithUser> {
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


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mTweetCallback != null) {
                mTweetCallback.onTweetSelected(item);
            }
        }
    };

    public TweetViewHolder(final View itemView, final TweetCallback tweetCallback) {
        super(itemView, new TweetWithUser());
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(mOnClickListener);
        mTweetCallback = tweetCallback;
    }

    public final void bindTweet() {
        mRealNameTextView.setText(item.getUserRealName());
        mScreenNameTextView.setText("@" + item.getUserScreenName());
        mTimeTextView.setText(BlabberDateUtils.getTwitterRelativeTime(new Date(item.getCreatedAt())));
        mContentTextView.setText(item.getText());

        Glide.clear(mProfileImageView);

        Glide.with(itemView.getContext())
                .load(item.getUserImageUrl())
                .fitCenter()
                .placeholder(R.drawable.ic_progress_indeterminate)
                .error(R.drawable.ic_error)
                .into(mProfileImageView);
    }
}
