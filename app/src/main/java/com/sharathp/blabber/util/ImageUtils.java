package com.sharathp.blabber.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageUtils {
    private static final int TWEET_LIST_PROFILE_TRANSFORM_RADIUS = 5;
    private static final int PROFILE_TRANSFORM_MARGIN = 0;

    private static final int PROFILE_TRANSFORM_RADIUS = 10;

    private static final int TRANSFORM_RADIUS = 10;
    private static final int TRANSFORM_MARGIN = 0;

    private static final String DEFAULT_QUALITY = "_normal";
    private static final String DESIRED_QUALITY = "_bigger";


    public static void loadTweetsListProfileImageWithRounderCorners(final Context context, final ImageView imageView, final String url) {
        Glide.with(context)
                .load(getDesiredQualityImageUrl(url))
                .bitmapTransform(new RoundedCornersTransformation(context, TWEET_LIST_PROFILE_TRANSFORM_RADIUS, PROFILE_TRANSFORM_MARGIN))
                .placeholder(R.drawable.ic_progress_indeterminate)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    public static void loadProfileImageWithRounderCorners(final Context context, final ImageView imageView, final String url) {
        Glide.with(context)
                .load(getDesiredQualityImageUrl(url))
                .bitmapTransform(new RoundedCornersTransformation(context, PROFILE_TRANSFORM_RADIUS, PROFILE_TRANSFORM_MARGIN))
                .placeholder(R.drawable.ic_progress_indeterminate)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    public static void loadImageWithRounderCorners(final Context context, final ImageView imageView, final String url) {
        Glide.with(context)
                .load(getDesiredQualityImageUrl(url))
                .bitmapTransform(new RoundedCornersTransformation(context, TRANSFORM_RADIUS, TRANSFORM_MARGIN))
                .placeholder(R.drawable.ic_progress_indeterminate)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    public static void loadImage(final Context context, final ImageView imageView, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Glide.with(context)
                .load(getDesiredQualityImageUrl(url))
                .centerCrop()
                .placeholder(R.drawable.ic_progress_indeterminate)
                .error(R.drawable.ic_error)
                .into(imageView);
    }

    private static String getDesiredQualityImageUrl(final String url) {
        return url.replace(DEFAULT_QUALITY, DESIRED_QUALITY);
    }
}
