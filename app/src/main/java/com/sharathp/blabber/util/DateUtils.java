package com.sharathp.blabber.util;

import java.util.Date;

public class DateUtils {
    public static final String DATE_FORMAT_TWITTER ="EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public static String getRelativeTime(final Date date) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS,
                android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }
}
