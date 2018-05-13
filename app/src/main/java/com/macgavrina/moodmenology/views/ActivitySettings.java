package com.macgavrina.moodmenology.views;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.Services.ServicePrepareAllData;
import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener{

    private static final String PENDING_INTENT_KEY_NAME = "pendingIntent";
    public static final int STATUS_FINISHED = 1;
    private static final String TEXT_FOR_EMAIL_KEY_NAME = "textForEmail";
    private Button prepareAllDataButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button deleteAllDataButton = (Button) findViewById(R.id.ActivitySettings_deleteAllDataButton);
        deleteAllDataButton.setOnClickListener(this);

        prepareAllDataButton = (Button) findViewById(R.id.ActivitySettings_prepareAllDataButton);
        prepareAllDataButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.ActivitySettings_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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

            case (R.id.ActivitySettings_prepareAllDataButton):
                Log.d("User has pressed PrepareAllData button, start processing");


                Intent intent = new Intent (this, ServicePrepareAllData.class);
                PendingIntent pendingIntent = createPendingResult(1, intent, 0);
                intent.putExtra(PENDING_INTENT_KEY_NAME, pendingIntent);
                startService(intent);
                prepareAllDataButton.setText(R.string.sendAllDataButtonInProgress);
                prepareAllDataButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

            default:
                break;
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d("Service is finished");

        String textForEmail = data.getStringExtra(TEXT_FOR_EMAIL_KEY_NAME);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");

        intent.putExtra(android.content.Intent.EXTRA_EMAIL,
                "ivgavrina@gmail.com");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Data from MoodMenology");

        intent.putExtra(android.content.Intent.EXTRA_TEXT,
                textForEmail);

        startActivity(intent);

        prepareAllDataButton.setText(R.string.SendAllData);
        prepareAllDataButton.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
