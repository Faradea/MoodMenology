package com.macgavrina.moodmenology.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button deleteAllDataButtonSettings = (Button) findViewById(R.id.ActivitySettings_deleteAllDataButton);
        deleteAllDataButtonSettings.setOnClickListener(this);

        Log.d("Activity building is finished");

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case (R.id.ActivitySettings_deleteAllDataButton):

                Log.d("User has pressed DeleteAllData button, start processing");
                DBOperations.rmrf(this);

                Toast.makeText(this, "All data have been deleted", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
