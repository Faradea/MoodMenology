package com.macgavrina.moodmenology.model;

import com.macgavrina.moodmenology.R;

public class Icons {

        private int[][] actionIconsIds;
        private int[] moodIconIds;
        private int[] actionGroupIconIds;
        private int[] moodForEditIconIds;

        public Icons(){

            actionIconsIds = new int[][]{
                    {R.mipmap.group1action1, R.mipmap.group1action2, R.mipmap.group1action3, R.mipmap.group1action4, R.mipmap.group1action5, R.mipmap.group1action6, R.mipmap.group1action7, R.mipmap.group1action8, R.mipmap.group1action9, R.mipmap.group1action10, R.mipmap.group1action11, R.mipmap.group1action12},
                    {R.mipmap.group2action1, R.mipmap.group2action2, R.mipmap.group2action3, R.mipmap.group2action4, R.mipmap.group2action5, R.mipmap.group2action6, R.mipmap.group2action7, R.mipmap.group2action8, R.mipmap.group2action9, R.mipmap.group2action10, R.mipmap.group2action11, R.mipmap.group2action12},
                    {R.mipmap.group3action1, R.mipmap.group3action2, R.mipmap.group3action3},
                    {R.mipmap.group4action1, R.mipmap.group4action2, R.mipmap.group4action3, R.mipmap.group4action4, R.mipmap.group4action5, R.mipmap.group4action6, R.mipmap.group4action7, R.mipmap.group4action8, R.mipmap.group4action9},
                    {R.mipmap.group5action1, R.mipmap.group5action2, R.mipmap.group5action3, R.mipmap.group5action4, R.mipmap.group5action5, R.mipmap.group5action6, R.mipmap.group5action7, R.mipmap.group5action8, R.mipmap.group5action9, R.mipmap.group5action10, R.mipmap.group5action11, R.mipmap.group5action12},
                    {R.mipmap.group6action1, R.mipmap.group6action2, R.mipmap.group6action3, R.mipmap.group6action4, R.mipmap.group6action5, R.mipmap.group6action6, R.mipmap.group6action7, R.mipmap.group6action8, R.mipmap.group6action9, R.mipmap.group6action10, R.mipmap.group6action11, R.mipmap.group6action12}
            };

            moodIconIds = new int[] {R.mipmap.mood1back, R.mipmap.mood2back, R.mipmap.mood3back, R.mipmap.mood4back, R.mipmap.mood5back, R.mipmap.mood6back};

            actionGroupIconIds = new int[] {R.mipmap.actionsgroup1, R.mipmap.actionsgroup2, R.mipmap.actionsgroup3, R.mipmap.actionsgroup4, R.mipmap.actionsgroup5, R.mipmap.actionsgroup6};

            moodForEditIconIds = new int[] {R.mipmap.mood1, R.mipmap.mood2, R.mipmap.mood3, R.mipmap.mood4, R.mipmap.mood5, R.mipmap.mood6};
        }

        public int getMoodIconId(final int moodId){
            return moodIconIds[moodId];
        }

        public int getMoodIconsLenght() {
        return moodIconIds.length;
    }

        public int getMoodForEditIconId(final int moodId){
        return moodForEditIconIds[moodId];
    }


        public int getActionGroupIconId (final int actionGroupId){
            return actionGroupIconIds[actionGroupId];
        }

        public int getActionGroupIconsLenght() {
            return actionGroupIconIds.length;
        }

        public int getActionIconsId(final int actionsGroupId, final int actionId){
                return actionIconsIds[actionsGroupId][actionId];
        }

        public int getActionIconsLenght(int actionsGroupId) {
            return actionIconsIds[actionsGroupId].length;
        }


    public enum IconTypes {

        moodIconsType(0), actionGroupIconsType(1), actionIconsType(2);

        private final int typeId;

        IconTypes(int i) {
            typeId=i;
        }

        public int getId() {
            return typeId;
        }
    }
}
