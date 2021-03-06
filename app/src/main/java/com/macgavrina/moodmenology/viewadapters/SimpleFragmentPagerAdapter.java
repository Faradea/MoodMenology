package com.macgavrina.moodmenology.viewadapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.macgavrina.moodmenology.views.FragmentFillDataActions;
import com.macgavrina.moodmenology.views.FragmentFillDataMood;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String START_DATE_KEY = "startDate";
    private static final String END_DATE_KEY = "endDate";
    private static final String MOOD_FRAGMENT_PAGE_TITLE = "Mood";
    private static final String ACTION_FRAGMENT_PAGE_TITLE = "Actions";

    private static final int NUMBER_OF_FRAGMENTS = 2;
    private static final int MOOD_FRAGMENT_ID=0;
    private static final int ACTION_FRAGMENT_ID=1;

    private FragmentFillDataActions actionCurrentFragment;
    private FragmentFillDataMood moodCurrentFragment;

    private long startDateValue;
    private long endDateValue;

    public SimpleFragmentPagerAdapter(final FragmentManager fm, final long selectedDayStartDate, final long selectedDayEndDate) {
        super(fm);
        this.startDateValue = selectedDayStartDate;
        this.endDateValue = selectedDayEndDate;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(final int position) {

        Bundle bundle = new Bundle();

        switch (position){
            case MOOD_FRAGMENT_ID:
                FragmentFillDataMood fillDataMoodFragment = new FragmentFillDataMood();
                bundle.putLong(START_DATE_KEY, startDateValue);
                bundle.putLong(END_DATE_KEY, endDateValue);
                fillDataMoodFragment.setArguments(bundle);
                moodCurrentFragment = fillDataMoodFragment;
                return fillDataMoodFragment;
            case ACTION_FRAGMENT_ID:
                FragmentFillDataActions fillDataActionsFragment = new FragmentFillDataActions();
                bundle.putLong(START_DATE_KEY, startDateValue);
                bundle.putLong(END_DATE_KEY, endDateValue);
                fillDataActionsFragment.setArguments(bundle);
                actionCurrentFragment = fillDataActionsFragment;
                return fillDataActionsFragment;
            default:
                return null;
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

    public FragmentFillDataActions getCurrentActionFragment() {
        return actionCurrentFragment;
    }

    public FragmentFillDataMood getCurrentMoodFragment() {
        return moodCurrentFragment;
    }

}
