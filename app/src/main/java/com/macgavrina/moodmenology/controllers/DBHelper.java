package com.macgavrina.moodmenology.controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.macgavrina.moodmenology.logging.Log;

public class DBHelper extends SQLiteOpenHelper {


    static final String DB_NAME = "myDB";
    static final String EVENT_ID_COLUMN_NAME = "eventId";
    static final String START_DATETIME_COLUMN_NAME = "startDateTime";
    static final String ID_COLUMN_NAME = "id";
    static final String TABLE_NAME = "Events";
    static final String EVENT_TYPE_COLUMN_NAME = "eventType";
    static final String END_DATETIME_COLUMN_NAME = "endDateTime";
    static final String EVENT_GROUP_ID_COLUMN_NAME = "eventGroupId";
    static final String ROWID_COLUMN_NAME = "rowId";

    static final int DB_VERSION=1;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    // Create DB
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + " (" +
                        ID_COLUMN_NAME + " integer primary key autoincrement," +
                        EVENT_ID_COLUMN_NAME + " Integer," +
                        EVENT_GROUP_ID_COLUMN_NAME + " Integer," +
                        EVENT_TYPE_COLUMN_NAME + " Integer," +
                        START_DATETIME_COLUMN_NAME + " Long," +
                        END_DATETIME_COLUMN_NAME + " Long" +
                        ");"
                );

        Log.d("DataBase is created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
