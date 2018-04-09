package com.macgavrina.moodmenology.views;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.model.ActionEvent;
import com.macgavrina.moodmenology.model.Icons;

/**
 * Created by Irina on 24.02.2018.
 */

public class ActivityEditAction extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "MoodMenology";
    private static final String ROWID_KEY = "rowId";

    private static final Long dayDurationInMillis = Long.valueOf(86400000);

    private DBHelper dbHelper;
    private ActionEvent actionEvent;
    private Icons icons;

    private Integer rowId;

    private ImageView actionImageView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private Button deleteButton;
    private Button saveButton;
    private ImageButton editStartTimeButton;
    private ImageButton editEndTimeButton;

    private java.util.Calendar dateAndTimeStart = java.util.Calendar.getInstance();
    private java.util.Calendar dateAndTimeEnd = java.util.Calendar.getInstance();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_action);

        Intent intent = getIntent();
        rowId = intent.getIntExtra(ROWID_KEY, 0);

        actionImageView = (ImageView) findViewById(R.id.ActivityEditAction_actionImage);
        startTimeTextView = (TextView) findViewById(R.id.ActivityEditAction_startTimeText);
        endTimeTextView = (TextView) findViewById(R.id.ActivityEditAction_endTimeText);

        editStartTimeButton = (ImageButton) findViewById(R.id.ActivityEditAction_editStartTimeImageButton);
        editStartTimeButton.setOnClickListener(this);

        editEndTimeButton = (ImageButton) findViewById(R.id.ActivityEditAction_editEndTimeImageButton);
        editEndTimeButton.setOnClickListener(this);

        saveButton = (Button) findViewById(R.id.ActivityEditAction_saveButton);
        saveButton.setOnClickListener(this);
        saveButton.setEnabled(false);

        dbHelper = new DBHelper(this);
        actionEvent = ActionEvent.getActionData(dbHelper, rowId);

        dateAndTimeStart.setTimeInMillis(actionEvent.getStartDateInUnixFormat());
        dateAndTimeEnd.setTimeInMillis(actionEvent.getEndDateInUnixFormat());

        setupStartTimeTextView();
        setupEndTimeTextView();

        icons = new Icons();
        actionImageView.setImageResource(icons.getActionIconsId(actionEvent.getGroupId(), actionEvent.getEventId()));

        deleteButton = (Button) findViewById(R.id.ActivityEditAction_deleteButton);
        deleteButton.setOnClickListener(this);

        Log.d(LOG_TAG, "EditActionActivity.onCreate: EditAction activity building is finished, rowId=" + rowId);
    }

    private void setupEndTimeTextView() {
        endTimeTextView.setText("To: " + SmallFunctions.formatDate(actionEvent.getEndDateInUnixFormat()) + ", "
                + SmallFunctions.formatTime(actionEvent.getEndDateInUnixFormat()));
    }

    private void setupStartTimeTextView() {
        startTimeTextView.setText("From: " + SmallFunctions.formatDate(actionEvent.getStartDateInUnixFormat()) + ", "
                + SmallFunctions.formatTime(actionEvent.getStartDateInUnixFormat()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ActivityEditAction_deleteButton:

                actionEvent.deleteEvent(dbHelper);

                setResult(RESULT_OK, new Intent());
                finish();

                break;

            case R.id.ActivityEditAction_saveButton:

                actionEvent.updateStartAndEndTime(dbHelper);

                setResult(RESULT_OK, new Intent());
                finish();

                break;

            case R.id.ActivityEditAction_editStartTimeImageButton:

                setStartTime(v);

                break;

            case R.id.ActivityEditAction_editEndTimeImageButton:

                setEndTime(v);

                break;

            default:
                break;
        }
    }

    private void setStartTime(final View v) {
        dateAndTimeStart.setTimeInMillis(actionEvent.getStartDateInUnixFormat());

        new TimePickerDialog(this, tStart,
                dateAndTimeStart.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTimeStart.get(java.util.Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener tStart=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTimeStart.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeStart.set(java.util.Calendar.MINUTE, minute);

            actionEvent.setStartTime(dateAndTimeStart.getTimeInMillis());
            setupStartTimeTextView();
            setupEndTimeTextView();
            //startTimeTextView.setText("From: "+SmallFunctions.formatTime(actionEvent.getStartDateInUnixFormat()));
            //endTimeTextView.setText("To: " + SmallFunctions.formatTime(actionEvent.getEndDateInUnixFormat()));

            saveButton.setEnabled(true);
        }
    };

    private void setEndTime(final View v) {
        dateAndTimeEnd.setTimeInMillis(actionEvent.getEndDateInUnixFormat());

        new TimePickerDialog(this, tEnd,
                dateAndTimeEnd.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTimeEnd.get(java.util.Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener tEnd=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTimeEnd.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeEnd.set(java.util.Calendar.MINUTE, minute);

            if (dateAndTimeEnd.getTimeInMillis()<dateAndTimeStart.getTimeInMillis()) {
                dateAndTimeEnd.setTimeInMillis(dateAndTimeEnd.getTimeInMillis()+dayDurationInMillis);
            }

            actionEvent.setEndTime(dateAndTimeEnd.getTimeInMillis());

            setupEndTimeTextView();
            //endTimeTextView.setText("From: "+SmallFunctions.formatTime(actionEvent.getEndDateInUnixFormat()));
            saveButton.setEnabled(true);
        }
    };
}
