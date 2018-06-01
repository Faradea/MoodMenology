package com.macgavrina.moodmenology.menu;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.logging.Log;

public class MainMenu extends AppCompatActivity {

    private int menuItemId;
    private Intent intent;

    public MainMenu(final int itemId, Context context) {

        this.menuItemId = itemId;

    }

    public Intent processOnMenyItemSelected(FragmentActivity activity) {

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
