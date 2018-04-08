package com.macgavrina.moodmenology.model;

import java.util.Calendar;

/**
 * Created by Irina on 09.02.2018.
 */

public class SelectedDay {

    private static final Long dayDurationInMillis = Long.valueOf(86400000);
    private Calendar currentDateAndTime;
    private Calendar endDayDateAndTime;
    private Calendar startDayDateAndTime;

    public SelectedDay(final Long selectedDateInMillis) {

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

    //ToDo REFACT сделать возвращаемое значение - Long и переводить в String на стороне получателя
    public String getDayStartStringTimestamp(){
        Long selectedDayStartDateTimestamp = startDayDateAndTime.getTimeInMillis();
        String selectedDayStartDateString = selectedDayStartDateTimestamp.toString();
        return selectedDayStartDateString;
    }

    public String getDayEndStringTimestamp(){
        Long selectedDayEndDateTimestamp = endDayDateAndTime.getTimeInMillis();
        String selectedDayEndDateString = selectedDayEndDateTimestamp.toString();
        return selectedDayEndDateString;
    }

    public Calendar getCurrentDateAndTime() {
        return currentDateAndTime;
    }
}
