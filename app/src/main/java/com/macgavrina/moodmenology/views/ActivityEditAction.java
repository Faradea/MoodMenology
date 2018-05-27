package com.macgavrina.moodmenology.views;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.model.ActionEvent;
import com.macgavrina.moodmenology.model.Icons;

public class ActivityEditAction extends AppCompatActivity implements View.OnClickListener {

    private static final String ROWID_KEY = "rowId";

    private static final long dayDurationInMillis = 86400000L;

    private ActionEvent actionEvent;

    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private Button saveButton;

    private java.util.Calendar dateAndTimeStart = java.util.Calendar.getInstance();
    private java.util.Calendar dateAndTimeEnd = java.util.Calendar.getInstance();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_action);

        Intent intent = getIntent();
        int rowId = intent.getIntExtra(ROWID_KEY, 0);
        actionEvent = new ActionEvent(this, rowId);

        ImageView actionImageView = (ImageView) findViewById(R.id.ActivityEditAction_actionImage);
        startTimeTextView = (TextView) findViewById(R.id.ActivityEditAction_startTimeText);
        endTimeTextView = (TextView) findViewById(R.id.ActivityEditAction_endTimeText);

        ImageButton editStartTimeButton = (ImageButton) findViewById(R.id.ActivityEditAction_editStartTimeImageButton);
        editStartTimeButton.setOnClickListener(this);

        ImageButton editEndTimeButton = (ImageButton) findViewById(R.id.ActivityEditAction_editEndTimeImageButton);
        editEndTimeButton.setOnClickListener(this);

        saveButton = (Button) findViewById(R.id.ActivityEditAction_saveButton);
        saveButton.setOnClickListener(this);
        saveButton.setEnabled(false);

        dateAndTimeStart.setTimeInMillis(actionEvent.getStartDateInUnixFormat());
        dateAndTimeEnd.setTimeInMillis(actionEvent.getEndDateInUnixFormat());

        setupStartTimeTextView();
        setupEndTimeTextView();

        Icons icons = new Icons();
        actionImageView.setImageResource(icons.getActionIconsId(actionEvent.getGroupId(), actionEvent.getEventId()));

        Button deleteButton = (Button) findViewById(R.id.ActivityEditAction_deleteButton);
        deleteButton.setOnClickListener(this);

        Log.d("Activity building is finished, rowId=" + rowId);
    }

    private void setupEndTimeTextView() {
        String endTimeText = getResources().getString(R.string.timeToText) + " " + SmallFunctions.formatDate(actionEvent.getEndDateInUnixFormat())
                + getResources().getString(R.string.timeSeparater) + " "
                + SmallFunctions.formatTime(actionEvent.getEndDateInUnixFormat());
        endTimeTextView.setText(endTimeText);
    }

    private void setupStartTimeTextView() {
        String startTimeText = getResources().getString(R.string.timeFromText) + " " + SmallFunctions.formatDate(actionEvent.getStartDateInUnixFormat())
                + getResources().getString(R.string.timeSeparater) + " "
                + SmallFunctions.formatTime(actionEvent.getStartDateInUnixFormat());
        startTimeTextView.setText(startTimeText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ActivityEditAction_deleteButton:

                Log.d("User has pressed Delete Button, start processing");

                actionEvent.deleteEvent(this);

                setResult(RESULT_OK, new Intent());
                finish();

                break;

            case R.id.ActivityEditAction_saveButton:

                Log.d("User has pressed Save Button, start processing");

                actionEvent.updateStartAndEndTime(this);

                setResult(RESULT_OK, new Intent());
                finish();

                break;

            case R.id.ActivityEditAction_editStartTimeImageButton:

                Log.d("User has pressed EditStartTime Button, start processing");

                setStartTime(v);

                break;

            case R.id.ActivityEditAction_editEndTimeImageButton:

                Log.d("User has pressed EditEndTime Button, start processing");

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

        Log.d("TimePicker dialog for startTime is displayed, initial time is " + SmallFunctions.formatTime(dateAndTimeStart.getTimeInMillis()));
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

            Log.d("Start time is set by user, startDate = " + SmallFunctions.formatDate(dateAndTimeStart.getTimeInMillis()) +
                    ", startTime = " + SmallFunctions.formatTime(dateAndTimeStart.getTimeInMillis()));
        }
    };

    private void setEndTime(final View v) {
        dateAndTimeEnd.setTimeInMillis(actionEvent.getEndDateInUnixFormat());

        new TimePickerDialog(this, tEnd,
                dateAndTimeEnd.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTimeEnd.get(java.util.Calendar.MINUTE), true)
                .show();

        Log.d("TimePicker dialog for endTime is displayed, initial time is " + SmallFunctions.formatTime(dateAndTimeEnd.getTimeInMillis()));

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

            Log.d("End time is set by user, endDate = " + SmallFunctions.formatDate(dateAndTimeStart.getTimeInMillis()) +
                    ", endTime = " + SmallFunctions.formatTime(dateAndTimeStart.getTimeInMillis()));
        }
    };
}
