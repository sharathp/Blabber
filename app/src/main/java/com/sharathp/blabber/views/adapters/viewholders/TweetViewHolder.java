package com.sharathp.blabber.views.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharathp.blabber.R;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.views.DynamicHeightImageView;
import com.sharathp.blabber.views.adapters.TweetCallback;

import butterknife.BindView;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    protected final TweetCallback mTweetCallback;
    protected Tweet mTweet;

    @BindView(R.id.iv_retweet_image)
    ImageView mRetweetImageView;

    @BindView(R.id.tv_retweeted)
    TextView mRetweetedName;

    @BindView(R.id.iv_profile_image)
    DynamicHeightImageView mProfileImageView;

    @BindView(R.id.tv_name)
    TextView mNameTextView;

    @BindView(R.id.tv_user_name)
    TextView mUserNameTextView;

    @BindView(R.id.tv_time)
    TextView mTimeTextView;

    @BindView(R.id.tv_content)
    TextView mContentTextView;


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mTweetCallback != null) {
                mTweetCallback.onTweetSelected(mTweet);
            }
        }
    };

    public TweetViewHolder(final View itemView, final TweetCallback tweetCallback) {
        super(itemView);
        itemView.setOnClickListener(mOnClickListener);
        mTweetCallback = tweetCallback;
    }

    public final void bind(final Tweet tweet) {
        
    }
}
