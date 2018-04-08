package com.macgavrina.moodmenology.controllers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


/**
 * Created by Irina on 09.02.2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "MoodMenology";

    private static final String DB_NAME = "myDB";
    private static final String EVENT_ID_COLUMN_NAME = "eventId";
    private static final String START_DATETIME_COLUMN_NAME = "startDateTime";
    private static final String ID_COLUMN_NAME = "id";
    private static final String TABLE_NAME = "Events";
    private static final String EVENT_TYPE_COLUMN_NAME = "eventType";
    private static final String END_DATETIME_COLUMN_NAME = "endDateTime";
    private static final String EVENT_GROUP_ID_COLUMN_NAME = "eventGroupId";

    private static final int DB_VERSION=1;

    public DBHelper(FragmentActivity context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create DB
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(new StringBuffer("create table ").append(TABLE_NAME).append(" (")
                .append(ID_COLUMN_NAME).append(" integer primary key autoincrement,")
                .append(EVENT_ID_COLUMN_NAME).append(" Integer,")
                .append(EVENT_GROUP_ID_COLUMN_NAME).append(" Integer,")
                .append(EVENT_TYPE_COLUMN_NAME).append(" Integer,")
                .append(START_DATETIME_COLUMN_NAME).append(" Long,")
                .append(END_DATETIME_COLUMN_NAME).append(" Long")
                .append(");").toString()
                );

        Log.d(LOG_TAG, "FillDataTabs.onCreate DB: DB is created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
