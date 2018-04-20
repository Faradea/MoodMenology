package com.macgavrina.moodmenology.model;

import android.content.Context;

import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.Map;

/**
 * Created by Irina on 13.02.2018.
 */

public class ActionEvent extends Event{

    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_END_DATE = "endDate";
    private static final String ATTRIBUTE_NAME_EVENT_ID = "eventId";
    private static final String ATTRIBUTE_NAME_GROUP_ID = "groupId";
    private static final long defaultDurationInMillis = Long.valueOf(5400000);

    private int actionGroupId;
    private long endDateInUnixFormat;

    public ActionEvent(final long timeInMillis, final int selectedActionId, final int actionsGroupId) {
        super();
        this.eventType = EventTypes.actionEventTypeId.getId();
        this.startDateInUnixFormat = timeInMillis;
        this.endDateInUnixFormat = timeInMillis + defaultDurationInMillis;
        this.actionGroupId = actionsGroupId;
        this.eventId = selectedActionId;
    }

    public ActionEvent(final long startTimeInMillis, final long endTimeInMillis, final int selectedActionId, final int actionsGroupId) {
        super();
        this.eventType = EventTypes.actionEventTypeId.getId();
        this.startDateInUnixFormat = startTimeInMillis;
        this.endDateInUnixFormat = endTimeInMillis;
        this.actionGroupId = actionsGroupId;
        this.eventId = selectedActionId;
    }

    //ToDo REFACT подумать правильная ли это конструкция - со static методом
    public static ActionEvent getActionData(Context context, int rowId) {

        Map<String,Object> eventData = DBOperations.getEvent(context, rowId);

        ActionEvent actionEvent = new ActionEvent(
                (long) eventData.get(ATTRIBUTE_NAME_START_DATE),
                (long) eventData.get(ATTRIBUTE_NAME_END_DATE),
                (int) eventData.get(ATTRIBUTE_NAME_EVENT_ID),
                (int) eventData.get(ATTRIBUTE_NAME_GROUP_ID)
        );

        actionEvent.rowId = rowId;
        return actionEvent;
    }

    public int getGroupId(){
        return actionGroupId;
    };

    public long getEndDateInUnixFormat() {
        return endDateInUnixFormat;
    }

    public long getDuration(){
        long duration = this.endDateInUnixFormat - this.startDateInUnixFormat;
        return duration;
    }

    public void saveToDB(final Context context) {
        DBOperations.addRow(context, this.eventId, this.startDateInUnixFormat, this.eventType, this.actionGroupId, this.endDateInUnixFormat);
    }

    public void updateStartAndEndTime(Context context) {
        DBOperations.updateStartAndEndTime(context, this.rowId, this.startDateInUnixFormat, this.endDateInUnixFormat);
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
}
