package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.Map;

/**
 * Created by Irina on 09.02.2018.
 */

public class MoodEvent extends Event {

    private final static String ATTRIBUTE_NAME_START_DATE = "startDate";
    private final static String ATTRIBUTE_NAME_EVENT_ID = "eventId";

    public MoodEvent(final long timeInMillis, final int selectedMoodId) {
        super();
        this.eventType = EventTypes.moodEventTypeId.getId();
        this.startDateInUnixFormat = timeInMillis;
        this.eventId = selectedMoodId;
    }

    public MoodEvent (final Context context, final int rowId) {

        Map<String,Object> eventData = DBOperations.getEvent(context, rowId);

        this.startDateInUnixFormat = (long) eventData.get(ATTRIBUTE_NAME_START_DATE);
        this.eventId = (int) eventData.get(ATTRIBUTE_NAME_EVENT_ID);
        this.rowId = rowId;
    }

    public void saveToDB(final Context context) {
        DBOperations.addRow(context, this.eventId, startDateInUnixFormat, this.eventType, 0, (long) 0);
    }

}
