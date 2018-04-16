package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.Map;

/**
 * Created by Irina on 09.02.2018.
 */

public class MoodEvent extends Event {

    final static String ATTRIBUTE_NAME_START_DATE = "startDate";
    final static String ATTRIBUTE_NAME_EVENT_ID = "eventId";

    public MoodEvent(final long timeInMillis, final Integer selectedMoodId) {
        super();
        this.eventType = EventTypes.moodEventTypeId.getId();
        this.startDateInUnixFormat = timeInMillis;
        this.eventId = selectedMoodId;
    }

    public static MoodEvent getMoodData(final Context context, final Integer rowId) {

        Map<String,Object> eventData = DBOperations.getEvent(context, rowId);

        MoodEvent moodEvent = new MoodEvent(
                (Long) eventData.get(ATTRIBUTE_NAME_START_DATE),
                (Integer) eventData.get(ATTRIBUTE_NAME_EVENT_ID)

        );

        moodEvent.rowId = rowId;

        return moodEvent;
    }

    public void saveToDB(final Context context) {
        DBOperations.addRow(context, this.eventId, startDateInUnixFormat, this.eventType, 0, (long) 0);
    }

}
