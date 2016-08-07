package com.sharathp.blabber.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BlabberDateUtils {
    public static final String DATE_FORMAT_TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public static final String DATE_FORMAT_DETAIL = "h:mm a . dd MMM yy";

    private static final String RELATIVE_SUFFIX = " ago";

    public static String getRelativeTime(final Date date) {
        final String formatted = android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS,
                android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE).toString();

        if (formatted.endsWith(RELATIVE_SUFFIX)) {
            return formatted.substring(0, formatted.indexOf(RELATIVE_SUFFIX));
        } else {
            return formatted;
        }
    }

    public static String getDetailPageTime(final Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DETAIL);
        return sdf.format(date);
    }

    public static String getTwitterRelativeTime(final Date date) {
        String time;
        final long diff = (System.currentTimeMillis() - date.getTime()) / 1000;
        if (diff < 5) {
            time = "Just now";
        } else if (diff < 60) {
            time = String.format(Locale.ENGLISH, "%ds", diff);
        } else if (diff < 60 * 60) {
            time = String.format(Locale.ENGLISH, "%dm", diff / 60);
        } else if (diff < 60 * 60 * 24) {
            time = String.format(Locale.ENGLISH, "%dh", diff / (60 * 60));
        } else if (diff < 60 * 60 * 24 * 30) {
            time = String.format(Locale.ENGLISH, "%dd", diff / (60 * 60 * 24));
        } else {
            Calendar now = Calendar.getInstance();
            Calendar then = Calendar.getInstance();
            then.setTime(date);
            if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                time = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
            } else {
                time = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                        + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                        + " " + String.valueOf(then.get(Calendar.YEAR) - 2000);
            }
        }
        return time;
    }
}
