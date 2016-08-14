package com.sharathp.blabber.views.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;
import com.sharathp.blabber.repositories.rest.resources.FolloweeResource;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.views.DynamicHeightImageView;
import com.sharathp.blabber.views.adapters.FolloweeCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FolloweeItemHolder extends RecyclerView.ViewHolder {
    private final FolloweeCallback mFolloweeCallback;

    private FolloweeResource mFolloweeResource;

    @BindView(R.id.iv_profile_image)
    DynamicHeightImageView mProfileImageView;

    @BindView(R.id.tv_screen_name)
    TextView mScreenNameTextView;

    @BindView(R.id.tv_real_name)
    TextView mRealNameTextView;

    @BindView(R.id.tv_content)
    TextView mContentTextView;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mFolloweeCallback != null) {
                mFolloweeCallback.onFolloweeSelected(mFolloweeResource.getId());
            }
        }
    };

    public FolloweeItemHolder(final View itemView, final FolloweeCallback followeeCallback) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mFolloweeCallback = followeeCallback;
        itemView.setOnClickListener(mOnClickListener);
    }

    public void bind(final FolloweeResource followeeResource) {
        mFolloweeResource = followeeResource;

        mRealNameTextView.setText(followeeResource.getName());
        mScreenNameTextView.setText("@" + followeeResource.getScreenName());
        mContentTextView.setText(followeeResource.getDescription());

        Glide.clear(mProfileImageView);
        ImageUtils.loadTweetsListProfileImageWithRounderCorners(itemView.getContext(),
                mProfileImageView, followeeResource.getProfileImageUrl());
    }
}
