package com.macgavrina.moodmenology.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.interfaces.IAddActionFragmentListener;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.model.Icons;

public class FragmentActionEvent extends Fragment implements View.OnClickListener{

    private static final String ACTION_GROUP_ID_KEY="actionGroupId";
    private static final String ACTION_ID_KEY="actionId";
    private static final String FORMATTED_STARTTIME_KEY="formattedStartTime";
    private static final String FORMATTED_ENDTIME_KEY="formattedEndTime";
    private static final String FORMATTED_DURATION_KEY="formattedDuration";

    private int actionGroupId;
    private int actionId;

    private String formattedStartTime;
    private String formattedEndTime;
    private String formattedDuration;

    private IAddActionFragmentListener addActionFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_action_event, container, false);

        getDataFromActivity();

        Icons icons = new Icons();
        ImageView actionImageView = (ImageView) v.findViewById(R.id.FragmentAddActionEvent_actionImage);
        actionImageView.setImageResource(icons.getActionIconsId(actionGroupId, actionId));

        TextView startDateTextView = (TextView) v.findViewById(R.id.FragmentAddActionEvent_startTimeText);
        startDateTextView.setText(formattedStartTime);

        TextView endDateTextView = (TextView) v.findViewById(R.id.FragmentAddActionEvent_endTimeText);
        endDateTextView.setText(formattedEndTime);

        TextView durationTextView = (TextView) v.findViewById(R.id.FragmentAddActionEvent_durationText);
        durationTextView.setText(formattedDuration);

        ImageButton editStartTimeButton = (ImageButton) v.findViewById(R.id.FragmentAddActionEvent_editStartTimeImageButton);
        editStartTimeButton.setOnClickListener(this);

        ImageButton editEndTimeButton = (ImageButton) v.findViewById(R.id.FragmentAddActionEvent_editEndTimeImageButton);
        editEndTimeButton.setOnClickListener(this);

        try {
            addActionFragmentListener = (IAddActionFragmentListener) getActivity();
            Log.d("addActionFragmentListener interface is checked, ok");
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " shall implement moodFragmentListener interface");
        }

        Log.d("Fragment building is finished, startTime = " + formattedStartTime +
            ", endTime = " + formattedEndTime);

        return v;
    }

    private void getDataFromActivity() {
        // Get data from activity
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            actionGroupId = bundle.getInt(ACTION_GROUP_ID_KEY, 0);
            actionId = bundle.getInt(ACTION_ID_KEY, 0);
            formattedStartTime = bundle.getString(FORMATTED_STARTTIME_KEY, "");
            formattedEndTime = bundle.getString(FORMATTED_ENDTIME_KEY, "");
            formattedDuration = bundle.getString(FORMATTED_DURATION_KEY, "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.FragmentAddActionEvent_editStartTimeImageButton):
                Log.d("User has pressed editStartTime button");
                addActionFragmentListener.editStartDate();
                break;
            case (R.id.FragmentAddActionEvent_editEndTimeImageButton):
                Log.d("User has pressed editEndTime button");
                addActionFragmentListener.editEndDate();
                break;
            default:
                break;
        }
    }
}
