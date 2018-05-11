package com.macgavrina.moodmenology.Services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.views.ActivitySettings;

import java.util.concurrent.TimeUnit;

public class ServicePrepareAllData extends IntentService {

    private static final String PENDING_INTENT = "pendingIntent";

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

        //ToDo NEW заменить здесь sleep на формирование текста и убрать после этого кнопку из меню

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Context context = getApplicationContext();
        //String textForEmail = DBOperations.getAllDataForEmail(context);

        try {
            pendingIntent.send(ActivitySettings.STATUS_FINISHED);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
