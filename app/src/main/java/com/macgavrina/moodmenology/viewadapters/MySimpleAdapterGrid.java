package com.macgavrina.moodmenology.viewadapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.macgavrina.moodmenology.model.Icons;

import java.util.List;
import java.util.Map;

public class MySimpleAdapterGrid extends SimpleAdapter {

    private static final int MOOD_TYPE_ID = 0;
    private static final int ACTION_GROUP_TYPE_ID=1;
    private static final int ACTION_TYPE_ID=2;

    private int actionGroupId;
    private int iconsType;
    private Icons icons;

    public MySimpleAdapterGrid(final Context context,
                               final List<? extends Map<String, ?>> data,
                               final int resource,
                               final String[] from,
                               final int[] to,
                               final int iconsType,
                               final int actionsGroupId) {

        super(context, data, resource, from, to);

        this.icons = new Icons();
        this.actionGroupId = actionsGroupId;
        this.iconsType = iconsType;
    }

    public MySimpleAdapterGrid(final Context context,
                               final List<? extends Map<String, ?>> data,
                               final int resource,
                               final String[] from,
                               final int[] to,
                               final int iconsType) {

        super(context, data, resource, from, to);

        this.icons = new Icons();
        this.iconsType = iconsType;
    }

    public void setViewImage(final ImageView v, final int value) {

        switch (iconsType) {
            case MOOD_TYPE_ID:
                super.setViewImage(v, icons.getMoodIconId(value));
                break;
            case ACTION_GROUP_TYPE_ID:
                super.setViewImage(v, icons.getActionGroupIconId(value));
                break;
            case ACTION_TYPE_ID:
                super.setViewImage(v, icons.getActionIconsId(actionGroupId, value));
                break;
            default:
                break;
        }

    }

}
