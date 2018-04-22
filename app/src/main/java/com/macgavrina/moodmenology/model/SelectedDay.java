package com.macgavrina.moodmenology.model;

import java.util.Calendar;

/**
 * Created by Irina on 09.02.2018.
 */

public class SelectedDay {

    private static final long dayDurationInMillis = 86400000L;
    private Calendar currentDateAndTime;
    private Calendar endDayDateAndTime;
    private Calendar startDayDateAndTime;

    public SelectedDay(final long selectedDateInMillis) {

        super();

        currentDateAndTime = Calendar.getInstance();
        currentDateAndTime.setTimeInMillis(selectedDateInMillis);

        startDayDateAndTime = Calendar.getInstance();
        startDayDateAndTime.setTimeInMillis(selectedDateInMillis);
        startDayDateAndTime.set(java.util.Calendar.HOUR_OF_DAY, 0);
        startDayDateAndTime.set(java.util.Calendar.MINUTE, 0);

        endDayDateAndTime = Calendar.getInstance();
        endDayDateAndTime.setTimeInMillis(selectedDateInMillis+ dayDurationInMillis);
        endDayDateAndTime.set(java.util.Calendar.HOUR_OF_DAY, 0);
        endDayDateAndTime.set(java.util.Calendar.MINUTE, 0);
    }

    public long getDayStartTimestamp(){
        return startDayDateAndTime.getTimeInMillis();
    }

    public long getDayEndTimestamp(){
        return endDayDateAndTime.getTimeInMillis();
    }

    public Calendar getCurrentDateAndTime() {
        return currentDateAndTime;
    }
}
