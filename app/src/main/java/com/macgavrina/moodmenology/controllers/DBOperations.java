package com.macgavrina.moodmenology.controllers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.model.Icons;
import com.macgavrina.moodmenology.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Irina on 09.02.2018.
 */

public abstract class DBOperations {

    private static final String LOG_TAG = "MoodMenology";

    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_END_DATE = "endDate";
    private static final String ATTRIBUTE_NAME_EVENT_ID = "eventId";
    private static final String ATTRIBUTE_NAME_GROUP_ID = "groupId";
    private static final String ATTRIBUTE_NAME_LL = "ll";
    private static final String ATTRIBUTE_NAME_DURATION = "duration";

    public static void addRow(final DBHelper dbHelper,
                              final Integer selectedMoodIdForActivity,
                              final long startTimeInMillis,
                              final Integer eventType,
                              final Integer actionGroupId,
                              final long endTimeInMillis) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create object for data
        //ToDo REFACT почитать про ContentValues и понять лучше ли он чем Map (после обучения по ContentProvider)
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.EVENT_ID_COLUMN_NAME, selectedMoodIdForActivity);
        cv.put(DBHelper.START_DATETIME_COLUMN_NAME, startTimeInMillis);
        cv.put(DBHelper.EVENT_TYPE_COLUMN_NAME, eventType);
        cv.put(DBHelper.EVENT_GROUP_ID_COLUMN_NAME, actionGroupId);
        cv.put(DBHelper.END_DATETIME_COLUMN_NAME, endTimeInMillis);

        long rowID = db.insert(DBHelper.TABLE_NAME, null, cv);

        dbHelper.close();

        //ToDo REFACT понять имеет ли смысл использовать StringBuffer при создании строки сразу (когда она больше не меняется)
        Log.d(LOG_TAG, new StringBuffer().append("FillDataTabs.onTimeSetListener: Added row with MoodId=").append(selectedMoodIdForActivity).append(", Time: ").append(startTimeInMillis).append(", rowId=").append(rowID).toString());

    }

    //ToDo REFACT убрать dbHelper из входных параметров всех методов DBOperations
    public static void deleteAllDayData(final DBHelper dbHelper,
                                        final String selectedDayStartDateString,
                                        final String selectedDayEndDateString) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete(DBHelper.TABLE_NAME, new StringBuffer().append(DBHelper.START_DATETIME_COLUMN_NAME).append(" between ").append(selectedDayStartDateString).append(" and ").append(selectedDayEndDateString).toString(), null);

        Log.d(LOG_TAG, "FillDataTabs.deleteAllDayData: deleted rows count = " + delCount);

        dbHelper.close();
    }

    public static Map<String,Object> getEvent (final DBHelper dbHelper, final Integer rowId) {

        Map<String, Object> m = new HashMap<String, Object>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        String selection = DBHelper.ROWID_COLUMN_NAME + " = ?";
        String[] selectionArgs = new String[] {rowId.toString()};
        Cursor c = db.query(DBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

        if (c.moveToFirst()) {

            final int idColIndex = c.getColumnIndex(DBHelper.ID_COLUMN_NAME);
            final int eventIdColIndex = c.getColumnIndex(DBHelper.EVENT_ID_COLUMN_NAME);
            final int startDatetimeColIndex = c.getColumnIndex(DBHelper.START_DATETIME_COLUMN_NAME);
            final int groupIdColIndex = c.getColumnIndex(DBHelper.EVENT_GROUP_ID_COLUMN_NAME);
            final int endDatetimeColIndex = c.getColumnIndex(DBHelper.END_DATETIME_COLUMN_NAME);

            do {

                m.put(ATTRIBUTE_NAME_START_DATE, c.getLong(startDatetimeColIndex));
                m.put(ATTRIBUTE_NAME_END_DATE, c.getLong(endDatetimeColIndex));
                m.put(ATTRIBUTE_NAME_EVENT_ID, c.getInt(eventIdColIndex));
                m.put(ATTRIBUTE_NAME_GROUP_ID, c.getInt(groupIdColIndex));

            } while (c.moveToNext());

        } else
            Log.d(LOG_TAG, "EditData.onCreate: rowId with"+rowId+" doesn't exist");

        c.close();

        dbHelper.close();

        return m;
    }

    public static ArrayList<Map<String,Object>> getEventListForTheDay (final DBHelper dbHelper,
                                                                       final String selectedDayStartDateStringFillData,
                                                                       final String selectedDayEndDateStringFillData,
                                                                       final Integer eventType) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Icons icons = new Icons();

        // Select all mood rows for the day; get Cursor
        final String selection = "((" + DBHelper.START_DATETIME_COLUMN_NAME + " >= ? and " +
                DBHelper.START_DATETIME_COLUMN_NAME + " < ?) or (" +
                DBHelper.END_DATETIME_COLUMN_NAME + " >= ? and " + DBHelper.END_DATETIME_COLUMN_NAME + " < ?)) and " +
                DBHelper.EVENT_TYPE_COLUMN_NAME + " == ?";
        final String[] selectionArgs = new String[]{selectedDayStartDateStringFillData,
                                                    selectedDayEndDateStringFillData,
                                                    selectedDayStartDateStringFillData,
                                                    selectedDayEndDateStringFillData,
                                                    String.valueOf(eventType)};
        Cursor c = db.query(DBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

        // Get rows count in DB to define required amount of rows in the listView
        Integer rowsCount = c.getCount();

        // Convert data to map required for adapter
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                rowsCount);

        Map<String, Object> m;

        // Define columns
        final int idColIndex = c.getColumnIndex(DBHelper.ID_COLUMN_NAME);
        final int moodColIndex = c.getColumnIndex(DBHelper.EVENT_ID_COLUMN_NAME);
        final int startDatetimeColIndex = c.getColumnIndex(DBHelper.START_DATETIME_COLUMN_NAME);
        final int groupColIndex = c.getColumnIndex(DBHelper.EVENT_GROUP_ID_COLUMN_NAME);
        final int endDatetimeColIndex = c.getColumnIndex(DBHelper.END_DATETIME_COLUMN_NAME);

        if (c.moveToFirst()) {
            do {
                String formattedStartDate = SmallFunctions.formatTimeWithCompare(c.getLong(startDatetimeColIndex), selectedDayStartDateStringFillData, selectedDayEndDateStringFillData);
                String formattedEndDate = SmallFunctions.formatTimeWithCompare(c.getLong(endDatetimeColIndex), selectedDayStartDateStringFillData, selectedDayEndDateStringFillData);
                String formattedDuration = SmallFunctions.formatDuration(c.getLong(endDatetimeColIndex)-c.getLong(startDatetimeColIndex));

                m = new HashMap<String, Object>();

                m.put(ATTRIBUTE_NAME_START_DATE, formattedStartDate);
                m.put(ATTRIBUTE_NAME_END_DATE, formattedEndDate);
                m.put(ATTRIBUTE_NAME_DURATION, formattedDuration);

                if (eventType == Event.EventTypes.actionEventTypeId.getId()){

                    int iconId = icons.getActionIconsId(c.getInt(groupColIndex), c.getInt(moodColIndex));
                    m.put(ATTRIBUTE_NAME_LL, iconId);

                }

                else {
                    m.put(ATTRIBUTE_NAME_LL, c.getInt(moodColIndex));
                }

                data.add(m);

            } while (c.moveToNext());

        } else
            Log.d(LOG_TAG, "0 rows");

        c.close();

        dbHelper.close();
        return data;
    }

    public static Integer[] getPositionRowIdMapping(final DBHelper dbHelper,
                                                    final String selectedDayStartDateStringFillData,
                                                    final String selectedDayEndDateStringFillData,
                                                    final int eventType) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Select all mood rows for the day; get Cursor
        final String selection = "((" + DBHelper.START_DATETIME_COLUMN_NAME + " >= ? and " +
                DBHelper.START_DATETIME_COLUMN_NAME + " < ?) or (" +
                DBHelper.END_DATETIME_COLUMN_NAME + " >= ? and " +
                DBHelper.END_DATETIME_COLUMN_NAME + " < ?)) and " +
                DBHelper.EVENT_TYPE_COLUMN_NAME + " == ?";
        final String[] selectionArgs = new String[]{selectedDayStartDateStringFillData,
                                                    selectedDayEndDateStringFillData,
                                                    selectedDayStartDateStringFillData,
                                                    selectedDayEndDateStringFillData,
                                                    String.valueOf(eventType)};
        Cursor c = db.query(DBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

        // Get rows count in DB to define required amount of rows in the listView
        Integer rowsCount = c.getCount();
        Integer[] positionRowIdMapping = new Integer[rowsCount];

        // Define columns
        int idColIndex = c.getColumnIndex(DBHelper.ID_COLUMN_NAME);

        int elementPosition = 0;

        if (c.moveToFirst()) {
            do {
                // Save rowId for each row (necessary to define rowId and send it to EditMood activity)
                positionRowIdMapping[elementPosition] = c.getInt(idColIndex);
                elementPosition++;


            } while (c.moveToNext());

        } else
            Log.d(LOG_TAG, "0 rows");

        c.close();

        dbHelper.close();
        return positionRowIdMapping;
    }

    public static void deleteRow(final DBHelper dbHelper, final Integer rowId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_NAME, DBHelper.ID_COLUMN_NAME + " = " + rowId, null);
        dbHelper.close();
    }

    public static void updateStartTime(final DBHelper dbHelper, final Integer rowId, final long timeInMillis) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.START_DATETIME_COLUMN_NAME, timeInMillis);
        db.update(DBHelper.TABLE_NAME, cv, DBHelper.ID_COLUMN_NAME + " = " + rowId, null);

        dbHelper.close();
    }

    public static void rmrf(DBHelper dbHelper) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DBHelper.TABLE_NAME, null, null);

        dbHelper.close();
    }

    public static void updateStartAndEndTime(DBHelper dbHelper, Integer rowId, Long startTimeInMillis, Long endTimeInMillis) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.START_DATETIME_COLUMN_NAME, startTimeInMillis);
        cv.put(DBHelper.END_DATETIME_COLUMN_NAME, endTimeInMillis);
        db.update(DBHelper.TABLE_NAME, cv, DBHelper.ID_COLUMN_NAME + " = " + rowId, null);

        dbHelper.close();
    }

    public static String getAllDataForEmail (final DBHelper dbHelper) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        StringBuffer emailTextStringBuffer = new StringBuffer().
                append(DBHelper.ROWID_COLUMN_NAME).append(";")
                .append(DBHelper.EVENT_ID_COLUMN_NAME).append(";")
                .append(DBHelper.EVENT_GROUP_ID_COLUMN_NAME).append(";")
                .append(DBHelper.START_DATETIME_COLUMN_NAME).append(";")
                .append(DBHelper.END_DATETIME_COLUMN_NAME).append("\n");

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

        // Get rows count in DB to define required amount of rows in the listView
        Integer rowsCount = c.getCount();

        // Define columns
        final int idColIndex = c.getColumnIndex(DBHelper.ID_COLUMN_NAME);
        final int moodColIndex = c.getColumnIndex(DBHelper.EVENT_ID_COLUMN_NAME);
        final int startDatetimeColIndex = c.getColumnIndex(DBHelper.START_DATETIME_COLUMN_NAME);
        final int groupColIndex = c.getColumnIndex(DBHelper.EVENT_GROUP_ID_COLUMN_NAME);
        final int endDatetimeColIndex = c.getColumnIndex(DBHelper.END_DATETIME_COLUMN_NAME);

        if (c.moveToFirst()) {
            do {

                emailTextStringBuffer.append(c.getInt(idColIndex)).append(";")
                        .append(c.getInt(moodColIndex)).append(";")
                        .append(c.getInt(groupColIndex)).append(";")
                        .append(c.getLong(startDatetimeColIndex)).append(";")
                        .append(c.getLong(endDatetimeColIndex)).append("\n");

            } while (c.moveToNext());

        } else
            emailTextStringBuffer.append("No data");

        c.close();

        dbHelper.close();


        return emailTextStringBuffer.toString();
    }
}
