package com.sharathp.blabber.config;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(final Context context, final GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(final Context context, final Glide glide) {
        // no-op
    }
}