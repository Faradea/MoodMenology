package com.macgavrina.moodmenology.model;

import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.controllers.DBOperations;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static MoodEvent getMoodData(final DBHelper dbHelper, final Integer rowId) {

        Map<String,Object> eventData = DBOperations.getEvent(dbHelper, rowId);

        MoodEvent moodEvent = new MoodEvent(
                (Long) eventData.get(ATTRIBUTE_NAME_START_DATE),
                (Integer) eventData.get(ATTRIBUTE_NAME_EVENT_ID)

        );

        moodEvent.rowId = rowId;

        return moodEvent;
    }

    public void saveToDB(final DBHelper dbHelper) {
        DBOperations.addRow(dbHelper, this.eventId, startDateInUnixFormat, this.eventType, 0, (long) 0);
    }

}
