package com.macgavrina.moodmenology;

import com.macgavrina.moodmenology.model.SelectedDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

/**
 * Created by Irina on 09.02.2018.
 */

public abstract class SmallFunctions {

    private static final Long hour_in_millis = Long.valueOf(3600000);

    public static String formatTime(final long aLong) {

        long unixTime = aLong;

        Date date = new Date(unixTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public static String formatTimeWithCompare (final long aLong, final String startDate, final String endDate) {

        long unixTime = aLong;
        Date date = new Date(unixTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedDate = sdf.format(date);

        if (aLong < Long.valueOf(startDate) || aLong > Long.valueOf(endDate)) {
            formattedDate = "* " + formattedDate;
        }

        return formattedDate;
    }


    public static String formatDate(final long aLong) {

        long unixTime = aLong;

        Date date = new Date(unixTime);
        SimpleDateFormat sdf = new SimpleDateFormat("EE, dd MMM");
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public static String formatDuration (final long aLong){

        Long diffHours = TimeUnit.HOURS.convert(aLong, TimeUnit.MILLISECONDS);

        Date date = new Date(aLong);
        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        String formattedDate;

        if (aLong >= hour_in_millis){
        formattedDate = new StringBuffer().append(diffHours).append("h ").append(sdfMinute.format(date)).append("min").toString();}
        else {
            formattedDate = new StringBuffer().append(sdfMinute.format(date)).append("min").toString();
        }

        return formattedDate;
    }
}
