package com.ch.cbptest;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
    private static DateFormat nameFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

    public static String dateToString(Calendar calendar) {
        return df.format(calendar.getTime());
    }

    public static Date stringToDate(String date) {
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int compareDate(String val1, String val2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(stringToCal(val1));
        cal2.setTime(stringToCal(val2));

        return cal1.compareTo(cal2);
    }

    public static String calToString(Calendar calendar) { return nameFormat.format(calendar.getTime()); }

    public static Date stringToCal(String date) {
        try {
            return nameFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static String toMmhg(int value) {
        return value + "mmHg";
    }
}
