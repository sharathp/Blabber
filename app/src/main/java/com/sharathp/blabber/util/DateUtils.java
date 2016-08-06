package com.sharathp.blabber.util;

import java.util.Date;

public class DateUtils {
    public static final String DATE_FORMAT_TWITTER ="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    private static final String RELATIVE_SUFFIX = ". ago";

    public static String getRelativeTime(final Date date) {
        final String formatted = android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(),
                android.text.format.DateUtils.SECOND_IN_MILLIS,
                android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE).toString();



        if (formatted.endsWith(RELATIVE_SUFFIX)) {
            return formatted.substring(0, formatted.indexOf(RELATIVE_SUFFIX));
        } else {
            return formatted;
        }
    }
}
