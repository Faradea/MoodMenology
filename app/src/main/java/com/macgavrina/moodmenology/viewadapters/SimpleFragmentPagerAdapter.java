package com.macgavrina.moodmenology.viewadapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.views.FragmentFillDataActions;
import com.macgavrina.moodmenology.views.FragmentFillDataMood;

/**
 * Created by Irina on 28.12.2017.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    final private String START_DATE_KEY = "startDate";
    final private String END_DATE_KEY = "endDate";

    final private String MOOD_FRAGMENT_PAGE_TITLE = "Mood";
    final private String ACTION_FRAGMENT_PAGE_TITLE = "Actions";

    final private Integer NUMBER_OF_FRAGMENTS = 2;
    final private int MOOD_FRAGMENT_ID=0;
    final private int ACTION_FRAGMENT_ID=1;

    private String startDateValue;
    private String endDateValue;

    public SimpleFragmentPagerAdapter(final FragmentManager fm, final String selectedDayStartDateString, final String selectedDayEndDateString) {
        super(fm);
        startDateValue = selectedDayStartDateString;
        endDateValue = selectedDayEndDateString;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(final int position) {

        Bundle bundle = new Bundle();

        switch (position){
            case MOOD_FRAGMENT_ID:
                FragmentFillDataMood fillDataMoodFragment = new FragmentFillDataMood();
                bundle.putString(START_DATE_KEY, startDateValue);
                bundle.putString(END_DATE_KEY, endDateValue);
                fillDataMoodFragment.setArguments(bundle);
                return fillDataMoodFragment;
            case ACTION_FRAGMENT_ID:
                FragmentFillDataActions fillDataActionsFragment = new FragmentFillDataActions();
                bundle.putString(START_DATE_KEY, startDateValue);
                bundle.putString(END_DATE_KEY, endDateValue);
                fillDataActionsFragment.setArguments(bundle);
                return fillDataActionsFragment;
            default:
                fillDataActionsFragment = new FragmentFillDataActions();
                bundle.putString(START_DATE_KEY, startDateValue);
                bundle.putString(END_DATE_KEY, endDateValue);
                fillDataActionsFragment.setArguments(bundle);
                return fillDataActionsFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case MOOD_FRAGMENT_ID:
                return MOOD_FRAGMENT_PAGE_TITLE;
            case ACTION_FRAGMENT_ID:
                return ACTION_FRAGMENT_PAGE_TITLE;
            default:
                return null;
        }
    }

}
