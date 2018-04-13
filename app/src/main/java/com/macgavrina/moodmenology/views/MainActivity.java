package com.macgavrina.moodmenology.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.macgavrina.moodmenology.R;
import com.macgavrina.moodmenology.logging.Log;
import com.macgavrina.moodmenology.viewadapters.MainMenu;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //ToDo BUG timePickerDialog можно вызвать 2 раза
    //ToDo NEW переписать логи во всех класс на использование кастомного Log.java
    //ToDo NEW сохранение данных на сервере
    //ToDo REFACT перенести все отступы и т.д. в dimens.xml
    //ToDo REFACT можно ли вынести final переменные из классов в ресурсы?
    //ToDo NEW возможность указать физическое состояние (сонный, болезнь, холодно)
    //ToDo NEW комментарии к настроению и action
    //ToDo NEW сценарии для однотипных действий типа еды и сна
    //ToDO NEW добавить макеты для горизонтального расположения экрана


    private static final String LOG_TAG = "MoodMenology";
    private static final String DATE_IN_MILLIS_KEY="selectedDateInMillis";

    private Long selectedDateInMillis;

    private FloatingActionButton selectDateButtonMain;
    private DatePicker datePickerMain;
    private Calendar calendarMain;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePickerMain = (DatePicker) findViewById(R.id.ActivityMain_datePicker);

        selectDateButtonMain = (FloatingActionButton) findViewById(R.id.ActivityMain_addButton);
        selectDateButtonMain.setOnClickListener(this);

        initalizeCurrentDate();

        Log.d("MainActivity.onCreate: MainActivity building is finished, " + calendarMain.getTimeInMillis());

    }

/*    private void setupLogging() {
        //Enable or disable logs depending on application settings
        boolean isDebuggable = (BuildConfig.DEBUG);
        Log.setDebugLogging(isDebuggable);
    }*/

    private void initalizeCurrentDate() {
        //Блок чтобы устанавливалась текущая дата без перетыкивания в календаре
        calendarMain = Calendar.getInstance();

        datePickerMain.init(calendarMain.get(Calendar.YEAR), calendarMain.get(Calendar.MONTH), calendarMain.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            //То, что происходит при выборе даты в календаре

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendarMain.set(year, monthOfYear, dayOfMonth);

            }
        });
    }
    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.ActivityMain_addButton:

                selectedDateInMillis = calendarMain.getTimeInMillis();
                //Log.d(LOG_TAG, "selectedDateInMillis = " + String.valueOf(selectedDateInMillis));
                Intent intent = new Intent("com.macgavrina.moodmenology.fill.data");
                intent.putExtra(DATE_IN_MILLIS_KEY, selectedDateInMillis);
                startActivity(intent);

                break;
            default:
                break;
        }

    }

    // создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // обработка выбора пункта меню
    public boolean onOptionsItemSelected(MenuItem item) {

        MainMenu mainMenuProcessor = new MainMenu(item.getItemId());

        Intent intent = mainMenuProcessor.processOnMenyItemSelected(this);

        if (intent != null) {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);


    }


}
