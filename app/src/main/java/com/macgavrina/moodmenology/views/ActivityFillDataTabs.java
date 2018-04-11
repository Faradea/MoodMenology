package com.macgavrina.moodmenology.views;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.model.MoodEvent;
import com.macgavrina.moodmenology.model.SelectedDay;
import com.macgavrina.moodmenology.viewadapters.MainMenu;
import com.macgavrina.moodmenology.viewadapters.SimpleFragmentPagerAdapter;

import java.util.Calendar;


/**
 * Created by Irina on 28.12.2017.
 */

public class ActivityFillDataTabs extends AppCompatActivity implements View.OnClickListener, IMoodFragmentInteractionListener, IActionsFragmentInteractionListener {

    private static final String LOG_TAG = "MoodMenology";

    private static final String ACTION_GROUPID_KEY="actionGroupId";
    private static final String ROWID_KEY="rowId";
    private static final String DATE_IN_MILLIS_KEY="selectedDateInMillis";

    private static final int MOOD_FRAGMENT_ID = 0;
    private static final int ACTION_FRAGMENT_ID = 1;

    private DBHelper dbHelper;
    private SelectedDay selectedDay;

    FragmentFillDataMood moodFragment;
    FragmentFillDataActions actionFragment;
    private TextView selectedDateFillData;
    private ImageButton deleteAllButtonFillData;
    private Integer selectedHourOfDayFillData;
    private Integer selectedMinuteFillData;

    //ToDo REFACT убрать вот такие ни к чему не привязанные переменнные (они должны быть частью каких-то объектов или использоваться локально)
    private String selectedDayStartDateString;
    private String selectedDayEndDateString;
    private Integer selectedMoodId;

