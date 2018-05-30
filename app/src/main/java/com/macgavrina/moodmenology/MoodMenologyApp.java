package com.macgavrina.moodmenology;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formUri = "https://collector.tracepot.com/6a3d7225")

public class MoodMenologyApp extends Application {

    //ToDo BUG timePickerDialog можно вызвать 2 раза

    //ToDo REFACT перейти на RecyclerView
    //ToDo REFACT пройтись по всем предупреждениям студии в xml (и потом проверить что при коммите нет ошибок)
    //ToDo REFACT перенести все отступы и т.д. в dimens.xml
    //ToDo REFACT можно ли вынести final переменные из классов в ресурсы?
    //ToDo REFACT использовать CursorLoader для работы с БД (но сначала разобраться с какими view умеет работать CursorAdapter)

    //ToDo NEW возможность указать физическое состояние (сонный, болезнь, холодно)
    //ToDo NEW комментарии к настроению и action
    //ToDo NEW сценарии для однотипных действий типа еды и сна
    //ToDo NEW подумать нужна ли здесь лямбда-архитектура

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }

}
