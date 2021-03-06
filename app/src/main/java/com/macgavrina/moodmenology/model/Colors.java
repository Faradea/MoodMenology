package com.macgavrina.moodmenology.model;

import android.view.View;

import com.macgavrina.moodmenology.R;

public class Colors {

    private int[] moodColorsForList;
    private int[] moodColorsForGrid;
    private int actionColor;
    private int actionListColor;

    public Colors(final View view) {

        moodColorsForList = new int[] {
                view.getResources().getColor(R.color.colorMood1),
                view.getResources().getColor(R.color.colorMood2),
                view.getResources().getColor(R.color.colorMood3),
                view.getResources().getColor(R.color.colorMood4),
                view.getResources().getColor(R.color.colorMood5),
                view.getResources().getColor(R.color.colorMood6)
        };

        moodColorsForGrid = new int[] {
                view.getResources().getColor(R.color.colorMood1back),
                view.getResources().getColor(R.color.colorMood2back),
                view.getResources().getColor(R.color.colorMood3back),
                view.getResources().getColor(R.color.colorMood4back),
                view.getResources().getColor(R.color.colorMood5back),
                view.getResources().getColor(R.color.colorMood6back)
        };

        actionColor = view.getResources().getColor(R.color.colorAction);

        actionListColor = view.getResources().getColor(R.color.colorActionListItem);
    }

    public int getMoodColorForListId(final int colorSequenceNumber) {

        return moodColorsForList[colorSequenceNumber];

    }

    public int getMoodColorForGridId(final int colorSequenceNumber) {

        return moodColorsForGrid[colorSequenceNumber];

    }

    public int getActionColorForGridId() {

        return actionColor;

    }

    public int getActionColorForListId() {
        return actionListColor;
    }
}
