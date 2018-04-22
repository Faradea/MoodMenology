package com.macgavrina.moodmenology;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Irina on 09.02.2018.
 */

public abstract class SmallFunctions {

    private static final long hour_in_millis = 3600000L;

    public static String formatTime(final long aLong) {

        Date date = new Date(aLong);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(date);
    }

    public static String formatTimeWithCompare (final long aLong, final long startDate, final long endDate) {

        Date date = new Date(aLong);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedDate = sdf.format(date);

        if (aLong < startDate || aLong >= endDate) {
            formattedDate = "* " + formattedDate;
        }

        return formattedDate;
    }


    public static String formatDate(final long aLong) {

        Date date = new Date(aLong);
        SimpleDateFormat sdf = new SimpleDateFormat("EE, dd MMM");

        return sdf.format(date);
    }

    public static String formatDuration (final long aLong){

        long diffHours = TimeUnit.HOURS.convert(aLong, TimeUnit.MILLISECONDS);

        Date date = new Date(aLong);
        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        String formattedDate;

        if (aLong >= hour_in_millis){
        formattedDate = String.valueOf(diffHours) + "h " + sdfMinute.format(date) + "min";}
        else {
            formattedDate = sdfMinute.format(date) + "min";
        }

        return formattedDate;
    }
}
