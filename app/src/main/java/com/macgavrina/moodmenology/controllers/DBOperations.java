package com.macgavrina.moodmenology.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.model.Event;
import com.macgavrina.moodmenology.model.Icons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class DBOperations {

    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_END_DATE = "endDate";
    private static final String ATTRIBUTE_NAME_EVENT_ID = "eventId";
    private static final String ATTRIBUTE_NAME_GROUP_ID = "groupId";
    private static final String ATTRIBUTE_NAME_IMAGE = "image";
    private static final String ATTRIBUTE_NAME_LL = "ll";
    private static final String ATTRIBUTE_NAME_DURATION = "duration";

    public static void addRow(final Context context,
                              final int selectedMoodIdForActivity,
                              final long startTimeInMillis,
                              final int eventType,
                              final int actionGroupId,
                              final long endTimeInMillis) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.EVENT_ID_COLUMN_NAME, selectedMoodIdForActivity);
        cv.put(DBHelper.START_DATETIME_COLUMN_NAME, startTimeInMillis);
        cv.put(DBHelper.EVENT_TYPE_COLUMN_NAME, eventType);
        cv.put(DBHelper.EVENT_GROUP_ID_COLUMN_NAME, actionGroupId);
        cv.put(DBHelper.END_DATETIME_COLUMN_NAME, endTimeInMillis);

        long rowID = db.insert(DBHelper.TABLE_NAME, null, cv);

        dbHelper.close();

        Log.d("Added row with EventId = " + selectedMoodIdForActivity +
                ", EventGroupId = " + actionGroupId +
                ", StartDate = " + SmallFunctions.formatDate(startTimeInMillis) +
                ", StartTime = " + SmallFunctions.formatTime(startTimeInMillis) +
                ", EndDate = " + SmallFunctions.formatDate(endTimeInMillis) +
                ", EndTime = " + SmallFunctions.formatTime(endTimeInMillis) +
                ", rowId = " + rowID);
    }

