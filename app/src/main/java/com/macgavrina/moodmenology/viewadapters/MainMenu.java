package com.macgavrina.moodmenology.viewadapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;

public class MainMenu extends AppCompatActivity {

    private int menuItemId;
    private Intent intent;
    private Context context;

    public MainMenu(final int itemId, Context context) {

        this.menuItemId = itemId;
        this.context = context;

    }

    public Intent processOnMenyItemSelected(FragmentActivity activity) {

        switch (menuItemId){
            case R.id.menu_settings:
                Log.d("User selects Settings menuItem, start processing");
                intent = new Intent("com.macgavrina.moodmenology.settings");
                break;
            case R.id.menu_send_data:

                Log.d("User selects SendData menuItem, start processing");
                intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("plain/text");

                intent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        "ivgavrina@gmail.com");

                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "Data from MoodMenology");

                intent.putExtra(android.content.Intent.EXTRA_TEXT,
                        DBOperations.getAllDataForEmail(context));

                break;
            default:
                break;
        }

        return intent;
    }
}
