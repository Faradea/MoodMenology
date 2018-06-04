package com.macgavrina.moodmenology.menu;

import android.content.Intent;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.logging.Log;

public class MainMenu{

    private int menuItemId;
    private Intent intent;

    public MainMenu(final int itemId) {

        this.menuItemId = itemId;

    }

    public Intent processOnMenuItemSelected() {

        switch (menuItemId){
            case R.id.menu_settings:
                Log.d("User selects Settings menuItem, start processing");
                intent = new Intent("com.macgavrina.moodmenology.settings");
                break;
            default:
                break;
        }

        return intent;
    }
}