/*    public static void deleteAllDayData(final Context context,
                                        final long selectedDayStartDate,
                                        final long selectedDayEndDate) {

        DBHelper dbHelper = new DBHelper((FragmentActivity) context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete(DBHelper.TABLE_NAME, DBHelper.START_DATETIME_COLUMN_NAME + " between " + selectedDayStartDate
        + " and " + selectedDayEndDate, null);
ы
        Log.d("All data is deleted for the period: " +
                ", StartDay = " + SmallFunctions.formatDate(selectedDayStartDate) +
                ", StartTime = " + SmallFunctions.formatTime(selectedDayStartDate) +
                ", EndDay = " + SmallFunctions.formatDate(selectedDayEndDate) +
                ", EndTime = " + SmallFunctions.formatTime(selectedDayEndDate) +
                ", deleted rows count =" + delCount);

        dbHelper.close();
    }*/

    public static Map<String,Object> getEvent (final Context context, final int rowId) {

        Map<String, Object> m = new HashMap<>();

        DBHelper dbHelper = new DBHelper(context);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        String selection = DBHelper.ROWID_COLUMN_NAME + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(rowId)};
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
            Log.d("RowId with"+rowId+" doesn't exist");

        c.close();

        dbHelper.close();

        return m;
    }

    public static ArrayList<Map<String,Object>> getEventListForTheDay (final Context context,
                                                                       final long selectedDayStartDate,
                                                                       final long selectedDayEndDate,
                                                                       final int eventType) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Icons icons = new Icons();

        // Select all mood rows for the day; get Cursor
        final String selection = "((" + DBHelper.START_DATETIME_COLUMN_NAME + " >= ? and " +
                DBHelper.START_DATETIME_COLUMN_NAME + " < ?) or (" +
                DBHelper.END_DATETIME_COLUMN_NAME + " >= ? and " + DBHelper.END_DATETIME_COLUMN_NAME + " < ?)) and " +
                DBHelper.EVENT_TYPE_COLUMN_NAME + " == ?";

        final String[] selectionArgs = new String[]{String.valueOf(selectedDayStartDate),
                                                    String.valueOf(selectedDayEndDate),
                                                    String.valueOf(selectedDayStartDate),
                                                    String.valueOf(selectedDayEndDate),
                                                    String.valueOf(eventType)};
        Cursor c = db.query(DBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

        // Convert data to map required for adapter
        ArrayList<Map<String, Object>> data = new ArrayList<>(
                c.getCount());

        Map<String, Object> m;

        // Define columns
        final int idColIndex = c.getColumnIndex(DBHelper.ID_COLUMN_NAME);
        final int moodColIndex = c.getColumnIndex(DBHelper.EVENT_ID_COLUMN_NAME);
        final int startDatetimeColIndex = c.getColumnIndex(DBHelper.START_DATETIME_COLUMN_NAME);
        final int groupColIndex = c.getColumnIndex(DBHelper.EVENT_GROUP_ID_COLUMN_NAME);
        final int endDatetimeColIndex = c.getColumnIndex(DBHelper.END_DATETIME_COLUMN_NAME);

        if (c.moveToFirst()) {
            do {
                String formattedStartDate = SmallFunctions.formatTimeWithCompare(c.getLong(startDatetimeColIndex), selectedDayStartDate, selectedDayEndDate);
                String formattedEndDate = SmallFunctions.formatTimeWithCompare(c.getLong(endDatetimeColIndex), selectedDayStartDate, selectedDayEndDate);
                String formattedDuration = SmallFunctions.formatDuration(c.getLong(endDatetimeColIndex)-c.getLong(startDatetimeColIndex));

                m = new HashMap<>();

                m.put(ATTRIBUTE_NAME_START_DATE, formattedStartDate);
                m.put(ATTRIBUTE_NAME_END_DATE, formattedEndDate);
                m.put(ATTRIBUTE_NAME_DURATION, formattedDuration);

                if (eventType == Event.EventTypes.actionEventTypeId.getId()){

                    m.put(ATTRIBUTE_NAME_IMAGE, icons.getActionIconsId(c.getInt(groupColIndex), c.getInt(moodColIndex)));
                    m.put(ATTRIBUTE_NAME_LL, 0);

                }

                else {
                    m.put(ATTRIBUTE_NAME_LL, c.getInt(moodColIndex));
                }

                data.add(m);

            } while (c.moveToNext());

        } else
            Log.d("0 rows is found for" + "EventType = " + eventType +
                    ", DateStart = " + SmallFunctions.formatDate(selectedDayStartDate) +
                    ", TimeStart = " + SmallFunctions.formatTime(selectedDayStartDate) +
                    ", EndStart = " + SmallFunctions.formatDate(selectedDayEndDate) +
                    ", EndTime = " + SmallFunctions.formatTime(selectedDayEndDate));

        c.close();

        dbHelper.close();
        return data;
    }

    public static int[] getPositionRowIdMapping(final Context context,
                                                    final long selectedDayStartDate,
                                                    final long selectedDayEndDate,
                                                    final int eventType) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String selection = "((" + DBHelper.START_DATETIME_COLUMN_NAME + " >= ? and " +
                DBHelper.START_DATETIME_COLUMN_NAME + " < ?) or (" +
                DBHelper.END_DATETIME_COLUMN_NAME + " >= ? and " +
                DBHelper.END_DATETIME_COLUMN_NAME + " < ?)) and " +
                DBHelper.EVENT_TYPE_COLUMN_NAME + " == ?";
        final String[] selectionArgs = new String[]{String.valueOf(selectedDayStartDate),
                                                    String.valueOf(selectedDayEndDate),
                                                    String.valueOf(selectedDayStartDate),
                                                    String.valueOf(selectedDayEndDate),
                                                    String.valueOf(eventType)};
        Cursor c = db.query(DBHelper.TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

        // Get rows count in DB to define required amount of rows in the listView
        int[] positionRowIdMapping = new int[c.getCount()];

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

            Log.d("Position<->RowId mapping is empty for: " +
                    "EventType = " + eventType +
                    ", DateStart = " + SmallFunctions.formatDate(selectedDayStartDate) +
                    ", TimeStart = " + SmallFunctions.formatTime(selectedDayStartDate) +
                    ", EndStart = " + SmallFunctions.formatDate(selectedDayEndDate) +
                    ", EndTime = " + SmallFunctions.formatTime(selectedDayEndDate));

        c.close();

        dbHelper.close();
        return positionRowIdMapping;
    }

    public static void deleteRow(final Context context, final int rowId) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_NAME, DBHelper.ID_COLUMN_NAME + " = " + rowId, null);
        dbHelper.close();
        Log.d("Row with rowId = " + rowId + "is deleted");
    }

    public static void updateStartTime(final Context context, final int rowId, final long timeInMillis) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.START_DATETIME_COLUMN_NAME, timeInMillis);
        db.update(DBHelper.TABLE_NAME, cv, DBHelper.ID_COLUMN_NAME + " = " + rowId, null);

        dbHelper.close();
        Log.d("StartTime is updated for row with rowId = " + rowId +
                ". New startDate = " + SmallFunctions.formatDate(timeInMillis) +
                ", startTime = " + SmallFunctions.formatTime(timeInMillis));
    }

    public static void updateStartAndEndTime(final Context context, final int rowId, final long startTimeInMillis, final long endTimeInMillis) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelper.START_DATETIME_COLUMN_NAME, startTimeInMillis);
        cv.put(DBHelper.END_DATETIME_COLUMN_NAME, endTimeInMillis);
        db.update(DBHelper.TABLE_NAME, cv, DBHelper.ID_COLUMN_NAME + " = " + rowId, null);

        dbHelper.close();

        Log.d("StartTime and EndTime are updated for row with rowId = " + rowId +
                ". New startDate = " + SmallFunctions.formatDate(startTimeInMillis) +
                ", startTime = " + SmallFunctions.formatTime(startTimeInMillis) +
                ". New endDate = " + SmallFunctions.formatDate(endTimeInMillis) +
                ", endTime = " + SmallFunctions.formatTime(endTimeInMillis));
    }

    public static String getAllDataForEmail (final Context context) {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder emailTextStringBuffer = new StringBuilder().
                append(DBHelper.ROWID_COLUMN_NAME).append(";")
                .append(DBHelper.EVENT_ID_COLUMN_NAME).append(";")
                .append(DBHelper.EVENT_GROUP_ID_COLUMN_NAME).append(";")
                .append(DBHelper.START_DATETIME_COLUMN_NAME).append(";")
                .append(DBHelper.END_DATETIME_COLUMN_NAME).append("\n");

        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, DBHelper.START_DATETIME_COLUMN_NAME);

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
            emailTextStringBuffer.append("There is no any data in application");

        c.close();

        dbHelper.close();

        Log.d("All data is sent");

        return emailTextStringBuffer.toString();

    }
}