    private java.util.Calendar dateAndTime;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filldata_tabs);

        Intent intent = getIntent();
        Long selectedDateInMillis = intent.getLongExtra(DATE_IN_MILLIS_KEY, System.currentTimeMillis());

        selectedDay = new SelectedDay(selectedDateInMillis);
        dateAndTime = selectedDay.getCurrentDateAndTime();

        selectedDayStartDateString = selectedDay.getDayStartStringTimestamp();
        selectedDayEndDateString = selectedDay.getDayEndStringTimestamp();

        setupHeader();

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.ActivtyFilldata_viewPager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), selectedDayStartDateString, selectedDayEndDateString);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.ActivtyFilldata_slidingTabs);
        tabLayout.setupWithViewPager(viewPager);

        moodFragment = (FragmentFillDataMood) adapter.getItem(MOOD_FRAGMENT_ID);
        actionFragment = (FragmentFillDataActions) adapter.getItem(ACTION_FRAGMENT_ID);

        dbHelper = new DBHelper(this);

        Log.d(LOG_TAG, "FillDataTabs.onCreate: FillDataTabs activity building is finished");
    }

    @Override
    public void onResume() {

        super.onResume();

        java.util.Calendar currentDateAndTime = Calendar.getInstance();
        currentDateAndTime.setTimeInMillis(System.currentTimeMillis());

        dateAndTime.set(java.util.Calendar.HOUR_OF_DAY, currentDateAndTime.get(Calendar.HOUR_OF_DAY));
        dateAndTime.set(java.util.Calendar.MINUTE, currentDateAndTime.get(Calendar.MINUTE));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // Delete all data button (trash icon beside the date) with alert dialog
            case R.id.ActivtyFilldata_deleteAllButton:

                Log.d(LOG_TAG, "FillDataTabs.onClick: delete all data for the day button is pressed");

                //ToDO REFACT Использовать для диалога xml-разметку
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFillDataTabs.this);
                builder.setTitle("Delete all data")
                        .setMessage("Delete all data for this day?")
                        .setCancelable(true)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.d(LOG_TAG, "FillDataTabs.onClick: user confirms data deleting");
                                        DBOperations.deleteAllDayData(dbHelper, selectedDayStartDateString, selectedDayEndDateString);
                                        Log.d(LOG_TAG,"Activity: startDate="+selectedDayStartDateString);

                                        moodFragment.updateListMethod(selectedDayStartDateString, selectedDayEndDateString);
                                        actionFragment.updateListMethod(selectedDayStartDateString,selectedDayEndDateString);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Log.d(LOG_TAG, "FillDataTabs.onClick: user cancels data deleting");
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:
                break;
        }
    }

    // Process event from FillDataMood fragment (user selects mood from GridView)
    @Override
    public void setTimeEvent(final Integer eventId) {
        View v = null;
        selectedMoodId = eventId;
        setTime(v);
    }

    // Process event from FillDataActions fragment (user selects action from GridView to edit it)
    @Override
    public void selectActionsGroupEvent(final Integer selectedActionsGroupId) {
        Log.d(LOG_TAG, "FillDataTabs.selectActionsGroupEvent: Activity received event with selectActionsGroupEvent=" + selectedActionsGroupId);

        Intent intentActions = new Intent("com.macgavrina.moodmenology.add.action");
        intentActions.putExtra(ACTION_GROUPID_KEY, selectedActionsGroupId);
        intentActions.putExtra(DATE_IN_MILLIS_KEY, dateAndTime.getTimeInMillis());
        startActivity(intentActions);
    }

    @Override
    public void editActionRowEvent(Integer rowId) {
        Log.d(LOG_TAG, "FillDataTabs.editActionRowEvent: Activity received event - editRow with rowId=" + rowId);
        Intent intentEdit = new Intent("com.macgavrina.moodmenology.edit.action");
        intentEdit.putExtra(ROWID_KEY, rowId);
        startActivity(intentEdit);
    }

    // Process event from FillDataMood fragment (user selects row from ListView to edit it)
    @Override
    public void editMoodRowEvent(final Integer rowId) {
        Log.d(LOG_TAG, "FillDataTabs.editMoodRowEvent: Activity received event - editRow with rowId="+rowId);
        Intent intentEdit = new Intent("com.macgavrina.moodmenology.edit.mood");
        intentEdit.putExtra(ROWID_KEY, rowId);
        startActivity(intentEdit);
    }

    // OnTimeSetListener (user selects time)
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(java.util.Calendar.MINUTE, minute);
            selectedHourOfDayFillData = hourOfDay;
            selectedMinuteFillData = minute;

            Log.d(LOG_TAG, "FillDataTabs.onTimeSetListener: User sets time: "+selectedHourOfDayFillData + ":" + selectedMinuteFillData);


            Log.d(LOG_TAG, "FillDataTabs.setTimeEvent: Activity received event with selectedMoodId=" + selectedMoodId);

            MoodEvent moodEvent = new MoodEvent(dateAndTime.getTimeInMillis(), selectedMoodId);
            moodEvent.saveToDB(dbHelper);

            Log.d(LOG_TAG, "FillDataTabs.onTimeSetListener: Update list after adding new row");
            moodFragment.updateListMethod(selectedDayStartDateString, selectedDayEndDateString);

        }
    };

    // Setup time
    private void setTime(final View v) {
        new TimePickerDialog(ActivityFillDataTabs.this, t,
                dateAndTime.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTime.get(java.util.Calendar.MINUTE), true)
                .show();
        Log.d(LOG_TAG, "FillDataTabs.setTime: TimePickerDialog is displayed");
    }

    private void setupHeader() {

        deleteAllButtonFillData = (ImageButton) findViewById(R.id.ActivtyFilldata_deleteAllButton);
        deleteAllButtonFillData.setOnClickListener(this);

        Integer hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
        Log.d(LOG_TAG, "hour="+String.valueOf(hour));

        String selectedDateStringFillData= SmallFunctions.formatDate(dateAndTime.getTimeInMillis());
        selectedDateFillData = (TextView) findViewById(R.id.ActivtyFilldata_dateText);
        selectedDateFillData.setText(selectedDateStringFillData);
    }

    // создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // обработка выбора пункта меню
    public boolean onOptionsItemSelected(MenuItem item) {

        MainMenu mainMenuProcessor = new MainMenu(item.getItemId());

        Intent intent = mainMenuProcessor.processOnMenyItemSelected(this);

        if (intent != null) {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);

    }

}
