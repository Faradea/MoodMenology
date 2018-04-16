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
import com.macgavrina.moodmenology.model.Icons;
import com.macgavrina.moodmenology.model.MoodEvent;

public class ActivityEditMood extends AppCompatActivity implements View.OnClickListener {

    private static final String ROWID_KEY = "rowId";

    private MoodEvent moodEvent;
    private Icons icons;

    private ImageView moodImageView;
    private Integer rowId;
    private TextView selectedDateTextViewEditData, selectedTimeTextViewEditData;
    private Button saveButtonEditData, deleteButtonEditData;
    private ImageButton editTimeEditData;

    private java.util.Calendar dateAndTime = java.util.Calendar.getInstance();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mood);

        Intent intent = getIntent();
        rowId = intent.getIntExtra(ROWID_KEY, 0);

        selectedDateTextViewEditData = (TextView) findViewById(R.id.ActivityEditMood_dateText);
        selectedTimeTextViewEditData = (TextView) findViewById(R.id.ActivityEditMood_startTimeText);

        editTimeEditData = (ImageButton) findViewById(R.id.ActivityEditMood_editTimeImageButton);
        editTimeEditData.setOnClickListener(this);

        saveButtonEditData = (Button) findViewById(R.id.ActivityEditMood_saveButton);
        saveButtonEditData.setOnClickListener(this);
        saveButtonEditData.setEnabled(false);

        deleteButtonEditData = (Button) findViewById(R.id.ActivityEditMood_deleteButton);
        deleteButtonEditData.setOnClickListener(this);

        moodImageView = (ImageView) findViewById(R.id.ActivityEditMood_actionImage);

        moodEvent = MoodEvent.getMoodData(this, rowId);
        selectedDateTextViewEditData.setText(SmallFunctions.formatDate(moodEvent.getStartDateInUnixFormat()));
        selectedTimeTextViewEditData.setText(SmallFunctions.formatTime(moodEvent.getStartDateInUnixFormat()));

        icons = new Icons();
        moodImageView.setImageResource(icons.getMoodForEditIconId(moodEvent.getEventId()));

        Log.d("Activity building is finished, for rowId = " + rowId);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ActivityEditMood_deleteButton:

                Log.d("User has pressed Delete button, start processing");

                moodEvent.deleteEvent(this);

                setResult(RESULT_OK, new Intent());
                finish();

                break;

            case R.id.ActivityEditMood_editTimeImageButton:

                Log.d("User has pressed EditTime button, start processing");

                setTime(v);

                break;

            case R.id.ActivityEditMood_saveButton:

                Log.d("User has pressed Save button, start processing");

                moodEvent.updateStartTime(this);

                setResult(RESULT_OK, new Intent());
                finish();

                break;

            default:
                break;
        }
    }

    private void setTime(final View v) {
        dateAndTime.setTimeInMillis(moodEvent.getStartDateInUnixFormat());

        new TimePickerDialog(this, t,
                dateAndTime.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTime.get(java.util.Calendar.MINUTE), true)
                .show();
        Log.d("TimePicker dialog for startTime is displayed, initial time is " + SmallFunctions.formatTime(dateAndTime.getTimeInMillis()));

    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(java.util.Calendar.MINUTE, minute);
            moodEvent.setStartTime(dateAndTime.getTimeInMillis());

            selectedTimeTextViewEditData.setText(SmallFunctions.formatTime(moodEvent.getStartDateInUnixFormat()));
            saveButtonEditData.setEnabled(true);

            Log.d("Start time is set by user, startDate = " + SmallFunctions.formatDate(dateAndTime.getTimeInMillis()) +
                    ", startTime = " + SmallFunctions.formatTime(dateAndTime.getTimeInMillis()));
        }
    };

}
