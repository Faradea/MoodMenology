package com.macgavrina.moodmenology.views;

import android.app.Activity;
import android.os.Bundle;
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

/**
 * Created by Irina on 28.12.2017.
 */

public class FragmentFillDataActions extends Fragment {

    private static final String ATTRIBUTE_NAME_GRID_IMAGE = "image";
    private static final String ATTRIBUTE_NAME_LL_GRID = "ll_grid";
    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_END_DATE = "endDate";
    private static final String ATTRIBUTE_NAME_DURATION = "duration";
    private static final String ATTRIBUTE_NAME_LL = "ll";

    private static final String STARTDATE_KEY = "startDate";
    private static final String ENDDATE_KEY = "endDate";

    private static final int iconsTypeGrid = Icons.IconTypes.actionGroupIconsType.getId();

    private long startDateValue;
    private long endDateValue;
    private Integer selectedActionsGroupId;

    private GridView gridViewActionFragment;

    //ToDo REFACT сделать non-static
    private static FragmentActivity myContext;
    private static GridView lvSimple;
    private static Integer[] positionRowIdMapping;
    private static IActionsFragmentInteractionListener actionsFragmentListener;

    private Icons icons;

    public FragmentFillDataActions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        Activity activity = getActivity();
        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;

        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filldata_actions, container, false);

        gridViewActionFragment = (GridView) v.findViewById(R.id.FragmentFilldataActions_gridView);
        lvSimple = (GridView) v.findViewById(R.id.FragmentFilldataActions_listView);

        getBundleDataFromActivity();

        initializeList();

        setupGridView();

        try {
            actionsFragmentListener = (IActionsFragmentInteractionListener) activity;
            Log.d("actionsFragmentListener interface is ok");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " shall implement actionsFragmentListener interface");
        }

        Log.d( "Fragment building is finished, startDate = "
        + SmallFunctions.formatDate(startDateValue) + ", startTime = " + SmallFunctions.formatTime(startDateValue)
        + ", endDate = " + SmallFunctions.formatDate(endDateValue) +
        ", endTime = " + SmallFunctions.formatTime(endDateValue));

        return v;

    }

    private void getBundleDataFromActivity() {
        // Get data from activity
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            startDateValue = bundle.getLong(STARTDATE_KEY, 0);
            endDateValue = bundle.getLong(ENDDATE_KEY, 0);
        } else {
            Log.d("Bundle is null");
        }
    }

    //Update list after editing via EditMood activity
    public void onResume() {
        super.onResume();
    }

    private void setupGridView() {

        icons = new Icons();

        //For GridView - list of action groups
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                icons.getActionGroupIconsLenght());

        Map<String, Object> m;
        for (int i = 0; i < icons.getActionGroupIconsLenght(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_GRID_IMAGE, i);
            m.put(ATTRIBUTE_NAME_LL_GRID, i);
            data.add(m);
        }

        String[] from = {ATTRIBUTE_NAME_GRID_IMAGE, ATTRIBUTE_NAME_LL_GRID};
        int[] to = {R.id.ItemUniversalGrid_iconImage, R.id.ItemUniversalGrid_layout};

        // sAdapterGrid - adapter for GridView
        MySimpleAdapterGrid sAdapterGrid = new MySimpleAdapterGrid(myContext, data,
                R.layout.item_universal_grid, from, to, iconsTypeGrid, null);

        gridViewActionFragment.setAdapter(sAdapterGrid);


        sAdapterGrid.setViewBinder(new LayoutGridColorViewBinder());
        adjustGridView();

        // Select list item listener (for GridView)
        gridViewActionFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedActionsGroupId = position;
                Log.d("User has selected grid item with actionsGroupId = " + selectedActionsGroupId);
                // Send event to Activity
                actionsFragmentListener.selectActionsGroupEvent(selectedActionsGroupId);
            }
        });
    }

    //Update listView
    private SimpleAdapter initializeList() {

        ArrayList<Map<String, Object>> data = DBOperations.getEventListForTheDay(myContext, startDateValue,
                endDateValue, Event.EventTypes.actionEventTypeId.getId());

        positionRowIdMapping = DBOperations.getPositionRowIdMapping(myContext, startDateValue,
                endDateValue, Event.EventTypes.actionEventTypeId.getId());

        // Create adapter
        String[] from = {ATTRIBUTE_NAME_START_DATE, ATTRIBUTE_NAME_END_DATE, ATTRIBUTE_NAME_DURATION, ATTRIBUTE_NAME_LL};
        int[] to = {R.id.ItemActionEvent_startTimeText, R.id.ItemActionEvent_endTimeText, R.id.ItemActionEvent_durationText, R.id.ItemActionEvent_iconImage};

        SimpleAdapter sAdapterList = new SimpleAdapter(myContext, data, R.layout.item_action_event,
                from, to);

        lvSimple.setAdapter(sAdapterList);
        adjustGridListView();

        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Log.d("User has selected list item: position = " + position + ", id = " + id + ", rowId = " + positionRowIdMapping[position]);
                                                actionsFragmentListener.editActionRowEvent(positionRowIdMapping[position]);
                                            }
                                        }
        );

        return sAdapterList;
    }

    public void updateList() {

        getBundleDataFromActivity();
        initializeList();

    }

    // Adjust view for ListView
    private static void adjustGridListView() {
        lvSimple.setNumColumns(1);
        lvSimple.setVerticalSpacing(5);
        lvSimple.setHorizontalSpacing(5);
        lvSimple.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

    }

    //Adjust view for GridView
    private void adjustGridView() {
        int numColumns = (int) Math.ceil(Math.sqrt(icons.getActionGroupIconsLenght()));
        gridViewActionFragment.setNumColumns(numColumns);
        gridViewActionFragment.setVerticalSpacing(5);
        gridViewActionFragment.setHorizontalSpacing(5);
        gridViewActionFragment.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
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