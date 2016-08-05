package com.sharathp.blabber.util;

import java.util.Date;

public class DateUtils {

    public static String getRelativeTime(final Date date) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS,
                android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }
}
