package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.Map;

public class MoodEvent extends Event {

    private final static String ATTRIBUTE_NAME_START_DATE = "startDate";
    private final static String ATTRIBUTE_NAME_EVENT_ID = "eventId";
    private final static int DEFAULT_ACTION_GROUP_ID_FOR_MOOD = 0;
    private final static long DEFAULT_ENDTIME_FOR_MOOD = 0;


    public MoodEvent(final long timeInMillis, final int selectedMoodId) {
        super();
        this.eventType = EventTypes.moodEventTypeId.getId();
        this.startDateInUnixFormat = timeInMillis;
        this.eventId = selectedMoodId;
    }

    public MoodEvent (final Context context, final int rowId, final boolean skipLoadData) {

        if (skipLoadData) {
            this.rowId = rowId;
        }

        else {
            Map<String, Object> eventData = DBOperations.getEvent(context, rowId);

            this.startDateInUnixFormat = (long) eventData.get(ATTRIBUTE_NAME_START_DATE);
            this.eventId = (int) eventData.get(ATTRIBUTE_NAME_EVENT_ID);
            this.rowId = rowId;
        }
    }

    public void saveToDB(final Context context) {
        DBOperations.addRow(context, this.eventId, startDateInUnixFormat, this.eventType, DEFAULT_ACTION_GROUP_ID_FOR_MOOD, DEFAULT_ENDTIME_FOR_MOOD);
    }

}
