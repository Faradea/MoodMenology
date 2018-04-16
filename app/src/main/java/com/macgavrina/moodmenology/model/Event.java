package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

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

    public void deleteEvent(final Context context) {
        DBOperations.deleteRow(context, this.rowId);
    }

    public void updateStartTime(final Context context) {
        DBOperations.updateStartTime(context, this.rowId, this.startDateInUnixFormat);
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
