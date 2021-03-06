package com.macgavrina.moodmenology.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.macgavrina.moodmenology.controllers.DBOperations;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.views.ActivitySettings;

public class ServicePrepareAllData extends IntentService {

    //ToDo REFACT проверить наличие прилоежний которые могут обработать интент (https://developer.android.com/training/basics/intents/sending)
    //ToDo REFACT Сделать service недоступным для других приложений - https://developer.android.com/guide/components/services
    //ToDo NEW Поиграться с обработкой эвента от IntentService - https://developer.android.com/training/run-background-service/report-status

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
    protected void onHandleIntent(Intent intent) {
        Log.d("handling intent");

        PendingIntent pendingIntent = intent.getParcelableExtra(PENDING_INTENT);

/*        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        String textForEmail = DBOperations.getAllDataForEmail(this);
        intent.putExtra(TEXT_FOR_EMAIL_KEY_NAME, textForEmail);

        try {
            pendingIntent.send(ServicePrepareAllData.this, ActivitySettings.STATUS_FINISHED, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
