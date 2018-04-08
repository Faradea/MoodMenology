package com.macgavrina.moodmenology.model;

import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.controllers.DBOperations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Irina on 13.02.2018.
 */

public class ActionEvent extends Event{

    //ToDo REFACT разобраться когда лучше Integer/Long, а когда - int/long

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
    public static ActionEvent getActionData(DBHelper dbHelper, Integer rowId) {

        Map<String,Object> eventData = DBOperations.getEvent(dbHelper, rowId);

        ActionEvent actionEvent = new ActionEvent(
                (Long) eventData.get(ATTRIBUTE_NAME_START_DATE),
                (Long) eventData.get(ATTRIBUTE_NAME_END_DATE),
                (Integer) eventData.get(ATTRIBUTE_NAME_EVENT_ID),
                (Integer) eventData.get(ATTRIBUTE_NAME_GROUP_ID)
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

    public Long getDuration(){
        Long duration = this.endDateInUnixFormat - this.startDateInUnixFormat;
        return duration;
    }

    public void saveToDB(final DBHelper dbHelper) {
        DBOperations.addRow(dbHelper, this.eventId, this.startDateInUnixFormat, this.eventType, this.actionGroupId, this.endDateInUnixFormat);
    }

    public void updateStartAndEndTime(DBHelper dbHelper) {
        DBOperations.updateStartAndEndTime(dbHelper, this.rowId, this.startDateInUnixFormat, this.endDateInUnixFormat);
    }

    @Override
    public void setStartTime(final Long startDateInUnixFormat) {
        this.startDateInUnixFormat = startDateInUnixFormat;
        if (this.startDateInUnixFormat >= this.endDateInUnixFormat) {
            this.endDateInUnixFormat = this.startDateInUnixFormat + defaultDurationInMillis;
        }
    }

    public void setEndTime(final Long endDate) {
        this.endDateInUnixFormat = endDate;
    }
}
