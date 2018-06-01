package com.macgavrina.moodmenology.interfaces;

public interface IMoodFragmentInteractionListener {

        void setTimeEvent(int selectedMoodId);

        void editMoodRowEvent(int rowId);

        void deleteMoodRowEvent(int rowId);
}
