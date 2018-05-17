package com.macgavrina.moodmenology.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.model.Colors;
import com.macgavrina.moodmenology.model.Event;
import com.macgavrina.moodmenology.model.Icons;
import com.macgavrina.moodmenology.viewadapters.MySimpleAdapterGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentFillDataMood extends Fragment {

    //ToDo NEW сделать вызов TimePicker в onClick вместо editActivity (после того как будет сделано удаление через actionMode)

    public ArrayList<Map<String, Object>> data;

    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_LL = "ll";
    private static final String ATTRIBUTE_NAME_LL_GRID = "ll_grid";
    private static final String ATTRIBUTE_NAME_GRID_IMAGE = "image";

    private static final String STARTDATE_KEY = "startDate";
    private static final String ENDDATE_KEY = "endDate";

    private static final int iconsType= Icons.IconTypes.moodIconsType.getId();

    private int selectedMoodId;
    private long startDateValue;
    private long endDateValue;

    private static int displayMode;
    private static int numColumns;

    private static int[] positionRowIdMapping;

    private GridView gridViewMoodFragment;
    //ToDO REFACT сделать non-static (и ретест - приложение при этом падает)
    private static GridView lvSimple;
    private static FragmentActivity myContext;

    private static IMoodFragmentInteractionListener moodFragmentListener;

    private Icons icons;

    public FragmentFillDataMood() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        Activity activity = getActivity();
        if (activity != null) {
            myContext = (FragmentActivity) activity;

        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filldata_mood, container, false);

        displayMode = v.getResources().getConfiguration().orientation;

        //lvSimple - list of saved mood rows (top of the screen)
        lvSimple = (GridView) v.findViewById(R.id.FragmentFilldataMood_listView);
        gridViewMoodFragment = (GridView) v.findViewById(R.id.FragmentFilldataMood_gridView);

        getBundleDataFromActivity();

        initializeList();

        setupGridView();

        try {
            moodFragmentListener = (IMoodFragmentInteractionListener) activity;
            Log.d("moodFragmentListener interface is ok");
        } catch (ClassCastException e) {
            if (activity != null) {
                throw new ClassCastException(activity.toString()
                        + " shall implement moodFragmentListener interface");
            }
        }

        Log.d( "Fragment building is finished, startDate = "
                + SmallFunctions.formatDate(startDateValue) + ", startTime = " + SmallFunctions.formatTime(startDateValue)
                + ", endDate = " + SmallFunctions.formatDate(endDateValue) +
                ", endTime = " + SmallFunctions.formatTime(endDateValue));

        return v;

    }


    //Update list after editing via EditMood activity
    public void onResume() {
        super.onResume();
    }

    private void setupGridView() {
        String[] from = {ATTRIBUTE_NAME_GRID_IMAGE, ATTRIBUTE_NAME_LL_GRID};
        int[] to = {R.id.ItemUniversalGrid_iconImage, R.id.ItemUniversalGrid_layout};

        icons = new Icons();

        //For GridView - list of "available" moods
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                icons.getMoodIconsLenght());
        Map<String, Object> m;
        for (int i = 0; i < icons.getMoodIconsLenght(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_GRID_IMAGE, i);
            m.put(ATTRIBUTE_NAME_LL_GRID, i);
            data.add(m);
        }

        // sAdapterGrid - adapter for GridView
        MySimpleAdapterGrid sAdapterGrid = new MySimpleAdapterGrid(myContext, data,
                R.layout.item_universal_grid, from, to, iconsType);

        gridViewMoodFragment.setAdapter(sAdapterGrid);
        sAdapterGrid.setViewBinder(new LayoutGridColorViewBinder());
        adjustGridView();

        // Select list item listener (for GridView)
        gridViewMoodFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMoodId=position;
                Log.d("User has selected gridItem with moodId = " + selectedMoodId);
                // Send event to Activity
                moodFragmentListener.setTimeEvent(selectedMoodId);
                //updateList(startDateValue, endDateValue);

            }
        });
    }

    private void getBundleDataFromActivity() {
        // Get data from activity
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            startDateValue = bundle.getLong(STARTDATE_KEY, 0);
            endDateValue = bundle.getLong(ENDDATE_KEY, 0);
        }
    }



    //Update listView
    private void initializeList() {

        Log.d("Initialize list for startDate = " + SmallFunctions.formatDate(startDateValue)+
        ", startTime = " + SmallFunctions.formatTime(startDateValue) +
        ", endDate = " + SmallFunctions.formatDate(endDateValue) +
        ", endTime = " + SmallFunctions.formatTime(endDateValue));

        data = DBOperations.getEventListForTheDay(myContext, startDateValue, endDateValue, Event.EventTypes.moodEventTypeId.getId());

        positionRowIdMapping = DBOperations.getPositionRowIdMapping(myContext, startDateValue, endDateValue, Event.EventTypes.moodEventTypeId.getId());

        // Create adapter
        String[] from = new String[] {ATTRIBUTE_NAME_START_DATE, ATTRIBUTE_NAME_LL};
        int[] to = new int[]{R.id.ItemMoodEvent_timeText, R.id.ItemMoodEvent_layout};
        SimpleAdapter sAdapterList = new SimpleAdapter(myContext, data, R.layout.item_mood_event,
                from, to);

        sAdapterList.setViewBinder(new LayoutColorViewBinder());

        // Set adapter to list
        lvSimple.setAdapter(sAdapterList);
        adjustGridListView();

        //Select list item listener (for ListView)
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Log.d("User has selected listItem: position = " + position + ", id = " + id + ", rowId = " + positionRowIdMapping[position]);
                                                moodFragmentListener.editMoodRowEvent(positionRowIdMapping[position]);
                                            }
                                        }
        );

    }

    //ToDO REFACT переписать через sAdapterList.notifyDataSetChanged (и для action тоже) -
    // НО: с переходом на cursor adapter нужно будет обновить только курсор, без notifyDataSetChanged
    
    public void updateList(long startDateValue, long endDateValue) {

        //getBundleDataFromActivity();
        this.startDateValue = startDateValue;
        this.endDateValue = endDateValue;
        initializeList();

/*        Log.d("Update list for startDate = " + SmallFunctions.formatDate(selectedDayStartDate)+
                ", startTime = " + SmallFunctions.formatTime(selectedDayStartDate) +
                ", endDate = " + SmallFunctions.formatDate(selectedDayEndDate) +
                ", endTime = " + SmallFunctions.formatTime(selectedDayEndDate));

        data = DBOperations.getEventListForTheDay(myContext, selectedDayStartDate, selectedDayEndDate, Event.EventTypes.moodEventTypeId.getId());

        positionRowIdMapping = DBOperations.getPositionRowIdMapping(myContext, selectedDayStartDate, selectedDayEndDate, Event.EventTypes.moodEventTypeId.getId());

        sAdapterList = (SimpleAdapter) lvSimple.getAdapter();
        sAdapterList.notifyDataSetChanged();*/

    }

    // Adjust view for ListView
    private static void adjustGridListView() {

        if (displayMode == Configuration.ORIENTATION_PORTRAIT) {
            numColumns = 2;
        } else {
            numColumns = 3;
        }
        lvSimple.setNumColumns(numColumns);
        lvSimple.setVerticalSpacing(5);
        lvSimple.setHorizontalSpacing(5);
        lvSimple.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

    }

    //Adjust view for GridView
    private void adjustGridView() {

        if (displayMode == Configuration.ORIENTATION_PORTRAIT) {
            numColumns = (int) Math.ceil(Math.sqrt(icons.getMoodIconsLenght()));
        } else {
            numColumns = 6;
        }

        gridViewMoodFragment.setNumColumns(numColumns);
        gridViewMoodFragment.setVerticalSpacing(5);
        gridViewMoodFragment.setHorizontalSpacing(5);
        gridViewMoodFragment.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

    // Colors for GridView
    private class LayoutColorViewBinder implements SimpleAdapter.ViewBinder {

        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int i = 0;
            Colors colors = new Colors(view);
            switch (view.getId()) {
                case R.id.ItemMoodEvent_layout:
                    i = (int) data;
                    view.setBackgroundColor(colors.getMoodColorForListId(i));
                    return true;
            }
            return false;
        }
    }

    //Colors for ListView
    private class LayoutGridColorViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int i;
            Colors colors = new Colors(view);
            switch (view.getId()) {
                case R.id.ItemUniversalGrid_layout:
                    i = (int) data;
                    view.setBackgroundColor(colors.getMoodColorForGridId(i));
                    return true;
            }
            return false;
        }
    }

}


