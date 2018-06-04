package com.macgavrina.moodmenology.views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.SmallFunctions;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.interfaces.IActionsFragmentInteractionListener;
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

public class FragmentFillDataActions extends Fragment implements AbsListView.MultiChoiceModeListener
        //implements View.OnTouchListener
        {
            //ToDo NEW сделать startDate и endDate как в lifelog

    private static final String ATTRIBUTE_NAME_GRID_IMAGE = "image";
    private static final String ATTRIBUTE_NAME_LL_GRID = "ll_grid";
    private static final String ATTRIBUTE_NAME_START_DATE = "startDate";
    private static final String ATTRIBUTE_NAME_END_DATE = "endDate";
    private static final String ATTRIBUTE_NAME_DURATION = "duration";
    private static final String ATTRIBUTE_NAME_IMAGE = "image";
    private static final String ATTRIBUTE_NAME_LL = "ll";

    private static final String STARTDATE_KEY = "startDate";
    private static final String ENDDATE_KEY = "endDate";

    private static final int iconsTypeGrid = Icons.IconTypes.actionGroupIconsType.getId();

    private long startDateValue;
    private long endDateValue;

    private int displayMode;
    private int numColumns;

    private GridView gridViewActionFragment;
    private GridView lvSimple;
    private ActionMode actionMode;
    private int[] positionRowIdMapping;
    private IActionsFragmentInteractionListener actionsFragmentListener;

    private Colors colors;
    private Icons icons;

    public FragmentFillDataActions() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            actionsFragmentListener = (IActionsFragmentInteractionListener) getActivity();
            Log.d("actionsFragmentListener interface is ok");
        } catch (ClassCastException e) {
            if (getActivity() != null) {
                throw new ClassCastException(getActivity().toString()
                        + " shall implement actionsFragmentListener interface");
            }
        }
    }


            @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filldata_actions, container, false);
        colors = new Colors(v);

        displayMode = v.getResources().getConfiguration().orientation;

        lvSimple = (GridView) v.findViewById(R.id.FragmentFilldataActions_listView);
        gridViewActionFragment = (GridView) v.findViewById(R.id.FragmentFilldataActions_gridView);

        getBundleDataFromActivity();

        setupGridView();

        initializeList();

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
            Log.d("dayStart = " + SmallFunctions.formatDate(startDateValue));
        } else {
            Log.d("Bundle is null");
        }
    }

    private void setupGridView() {

        icons = new Icons();

        //For GridView - list of action groups
        ArrayList<Map<String, Object>> data = new ArrayList<>(
                icons.getActionGroupIconsLength());

        Map<String, Object> m;
        for (int i = 0; i < icons.getActionGroupIconsLength(); i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_NAME_GRID_IMAGE, i);
            m.put(ATTRIBUTE_NAME_LL_GRID, i);
            data.add(m);
        }

        String[] from = {ATTRIBUTE_NAME_GRID_IMAGE, ATTRIBUTE_NAME_LL_GRID};
        int[] to = {R.id.ItemUniversalGrid_iconImage, R.id.ItemUniversalGrid_layout};

        // sAdapterGrid - adapter for GridView
        MySimpleAdapterGrid sAdapterGrid = new MySimpleAdapterGrid(getActivity(), data,
                R.layout.item_universal_grid, from, to, iconsTypeGrid);

        gridViewActionFragment.setAdapter(sAdapterGrid);

        sAdapterGrid.setViewBinder(new LayoutGridColorViewBinder());
        adjustGridView();

        // Select list item listener (for GridView)
        gridViewActionFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("User has selected grid item with actionsGroupId = " + position);
                if (actionMode != null) {
                    actionMode.finish();
                }
                // Send event to Activity
                actionsFragmentListener.selectActionsGroupEvent(position);
            }
        });
    }

    //Update listView
    private void initializeList() {

        Log.d("Initialize list for startDate = " + SmallFunctions.formatDate(startDateValue)+
                ", startTime = " + SmallFunctions.formatTime(startDateValue) +
                ", endDate = " + SmallFunctions.formatDate(endDateValue) +
                ", endTime = " + SmallFunctions.formatTime(endDateValue));
        Log.d("lvSimple" + lvSimple);

        ArrayList<Map<String, Object>> data = DBOperations.getEventListForTheDay(getActivity(), startDateValue,
                endDateValue, Event.EventTypes.actionEventTypeId.getId());

        positionRowIdMapping = DBOperations.getPositionRowIdMapping(getActivity(), startDateValue,
                endDateValue, Event.EventTypes.actionEventTypeId.getId());

        // Create adapter
        String[] from = {ATTRIBUTE_NAME_START_DATE, ATTRIBUTE_NAME_END_DATE, ATTRIBUTE_NAME_DURATION, ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_LL};
        int[] to = {R.id.ItemActionEvent_startTimeText, R.id.ItemActionEvent_endTimeText, R.id.ItemActionEvent_durationText, R.id.ItemActionEvent_iconImage, R.id.ItemActionEvent_layout};

        SimpleAdapter sAdapterList = new SimpleAdapter(getActivity(), data, R.layout.item_action_event,
                from, to);

        sAdapterList.setViewBinder(new LayoutColorViewBinder());

        lvSimple.setAdapter(sAdapterList);
        adjustGridListView();

        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                if (actionMode != null) {
                                                    actionMode.finish();
                                                }
                                                Log.d("User has selected list item: position = " + position + ", id = " + id + ", rowId = " + positionRowIdMapping[position]);
                                                actionsFragmentListener.editActionRowEvent(positionRowIdMapping[position]);
                                            }
                                        }
        );


        lvSimple.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        lvSimple.setMultiChoiceModeListener(this);

        //lvSimple.setOnTouchListener(this::onTouch);

    }

    public void updateList(long startDateValue, long endDateValue) {

        this.startDateValue = startDateValue;
        this.endDateValue = endDateValue;

        initializeList();

    }

    // Adjust view for ListView
    private void adjustGridListView() {

        if (displayMode == Configuration.ORIENTATION_PORTRAIT) {
            numColumns = 1;
        } else {
            numColumns = 2;
        }

        lvSimple.setNumColumns(numColumns);
        lvSimple.setVerticalSpacing(5);
        lvSimple.setHorizontalSpacing(5);
        lvSimple.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

    }

    //Adjust view for GridView
    private void adjustGridView() {
        if (displayMode == Configuration.ORIENTATION_PORTRAIT) {
            numColumns = (int) Math.ceil(Math.sqrt(icons.getActionGroupIconsLength()));
        } else {
            numColumns = 6;
        }
        gridViewActionFragment.setNumColumns(numColumns);
        gridViewActionFragment.setVerticalSpacing(5);
        gridViewActionFragment.setHorizontalSpacing(5);
        gridViewActionFragment.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Log.d("Item check state is changed, position = " + position + ", checked = "
                        + checked);
                if (checked) {
                    lvSimple.getChildAt(position).setBackgroundResource(R.drawable.border);
                }
                else {
                    lvSimple.getChildAt(position).setBackgroundResource(0);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                actionMode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_mode_menu_delete:
                        Log.d("Delete button in actionMode is pressed");
                        SparseBooleanArray selectedPositionIds = lvSimple.getCheckedItemPositions();
                        for (int i = 0; i < selectedPositionIds.size(); i++) {
                            Log.d("i = " + i + ", keyAt = " + selectedPositionIds.keyAt(i) + ", value = " + selectedPositionIds.valueAt(i));
                            if (selectedPositionIds.valueAt(i)) {
                                Log.d("Delete action with rowId = " + positionRowIdMapping[selectedPositionIds.keyAt(i)]);
                                actionsFragmentListener.deleteActionRowEvent(positionRowIdMapping[selectedPositionIds.keyAt(i)]);
                            }
                        }
                        initializeList();
                        break;
                }
                mode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                Log.d("action mode is destroyed");
            };


