package com.macgavrina.moodmenology.menu;

import android.content.Intent;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.logging.Log;

public class MainMenu{

    public MainMenu() {

    }

    public Intent processOnMenuItemSelected(final int itemId) {

        Intent intent = null;

        switch (itemId){
            case R.id.menu_download:
                Log.d("User selects Download Data menuItem, start processing");
                intent = new Intent("com.macgavrina.moodmenology.downloaddata");
                break;
            case R.id.menu_about:
                Log.d("User selects About menuItem, start processing");
                intent = new Intent("com.macgavrina.moodmenology.about");
                break;
            default:
                break;
        }

        return intent;
    }
}
