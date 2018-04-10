package com.macgavrina.moodmenology;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Irina on 10.04.2018.
 */

@ReportsCrashes(formUri = "https://collector.tracepot.com/6a3d7225")

public class MoodMenologyApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }

}
