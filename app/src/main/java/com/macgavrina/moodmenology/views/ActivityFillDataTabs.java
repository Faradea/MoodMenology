package com.macgavrina.moodmenology.views;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.model.MoodEvent;
import com.macgavrina.moodmenology.model.SelectedDay;
import com.macgavrina.moodmenology.viewadapters.MainMenu;
import com.macgavrina.moodmenology.viewadapters.SimpleFragmentPagerAdapter;

import java.util.Calendar;


/**
 * Created by Irina on 28.12.2017.
 */

public class ActivityFillDataTabs extends AppCompatActivity implements View.OnClickListener, IMoodFragmentInteractionListener, IActionsFragmentInteractionListener {


    private static final int EDIT_MOOD_REQUEST_CODE = 1;
    private static final int EDIT_ACTION_REQUEST_CODE = 2;
    private static final int ADD_ACTION_REQUEST_CODE = 3;

    private static final String ACTION_GROUPID_KEY="actionGroupId";
    private static final String ROWID_KEY="rowId";
    private static final String DATE_IN_MILLIS_KEY="selectedDateInMillis";

    private static final int MOOD_FRAGMENT_ID = 0;
    private static final int ACTION_FRAGMENT_ID = 1;

    private ActivityFillDataTabs context;
    private SelectedDay selectedDay;

    FragmentFillDataMood moodFragment;
    FragmentFillDataActions actionFragment;
    private TextView selectedDateFillData;
    private ImageButton deleteAllButtonFillData;

    //ToDo REFACT убрать вот такие ни к чему не привязанные переменнные (они должны быть частью каких-то объектов или использоваться локально)
    private long selectedDayStartDate;
    private long selectedDayEndDate;
    private int selectedMoodId;
    private int selectedHourOfDayFillData;
    private int selectedMinuteFillData;

    private java.util.Calendar dateAndTime;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filldata_tabs);

        context = this;

        Intent intent = getIntent();
        long selectedDateInMillis = intent.getLongExtra(DATE_IN_MILLIS_KEY, System.currentTimeMillis());

        selectedDay = new SelectedDay(selectedDateInMillis);
        dateAndTime = selectedDay.getCurrentDateAndTime();

        selectedDayStartDate = selectedDay.getDayStartTimestamp();
        selectedDayEndDate = selectedDay.getDayEndTimestamp();

        setupHeader();

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.ActivtyFilldata_viewPager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), selectedDayStartDate, selectedDayEndDate);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.ActivtyFilldata_slidingTabs);
        tabLayout.setupWithViewPager(viewPager);

        moodFragment = (FragmentFillDataMood) adapter.getItem(MOOD_FRAGMENT_ID);
        actionFragment = (FragmentFillDataActions) adapter.getItem(ACTION_FRAGMENT_ID);

        Log.d("Activity building is finished, selected Date = " + SmallFunctions.formatDate(selectedDateInMillis));
    }

    @Override
    public void onResume() {

        super.onResume();

        java.util.Calendar currentDateAndTime = Calendar.getInstance();
        currentDateAndTime.setTimeInMillis(System.currentTimeMillis());

        dateAndTime.set(java.util.Calendar.HOUR_OF_DAY, currentDateAndTime.get(Calendar.HOUR_OF_DAY));
        dateAndTime.set(java.util.Calendar.MINUTE, currentDateAndTime.get(Calendar.MINUTE));

        Log.d("Initial date for TimePicker dialog is actualized");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // Delete all data button (trash icon beside the date) with alert dialog
            case R.id.ActivtyFilldata_deleteAllButton:

                Log.d("User has pressed DeleteAllDataForTheDay button");

                //ToDO REFACT Вынести тексты в string.xml
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFillDataTabs.this);
                builder.setTitle("Delete all data")
                        .setMessage("Delete all data for this day?")
                        .setCancelable(true)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.d("User has confirmed data deleting");
                                        DBOperations.deleteAllDayData(context, selectedDayStartDate, selectedDayEndDate);

                                        Toast.makeText(getBaseContext(), "All data for the day is deleted", Toast.LENGTH_SHORT).show();
                                        moodFragment.updateList();
                                        actionFragment.updateList();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Log.d("User has canceled data deleting");
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                Log.d("AlertDialog is displayed");
                break;
            default:
                break;
        }
    }

    // Process event from FillDataMood fragment (user selects mood from GridView)
    @Override
    public void setTimeEvent(final int eventId) {
        Log.d("Activity recieved setTimeEvent from FillDataMood fragment, selectedMoodId = " + selectedMoodId);
        View v = null;
        selectedMoodId = eventId;
        setTime(v);

    }

    // Process event from FillDataActions fragment (user selects action from GridView to edit it)
    @Override
    public void selectActionsGroupEvent(final int selectedActionsGroupId) {
        Log.d("Activity received selectActionsFroup event from FillDataAction fragment, groupId =" + selectedActionsGroupId);

        Intent intentActions = new Intent("com.macgavrina.moodmenology.add.action");
        intentActions.putExtra(ACTION_GROUPID_KEY, selectedActionsGroupId);
        intentActions.putExtra(DATE_IN_MILLIS_KEY, dateAndTime.getTimeInMillis());
        startActivityForResult(intentActions, ADD_ACTION_REQUEST_CODE);
    }

    @Override
    public void editActionRowEvent(int rowId) {
        Log.d("Activity received editActionRow event from FillDataAction fragment, rowId=" + rowId);
        Intent intentEdit = new Intent("com.macgavrina.moodmenology.edit.action");
        intentEdit.putExtra(ROWID_KEY, rowId);
        startActivityForResult(intentEdit, EDIT_ACTION_REQUEST_CODE);
    }

    // Process event from FillDataMood fragment (user selects row from ListView to edit it)
    @Override
    public void editMoodRowEvent(final int rowId) {
        Log.d("Activity received editMoodRow event from FillDataMood fragment, rowId="+rowId);
        Intent intentEdit = new Intent("com.macgavrina.moodmenology.edit.mood");
        intentEdit.putExtra(ROWID_KEY, rowId);
        startActivityForResult(intentEdit, EDIT_MOOD_REQUEST_CODE);
    }

    // OnTimeSetListener (user selects time)
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(java.util.Calendar.MINUTE, minute);
            selectedHourOfDayFillData = hourOfDay;
            selectedMinuteFillData = minute;

            Log.d("User has set time: " + SmallFunctions.formatTime(dateAndTime.getTimeInMillis()));

            MoodEvent moodEvent = new MoodEvent(dateAndTime.getTimeInMillis(), selectedMoodId);
            moodEvent.saveToDB(context);

            moodFragment.updateList();

        }
    };

    // Setup time
    private void setTime(final View v) {
        new TimePickerDialog(ActivityFillDataTabs.this, t,
                dateAndTime.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTime.get(java.util.Calendar.MINUTE), true)
                .show();
        Log.d("TimePickerDialog is displayed, initial time = " + SmallFunctions.formatTime(dateAndTime.getTimeInMillis()));
    }

    private void setupHeader() {

        deleteAllButtonFillData = (ImageButton) findViewById(R.id.ActivtyFilldata_deleteAllButton);
        deleteAllButtonFillData.setOnClickListener(this);

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

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (EDIT_MOOD_REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                    moodFragment.updateList();
                }
                break;
            case (EDIT_ACTION_REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                    actionFragment.updateList();
                }
                break;
            case (ADD_ACTION_REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                    actionFragment.updateList();
                }
                break;
            default:
                break;
        }
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
