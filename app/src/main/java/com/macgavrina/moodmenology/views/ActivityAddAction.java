package com.macgavrina.moodmenology.views;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.model.ActionEvent;
import com.macgavrina.moodmenology.model.Colors;
import com.macgavrina.moodmenology.model.Icons;
import com.macgavrina.moodmenology.viewadapters.MySimpleAdapterGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Irina on 05.02.2018.
 */

public class ActivityAddAction extends AppCompatActivity implements View.OnClickListener, IAddActionFragmentListener {

    private static final String LOG_TAG = "MoodMenology";
    private static final String ATTRIBUTE_NAME_GRID_IMAGE = "image";
    private static final String ATTRIBUTE_NAME_LL_GRID = "ll_grid";

    private static final String DATE_IN_MILLIS_KEY="selectedDateInMillis";
    private static final String ACTION_GROUP_ID_KEY="actionGroupId";
    private static final String ACTION_ID_KEY="actionId";
    private static final String FORMATTED_STARTTIME_KEY="formattedStartTime";
    private static final String FORMATTED_ENDTIME_KEY="formattedEndTime";
    private static final String FORMATTED_DURATION_KEY="formattedDuration";

    private static final int iconsType= Icons.IconTypes.actionIconsType.getId();

    private static final Long dayDurationInMillis = Long.valueOf(86400000);

    private Icons icons;
    private DBHelper dbHelper;
    private ActionEvent actionEvent;

    private Integer actionsGroupId;
    private Integer selectedActionId;
    private Long selectedDateInMillis;

    private Button saveButton;
    private GridView gridViewActionActivity;

