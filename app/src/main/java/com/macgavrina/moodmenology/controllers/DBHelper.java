package com.macgavrina.moodmenology.controllers;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;

import com.macgavrina.moodmenology.logging.Log;


/**
 * Created by Irina on 09.02.2018.
 */

public class DBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "myDB";
    public static final String EVENT_ID_COLUMN_NAME = "eventId";
    public static final String START_DATETIME_COLUMN_NAME = "startDateTime";
    public static final String ID_COLUMN_NAME = "id";
    public static final String TABLE_NAME = "Events";
    public static final String EVENT_TYPE_COLUMN_NAME = "eventType";
    public static final String END_DATETIME_COLUMN_NAME = "endDateTime";
    public static final String EVENT_GROUP_ID_COLUMN_NAME = "eventGroupId";
    public static final String ROWID_COLUMN_NAME = "rowId";

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

        Log.d("DataBase is created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