/*    float startX = 0;
    float endX = 0;
    float startY = 0;
    float endY = 0;
    boolean isMovement = false;

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
            //gestureDetector.onTouchEvent(event);
            int SWIPE_MIN_DISTANCE = 120;
            int SWIPE_THRESHOLD_VELOCITY = 200;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    endX = 0;
                    endY = 0;
                    Log.d("action_down");
                    isMovement = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("Action_move");
                    endX = event.getX();
                    endY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("Action_up");
                    int position = -1;
                    position = lvSimple.pointToPosition((int) startX, (int) startY);
                    if (!isMovement & position != -1) {
                        Log.d("User has selected list item: position = " + position + ", rowId = " + positionRowIdMapping[position]);
                        actionsFragmentListener.editActionRowEvent(positionRowIdMapping[position]);
                        startX = 0;
                        startY = 0;
                    }
                    break;
            }

            Log.d("startX = " + startX + ", endX = " + endX);
        if (!isMovement & endX != 0 & startX != 0 & (Math.abs(startX - endX) > SWIPE_MIN_DISTANCE)) {
            Log.d("swipe");
            int position = -1;
            position = lvSimple.pointToPosition((int) startX, (int) startY);
            if (position != -1) {
                Log.d("User swipes list item: position = " + position);// + ", rowId = " + positionRowIdMapping[position]);
                actionsFragmentListener.deleteActionRowEvent(positionRowIdMapping[position]);
            }
            isMovement = true;
        }

            return true;
    }*/


    //Colors for ListView
    public class LayoutGridColorViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(final View view, final Object data,
                                    final String textRepresentation) {

            switch (view.getId()) {
                case R.id.ItemUniversalGrid_layout:
                    view.setBackgroundColor(colors.getActionColorForGridId());
                    return true;
            }
            return false;
        }
    }

    private class LayoutColorViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (actionMode == null) {
                switch (view.getId()) {
                    case R.id.ItemActionEvent_layout: ;
                        view.setBackgroundColor(colors.getActionColorForListId());
                        return true;
                }
                return false;
            } else
                return true;
        }
    }
}