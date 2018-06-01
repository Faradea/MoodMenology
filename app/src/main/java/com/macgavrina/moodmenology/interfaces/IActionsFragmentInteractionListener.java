package com.macgavrina.moodmenology.interfaces;

public interface IActionsFragmentInteractionListener {

        void selectActionsGroupEvent(int selectedActionsGroupId);

        void editActionRowEvent(int rowId);

        void deleteActionRowEvent(int rowId);
}
