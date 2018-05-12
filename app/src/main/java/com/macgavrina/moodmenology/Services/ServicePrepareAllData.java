package com.macgavrina.moodmenology.Services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.views.ActivitySettings;

import java.util.concurrent.TimeUnit;

public class ServicePrepareAllData extends IntentService {

    private static final String PENDING_INTENT = "pendingIntent";
    private static final String TEXT_FOR_EMAIL_KEY_NAME = "textForEmail";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ServicePrepareAllData(String name) {
        super(name);
    }

    public ServicePrepareAllData() {
        super("DefaultName");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("handling intent");

        PendingIntent pendingIntent = intent.getParcelableExtra(PENDING_INTENT);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String textForEmail = DBOperations.getAllDataForEmail(this);
        intent.putExtra(TEXT_FOR_EMAIL_KEY_NAME, textForEmail);

        try {
            pendingIntent.send(ServicePrepareAllData.this, ActivitySettings.STATUS_FINISHED, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
