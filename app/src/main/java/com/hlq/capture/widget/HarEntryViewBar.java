package com.hlq.capture.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlq.capture.R;

import java.util.ArrayList;

/**
 * Created by hlq on 2018/12/22 0022.
 */

public class HarEntryViewBar extends LinearLayout {
    public static final int TYPE_ID = 0;
    public static final int TYPE_METHOD = 1;
    public static final int TYPE_STATUS = 2;
    public static final int TYPE_HOST = 3;
    public static final int TYPE_PATH = 4;
    public static final int TYPE_TOTAL_SIZE = 5;
    public static final int TYPE_STARTED_TIME = 6;
    public static final int TYPE_TOTAL_TIME = 7;

    private static ArrayList<ItemEntry> sEntries;
    static {
        sEntries = new ArrayList<>();
        sEntries.add(new ItemEntry(TYPE_ID,"ID",20));
        sEntries.add(new ItemEntry(TYPE_METHOD,"Method",42));
        sEntries.add(new ItemEntry(TYPE_STATUS,"Status",35));
        sEntries.add(new ItemEntry(TYPE_HOST,"Host",140));
        sEntries.add(new ItemEntry(TYPE_PATH,"Path",180));
        sEntries.add(new ItemEntry(TYPE_TOTAL_SIZE,"Total size",60));
        sEntries.add(new ItemEntry(TYPE_STARTED_TIME,"Started time",70));
        sEntries.add(new ItemEntry(TYPE_TOTAL_TIME,"Total time",60));
    }

    private boolean mIsTitle;

    public HarEntryViewBar(Context context) {
        this(context,null);
    }

    public HarEntryViewBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HarEntryViewBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIsTitle = attrs != null;
        setOrientation(HORIZONTAL);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        Resources resources = getResources();
        setDividerDrawable(resources.getDrawable(R.drawable.divider_vertical_bar));
        DisplayMetrics dm = resources.getDisplayMetrics();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, resources.getDimensionPixelSize(R.dimen.dp_30)));
        for (int i = 0; i < sEntries.size(); i++) {
            createTextView(sEntries.get(i),dm.density);
        }
    }

    private void createTextView(ItemEntry entry, float density) {
        Context context = getContext();
        TextView textView = new TextView(context);
        if (mIsTitle) {
            textView.getPaint().setFakeBoldText(true);
            textView.setTextColor(Color.BLACK);
            textView.setText(entry.text);
        }
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(12);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine();
        LayoutParams params = new LayoutParams((int) (density * entry.width + 0.5), ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = params.rightMargin = (int) (density * 5 + 0.5);
        addView(textView, params);
    }

    public static class ItemEntry {
        int type;
        CharSequence text;
        int width;

        ItemEntry(int type, CharSequence text, int width) {
            this.type = type;
            this.text = text;
            this.width = width;
        }
    }
}
