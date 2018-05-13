package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

public abstract class Event {

    protected int rowId;
    int eventType;
    int eventId;
    long startDateInUnixFormat;

    public int getEventId() {
        return this.eventId;
    }

    public long getStartDateInUnixFormat() {
        return this.startDateInUnixFormat;
    }

    public void setStartTime(final long startDateInUnixFormat) {
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

        public int getId() {
            return typeId;
        }
    }
}
