package com.macgavrina.moodmenology.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.controllers.DBHelper;
import com.macgavrina.moodmenology.controllers.DBOperations;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "MoodMenology";

    private DBHelper dbHelper;

    private Button deleteAllDataButtonSettings;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        deleteAllDataButtonSettings = (Button) findViewById(R.id.ActivitySettings_deleteAllDataButton);
        deleteAllDataButtonSettings.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case (R.id.ActivitySettings_deleteAllDataButton):

                dbHelper = new DBHelper(this);
                DBOperations.rmrf(dbHelper);

                Toast.makeText(this, "All data have been deleted", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
