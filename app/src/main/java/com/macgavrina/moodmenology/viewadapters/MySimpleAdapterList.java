package com.macgavrina.moodmenology.viewadapters;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Irina on 21.02.2018.
 */

public class MySimpleAdapterList extends SimpleAdapter {


    //ToDo REFACT а нужен ли такой кастомный адаптер, который не меняет логику SimpleAdapter от которого наследуется?

    public MySimpleAdapterList(final FragmentActivity context,
                               final List<? extends Map<String, ?>> data,
                               final int resource,
                               final String[] from,
                               final int[] to) {

        super(context, data, resource, from, to);

    }

    public void setViewImage(final ImageView v, final int value) {
        super.setViewImage(v, value);
    }

}
