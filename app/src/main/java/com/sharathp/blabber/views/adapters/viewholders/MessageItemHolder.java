package com.sharathp.blabber.views.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;
import com.sharathp.blabber.repositories.rest.resources.MessageResource;
import com.sharathp.blabber.util.BlabberDateUtils;
import com.sharathp.blabber.util.ImageUtils;
import com.sharathp.blabber.views.DynamicHeightImageView;
import com.sharathp.blabber.views.adapters.MessageCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageItemHolder extends RecyclerView.ViewHolder {
    private final MessageCallback mMessageCallback;
    private final Long mLoggedInUserId;

    private MessageResource mItem;

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

    @BindView(R.id.iv_message_direction)
    ImageView mMessageDirectionImageView;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (mMessageCallback != null) {
                long userId;
                if (mLoggedInUserId == mItem.getSender().getId()) {
                    userId = mItem.getRecipient().getId();
                } else {
                    userId = mItem.getSender().getId();
                }

                mMessageCallback.onMessageSelected(mItem, userId);
            }
        }
    };

    public MessageItemHolder(final View itemView,
                             final MessageCallback messageCallback,
                             final Long loggedInUserId) {
        super(itemView);
        mLoggedInUserId = loggedInUserId;
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(mOnClickListener);
        mMessageCallback = messageCallback;
    }

    public void bind(final MessageResource messageResource) {
        mItem = messageResource;

        String profileImageUrl;
        String realName;
        String screenName;
        int drawableResId;

        if (mLoggedInUserId == messageResource.getSender().getId()) {
            profileImageUrl = messageResource.getRecipient().getProfileImageUrl();
            realName = messageResource.getRecipient().getRealName();
            screenName = messageResource.getRecipient().getScreenName();
            drawableResId = R.drawable.ic_arrow_out;
        } else {
            profileImageUrl = messageResource.getSender().getProfileImageUrl();
            realName = messageResource.getSender().getRealName();
            screenName = messageResource.getSender().getScreenName();
            drawableResId = R.drawable.ic_arrow_in;
        }

        mScreenNameTextView.setText("@" + screenName);
        mRealNameTextView.setText(realName);
        mMessageDirectionImageView.setImageResource(drawableResId);

        Glide.clear(mProfileImageView);
        ImageUtils.loadProfileImageWithRounderCorners(mProfileImageView.getContext(), mProfileImageView, profileImageUrl);

        mContentTextView.setText(mItem.getText());
        mTimeTextView.setText(BlabberDateUtils.getTwitterRelativeTime(mItem.getCreatedAt()));
    }
}
