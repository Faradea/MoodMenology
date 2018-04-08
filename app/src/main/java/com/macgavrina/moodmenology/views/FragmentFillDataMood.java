package com.macgavrina.moodmenology.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.model.Colors;
import com.macgavrina.moodmenology.model.Event;
import com.macgavrina.moodmenology.model.Icons;
import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.model.MoodEvent;
import com.macgavrina.moodmenology.viewadapters.MySimpleAdapterGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by Irina on 28.12.2017.
 */

public class FragmentFillDataMood extends Fragment {

    private GridView gridViewMoodFragment;

    private static final String LOG_TAG = "MoodMenology";

    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_LL = "ll";
    private static final String ATTRIBUTE_NAME_LL_GRID = "ll_grid";
    private static final String ATTRIBUTE_NAME_GRID_IMAGE = "image";

    private static final String STARTDATE_KEY = "startDate";
    private static final String ENDDATE_KEY = "endDate";

    private static final int iconsType= Icons.IconTypes.moodIconsType.getId();

    private Integer selectedMoodId;
    private static String selectedDayStartDateStringFillData;
    private static String selectedDayEndDateStringFillData;
    private String startDateValue;
    private String endDateValue;

    //ToDO REFACT в activity DBHelper non-static, во фрагменте - static. Понять как правильно
    private static DBHelper dbHelper;
    private static Integer[] positionRowIdMapping;

    private static GridView lvSimple;
    private static FragmentActivity myContext;

    private static IMoodFragmentInteractionListener moodFragmentListener;

    private Icons icons;

    public FragmentFillDataMood() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filldata_mood, container, false);

        //lvSimple - list of saved mood rows (top of the screen)
        lvSimple = (GridView) v.findViewById(R.id.FragmentFilldataMood_listView);
        gridViewMoodFragment = (GridView) v.findViewById(R.id.FragmentFilldataMood_gridView);

        getBundleDataFromActivity();

        setupGridView();

        Log.d(LOG_TAG, "FillDataMoodFragment.onCreateView: FillDataMood fragment building is finished, startDate="+startDateValue+", endDate="+endDateValue);

        return v;

    }


    //Update list after editing via EditMood activity
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "FillDataMoodFragment.onResume: call update list"+startDateValue);
        updateListMethod(startDateValue, endDateValue);
    }

    // Check interface fragment <-> activity
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            moodFragmentListener = (IMoodFragmentInteractionListener) activity;
            Log.d(LOG_TAG, "FillDataMoodFragment.onAttach: moodFragmentListener interface is ok");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " shall implement moodFragmentListener interface");
        }
        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;

        }
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
                R.layout.item_universal_grid, from, to, iconsType, null);

        gridViewMoodFragment.setAdapter(sAdapterGrid);
        sAdapterGrid.setViewBinder(new LayoutGridColorViewBinder());
        adjustGridView();

        // Select list item listener (for GridView)
        gridViewMoodFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMoodId=position;
                Log.d(LOG_TAG, "FillDataMoodFragment.onItemClick: grid item is selected, moodId = " + selectedMoodId);
                // Send event to Activity
                moodFragmentListener.setTimeEvent(selectedMoodId);

            }
        });
    }

    private void getBundleDataFromActivity() {
        // Get data from activity
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            startDateValue = bundle.getString(STARTDATE_KEY, "");
            endDateValue = bundle.getString(ENDDATE_KEY, "");
        }
    }



    //Update listView
    public void updateListMethod(final String selectedDayStartDateStringMood, final String selectedDayEndDateStringMood) {
        selectedDayStartDateStringFillData = selectedDayStartDateStringMood;
        selectedDayEndDateStringFillData = selectedDayEndDateStringMood;

        dbHelper = new DBHelper(myContext);
        ArrayList<Map<String, Object>> data = DBOperations.getEventListForTheDay(dbHelper, selectedDayStartDateStringFillData, selectedDayEndDateStringFillData, Event.EventTypes.moodEventTypeId.getId());

        positionRowIdMapping = DBOperations.getPositionRowIdMapping(dbHelper, selectedDayStartDateStringFillData, selectedDayEndDateStringFillData, Event.EventTypes.moodEventTypeId.getId());

        // Create adapter
        String[] from = {ATTRIBUTE_NAME_START_DATE, ATTRIBUTE_NAME_LL};
        int[] to = {R.id.ItemMoodEvent_timeText, R.id.ItemMoodEvent_layout};
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
                                                Log.d(LOG_TAG, "FillDataMoodFragment.updateList.setOnItemClickListener: selected list item: position = " + position + ", id = " + id + ", rowId = " + positionRowIdMapping[position]);
                                                moodFragmentListener.editMoodRowEvent(positionRowIdMapping[position]);
                                            }
                                        }
        );

    }

    // Adjust view for ListView
    private static void adjustGridListView() {
        lvSimple.setNumColumns(2);
        lvSimple.setVerticalSpacing(5);
        lvSimple.setHorizontalSpacing(5);
        lvSimple.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

    }

    //Adjust view for GridView
    private void adjustGridView() {
        int numColumns = (int) Math.ceil(Math.sqrt(icons.getMoodIconsLenght()));
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
                    i = ((Integer) data).intValue();
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
            int i = 0;
            Colors colors = new Colors(view);
            switch (view.getId()) {
                case R.id.ItemUniversalGrid_layout:
                    i = (Integer) data;
                    view.setBackgroundColor(colors.getMoodColorForGridId(i));
                    return true;
            }
            return false;
        }
    }

}


