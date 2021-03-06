package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.Map;

public class ActionEvent extends Event{

    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_END_DATE = "endDate";
    private static final String ATTRIBUTE_NAME_EVENT_ID = "eventId";
    private static final String ATTRIBUTE_NAME_GROUP_ID = "groupId";
    private static final long defaultDurationInMillis = 5400000L;

    private int actionGroupId;
    private long endDateInUnixFormat;

    public ActionEvent (final Context context, final int rowId, final boolean skipLoadData) {

        if (skipLoadData) {
            this.rowId = rowId;
        }

        else {

            Map<String, Object> eventData = DBOperations.getEvent(context, rowId);

            this.startDateInUnixFormat = (long) eventData.get(ATTRIBUTE_NAME_START_DATE);
            this.endDateInUnixFormat = (long) eventData.get(ATTRIBUTE_NAME_END_DATE);
            this.eventId = (int) eventData.get(ATTRIBUTE_NAME_EVENT_ID);
            this.actionGroupId = (int) eventData.get(ATTRIBUTE_NAME_GROUP_ID);
            this.rowId = rowId;
        }
    }

    public ActionEvent(final long startTimeInMillis, final int selectedActionId, final int actionsGroupId) {
        super();
        this.eventType = EventTypes.actionEventTypeId.getId();
        this.startDateInUnixFormat = startTimeInMillis;
        this.endDateInUnixFormat = startTimeInMillis + defaultDurationInMillis;
        this.actionGroupId = actionsGroupId;
        this.eventId = selectedActionId;
    }

    public int getGroupId(){
        return actionGroupId;
    };

    public long getEndDateInUnixFormat() {
        return endDateInUnixFormat;
    }

    public long getDuration(){
        return this.endDateInUnixFormat - this.startDateInUnixFormat;
    }

    @Override
    public void setStartTime(final long startDateInUnixFormat) {
        this.startDateInUnixFormat = startDateInUnixFormat;
        if (this.startDateInUnixFormat >= this.endDateInUnixFormat) {
            this.endDateInUnixFormat = this.startDateInUnixFormat + defaultDurationInMillis;
        }
    }

    public void setEndTime(final long endDate) {
        this.endDateInUnixFormat = endDate;
    }

    public void saveToDB(final Context context) {
        DBOperations.addRow(context, this.eventId, this.startDateInUnixFormat, this.eventType, this.actionGroupId, this.endDateInUnixFormat);
    }

    public void updateStartAndEndTime(Context context) {
        DBOperations.updateStartAndEndTime(context, this.rowId, this.startDateInUnixFormat, this.endDateInUnixFormat);
    }
}