    private java.util.Calendar dateAndTimeStart = java.util.Calendar.getInstance();
    private java.util.Calendar dateAndTimeEnd = java.util.Calendar.getInstance();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_action);

        Intent intent = getIntent();
        actionsGroupId = intent.getIntExtra(ACTION_GROUP_ID_KEY, 0);
        selectedDateInMillis = intent.getLongExtra(DATE_IN_MILLIS_KEY, System.currentTimeMillis());

        saveButton = (Button) findViewById(R.id.AddActionActivity_saveButton);
        saveButton.setOnClickListener(this);
        saveButton.setEnabled(false);

        setupDateAndTime();

        icons = new Icons();

        setupGridView();

        Log.d(LOG_TAG, "AddActionActivity.onCreate: activity building is finished, selectedActionsGroupId = " + actionsGroupId);
    }

    private void addActionEventFragment(final ActionEvent actionEvent) {
        // получаем экземпляр FragmentTransaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        // добавляем фрагмент
        FragmentActionEvent actionEventFragment = new FragmentActionEvent();
        fragmentTransaction.replace(R.id.AddActionActivity_actionEventContainer, actionEventFragment);

        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt(ACTION_GROUP_ID_KEY, actionEvent.getGroupId());
        fragmentBundle.putInt(ACTION_ID_KEY, actionEvent.getEventId());
        fragmentBundle.putString(FORMATTED_STARTTIME_KEY, SmallFunctions.formatTime(actionEvent.getStartDateInUnixFormat()));
        fragmentBundle.putString(FORMATTED_ENDTIME_KEY, SmallFunctions.formatTime(actionEvent.getEndDateInUnixFormat()));
        fragmentBundle.putString(FORMATTED_DURATION_KEY, SmallFunctions.formatDuration(actionEvent.getDuration()));
        actionEventFragment.setArguments(fragmentBundle);

        fragmentTransaction.commit();

    }

    private void setupGridView() {
        //For GridView - list of action groups
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                icons.getActionIconsLenght(actionsGroupId));
        Map<String, Object> m;
        for (int i = 0; i < icons.getActionIconsLenght(actionsGroupId); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_GRID_IMAGE, i);
            m.put(ATTRIBUTE_NAME_LL_GRID, i);
            data.add(m);
        }

        String[] from = {ATTRIBUTE_NAME_GRID_IMAGE, ATTRIBUTE_NAME_LL_GRID};
        int[] to = {R.id.ItemUniversalGrid_iconImage, R.id.ItemUniversalGrid_layout};

        // sAdapterGrid - adapter for GridView
        MySimpleAdapterGrid sAdapterGrid = new MySimpleAdapterGrid(this, data,
                R.layout.item_universal_grid, from, to, iconsType, actionsGroupId);

        gridViewActionActivity = (GridView) findViewById(R.id.AddActionActivity_gridView);
        gridViewActionActivity.setAdapter(sAdapterGrid);
        sAdapterGrid.setViewBinder(new LayoutGridColorViewBinder());
        adjustGridView();

        // Select list item listener (for GridView)
        gridViewActionActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedActionId = position;
                Log.d(LOG_TAG, "AddActionActivity.onItemClick: grid item is selected, actionId = " + selectedActionId);
                setStartTime();
            }
        });
    }

    private void setupDateAndTime() {
        dateAndTimeStart.setTimeInMillis(selectedDateInMillis);
    }

    private void adjustGridView() {

        int numColumns;

        if (icons.getActionIconsLenght(actionsGroupId) < 4) {
            numColumns=3;
        } else {
            numColumns = (int) Math.ceil(Math.sqrt(icons.getActionIconsLenght(actionsGroupId)));
        }

        gridViewActionActivity.setNumColumns(numColumns);
        gridViewActionActivity.setVerticalSpacing(5);
        gridViewActionActivity.setHorizontalSpacing(5);
        gridViewActionActivity.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

    private void setStartTime() {
        new TimePickerDialog(this, tStart,
                dateAndTimeStart.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTimeStart.get(java.util.Calendar.MINUTE), true)
                .show();
        Log.d(LOG_TAG, "AddActionActivity.setTime: TimePickerDialog is displayed");
    }

    private void setEndTime() {
        dateAndTimeEnd.setTimeInMillis(actionEvent.getEndDateInUnixFormat());
        new TimePickerDialog(this, tEnd,
                dateAndTimeEnd.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTimeEnd.get(java.util.Calendar.MINUTE), true)
                .show();
        Log.d(LOG_TAG, "AddActionActivity.setTime: TimePickerDialog is displayed");
    }

    TimePickerDialog.OnTimeSetListener tStart = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTimeStart.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeStart.set(java.util.Calendar.MINUTE, minute);

            actionEvent = new ActionEvent(dateAndTimeStart.getTimeInMillis(), selectedActionId, actionsGroupId);

            addActionEventFragment(actionEvent);

            saveButton.setEnabled(true);
        }
    };

    TimePickerDialog.OnTimeSetListener tEnd = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTimeEnd.setTimeInMillis(actionEvent.getEndDateInUnixFormat());
            dateAndTimeEnd.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTimeEnd.set(java.util.Calendar.MINUTE, minute);

            if (dateAndTimeEnd.getTimeInMillis()<dateAndTimeStart.getTimeInMillis()) {
                dateAndTimeEnd.setTimeInMillis(dateAndTimeEnd.getTimeInMillis()+dayDurationInMillis);
            }

            actionEvent.setEndTime(dateAndTimeEnd.getTimeInMillis());

            addActionEventFragment(actionEvent);

        }
    };


    @Override
    public void onClick(final View v) {

        switch (v.getId()){
            case (R.id.AddActionActivity_saveButton):

                dbHelper = new DBHelper(this);
                actionEvent.saveToDB(dbHelper);

                setResult(RESULT_OK, new Intent());
                finish();

                break;
            default:
                break;
        }
    }

    @Override
    public void editStartDate() {
        Log.d(LOG_TAG, "AddActionActivity: editStartDate event");
        setStartTime();
    }

    @Override
    public void editEndDate() {
        Log.d(LOG_TAG, "AddActionActivity: editEndDate event");
        setEndTime();
    }

    //Colors for ListView
    public class LayoutGridColorViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(final View view, final Object data,
                                    final String textRepresentation) {

            Colors colors = new Colors(view);
            switch (view.getId()) {
                case R.id.ItemUniversalGrid_layout:
                    view.setBackgroundColor(colors.getActionColor());
                    return true;
            }
            return false;
        }
    }
}