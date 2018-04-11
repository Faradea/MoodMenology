package com.macgavrina.moodmenology.viewadapters;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.controllers.DBOperations;

public class MainMenu extends AppCompatActivity {

    private static final String LOG_TAG = "MoodMenology";

    private int menuItemId;
    private Intent intent;

    public MainMenu(final int itemId) {

        this.menuItemId = itemId;
    }

    public Intent processOnMenyItemSelected(FragmentActivity activity) {

        switch (menuItemId){
            case R.id.menu_settings:
                Log.d(LOG_TAG, "MainActivity.onOptionsItemSelected: settings menu item is picked");
                intent = new Intent("com.macgavrina.moodmenology.settings");
                break;
            case R.id.menu_send_data:
                intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("plain/text");

                intent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        "ivgavrina@gmail.com");

                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "Data from MoodMenology");

                DBHelper dbHelper = new DBHelper(activity);

                intent.putExtra(android.content.Intent.EXTRA_TEXT,
                        DBOperations.getAllDataForEmail(dbHelper));

                break;
            default:
                break;
        }

        return intent;
    }
}
