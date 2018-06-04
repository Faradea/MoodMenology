package com.macgavrina.moodmenology.views;

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
import com.macgavrina.moodmenology.interfaces.IMoodFragmentInteractionListener;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.model.Colors;
import com.macgavrina.moodmenology.model.Event;
import com.macgavrina.moodmenology.model.Icons;
import com.macgavrina.moodmenology.model.MoodEvent;
import com.macgavrina.moodmenology.viewadapters.MySimpleAdapterGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentFillDataMood extends Fragment implements AbsListView.MultiChoiceModeListener {

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

    private int displayMode;
    private int numColumns;

    private int[] positionRowIdMapping;

    private GridView gridViewMoodFragment;
    private GridView lvSimple;

    private ActionMode actionMode;

    private IMoodFragmentInteractionListener moodFragmentListener;

    private Icons icons;
    private Colors colors;

    public FragmentFillDataMood() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

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
            moodFragmentListener = (IMoodFragmentInteractionListener) getActivity();
            Log.d("moodFragmentListener interface is ok");
        } catch (ClassCastException e) {
            if (getActivity() != null) {
                throw new ClassCastException(getActivity().toString()
                        + " shall implement moodFragmentListener interface");
            }
        }

        colors = new Colors(v);

        Log.d( "Fragment building is finished, startDate = "
                + SmallFunctions.formatDate(startDateValue) + ", startTime = " + SmallFunctions.formatTime(startDateValue)
                + ", endDate = " + SmallFunctions.formatDate(endDateValue) +
                ", endTime = " + SmallFunctions.formatTime(endDateValue));

        return v;

    }

    private void setupGridView() {
        String[] from = {ATTRIBUTE_NAME_GRID_IMAGE, ATTRIBUTE_NAME_LL_GRID};
        int[] to = {R.id.ItemUniversalGrid_iconImage, R.id.ItemUniversalGrid_layout};

        icons = new Icons();

        //For GridView - list of "available" moods
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                icons.getMoodIconsLength());
        Map<String, Object> m;
        for (int i = 0; i < icons.getMoodIconsLength(); i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_GRID_IMAGE, i);
            m.put(ATTRIBUTE_NAME_LL_GRID, i);
            data.add(m);
        }

        // sAdapterGrid - adapter for GridView
        MySimpleAdapterGrid sAdapterGrid = new MySimpleAdapterGrid(getActivity(), data,
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
                if (actionMode != null) {
                    actionMode.finish();
                }
                moodFragmentListener.setTimeEvent(selectedMoodId);
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

        ArrayList<Map<String, Object>> data = DBOperations.getEventListForTheDay(getActivity(), startDateValue, endDateValue, Event.EventTypes.moodEventTypeId.getId());

        positionRowIdMapping = DBOperations.getPositionRowIdMapping(getActivity(), startDateValue, endDateValue, Event.EventTypes.moodEventTypeId.getId());

        // Create adapter
        String[] from = new String[] {ATTRIBUTE_NAME_START_DATE, ATTRIBUTE_NAME_LL};
        int[] to = new int[]{R.id.ItemMoodEvent_timeText, R.id.ItemMoodEvent_layout};
        SimpleAdapter sAdapterList = new SimpleAdapter(getActivity(), data, R.layout.item_mood_event,
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

        lvSimple.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        lvSimple.setMultiChoiceModeListener(this);

    }


    
    public void updateList(long startDateValue, long endDateValue) {

        this.startDateValue = startDateValue;
        this.endDateValue = endDateValue;
        initializeList();

        //ToDO REFACT переписать через sAdapterList.notifyDataSetChanged (и для action тоже)
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
    private void adjustGridListView() {

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
            numColumns = (int) Math.ceil(Math.sqrt(icons.getMoodIconsLength()));
        } else {
            numColumns = 6;
        }

        gridViewMoodFragment.setNumColumns(numColumns);
        gridViewMoodFragment.setVerticalSpacing(5);
        gridViewMoodFragment.setHorizontalSpacing(5);
        gridViewMoodFragment.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {

        Log.d("Item check state is changed, position = " + position + ", checked = "
                + checked);

        if (checked) {
            lvSimple.getChildAt(position).setBackgroundResource(R.drawable.border);
        }
        else {
            Colors colors = new Colors(lvSimple);
            MoodEvent moodEvent = new MoodEvent(getActivity(), positionRowIdMapping[position], false);
            lvSimple.getChildAt(position).setBackgroundColor(colors.getMoodColorForListId(moodEvent.getEventId()));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.actionMode = mode;
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
                        moodFragmentListener.deleteMoodRowEvent(positionRowIdMapping[selectedPositionIds.keyAt(i)]);
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
        this.actionMode = null;
        Log.d("action mode is destroyed");
    }

    // Colors for ListView
    private class LayoutColorViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if (actionMode == null) {
                int i;
                switch (view.getId()) {
                    case R.id.ItemMoodEvent_layout:
                        i = (int) data;
                        view.setBackgroundColor(colors.getMoodColorForListId(i));
                        return true;
                }
                return false;
            } else
                return true;
        }
    }

    //Colors for GridView
    private class LayoutGridColorViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int i;
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


