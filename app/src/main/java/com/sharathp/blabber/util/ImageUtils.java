package com.sharathp.blabber.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sharathp.blabber.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageUtils {
    private static final int TRANSFORM_RADIUS = 5;
    private static final int TRANSFORM_MARGIN = 2;


    public static void loadImage(final Context context, final ImageView imageView, final String url) {
        Glide.with(context)
                .load(url)
                .bitmapTransform(new RoundedCornersTransformation(context, TRANSFORM_RADIUS, TRANSFORM_MARGIN))
                .placeholder(R.drawable.ic_progress_indeterminate)
                .error(R.drawable.ic_error)
                .into(imageView);
    }
}
