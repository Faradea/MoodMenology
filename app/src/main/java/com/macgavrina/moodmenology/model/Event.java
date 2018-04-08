package com.macgavrina.moodmenology.model;

import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Irina on 09.02.2018.
 */

public abstract class Event {

    protected Integer rowId;
    protected Integer eventType;
    protected Integer eventId;
    protected Long startDateInUnixFormat;

    public Integer getEventId() {
        return this.eventId;
    }

    public Long getStartDateInUnixFormat() {
        return this.startDateInUnixFormat;
    }

    public void setStartTime(final Long startDateInUnixFormat) {
        this.startDateInUnixFormat = startDateInUnixFormat;
    }

    public void deleteEvent(final DBHelper dbHelper) {
        DBOperations.deleteRow(dbHelper, this.rowId);
    }

    public void updateStartTime(final DBHelper dbHelper) {
        DBOperations.updateStartTime(dbHelper, this.rowId, this.startDateInUnixFormat);
    }

    public enum EventTypes {
        moodEventTypeId(0), actionEventTypeId(1);

        private final int typeId;

        EventTypes(int i) {
            typeId = i;
        }

        public Integer getId() {
            return typeId;
        }
    }
}
