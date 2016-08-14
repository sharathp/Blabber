package com.sharathp.blabber.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.sharathp.blabber.R;
import com.sharathp.blabber.views.PatternEditableBuilder;
import com.sharathp.blabber.views.adapters.TweetCallback;

import java.util.regex.Pattern;

public class ViewUtils {

    public static SpannableStringBuilder getSpannedText(final Context context, final String label, final int count) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(Integer.toString(count));
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.detail_text_likes_retweets_numbers)), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" ").append(label);
        return spannableStringBuilder;
    }

    public static void addContentSpans(final TextView textView, final TweetCallback tweetCallback) {
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        userScreenName -> {
                            tweetCallback.onUserScreenNameSelected(userScreenName);
                        }).into(textView);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), Color.BLUE,
                        hash -> {
                            tweetCallback.onHashSpanSelected(hash);
                        }).into(textView);
    }
}
