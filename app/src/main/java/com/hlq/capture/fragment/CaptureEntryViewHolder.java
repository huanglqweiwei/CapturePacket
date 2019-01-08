package com.hlq.capture.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hlq.capture.widget.HarEntryViewBar;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hlq on 2018/12/23 0023.
 */

public class CaptureEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final HarEntryViewBar mBarView;
    private EntryTabDelegate mEntryTabDelegate;
    private HarEntry mHarEntry;

    public CaptureEntryViewHolder(Context context, EntryTabDelegate entryTabDelegate) {
        super(new HarEntryViewBar(context));
        mEntryTabDelegate = entryTabDelegate;
        mBarView = (HarEntryViewBar) itemView;
        mBarView.setOnClickListener(this);
    }

    private void setDefaultBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBarView.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.LTGRAY),new ColorDrawable(Color.WHITE),null));
        }
    }

    public void setItemContent(int type, String content) {
        TextView textView = (TextView) mBarView.getChildAt(type);
        if (textView != null) {
            textView.setText(content);
        }
    }

    void setData(HarEntry harEntry,int position,SimpleDateFormat format){
        mHarEntry = harEntry;
        if (mEntryTabDelegate != null && mEntryTabDelegate.mHarEntry == harEntry ) {
            onClick(mBarView);
        } else {
            setDefaultBackground();
        }

        HarRequest request = harEntry.getRequest();
        HarResponse response = harEntry.getResponse();
        setItemContent(HarEntryViewBar.TYPE_ID,String.valueOf(position));
        Date startedDateTime = harEntry.getStartedDateTime();
        if (startedDateTime != null) {
            setItemContent(HarEntryViewBar.TYPE_STARTED_TIME, format.format(startedDateTime));
        } else {
            setItemContent(HarEntryViewBar.TYPE_STARTED_TIME, "");
        }

        if (request != null) {
            setItemContent(HarEntryViewBar.TYPE_METHOD,request.getMethod());
            String url = request.getUrl();
            if (url != null) {
                Uri uri = Uri.parse(url);
                setItemContent(HarEntryViewBar.TYPE_HOST,uri.getHost());
                setItemContent(HarEntryViewBar.TYPE_PATH,uri.getPath());
            }else {
                setItemContent(HarEntryViewBar.TYPE_HOST,"");
                setItemContent(HarEntryViewBar.TYPE_PATH,"");
            }
        } else {
            setItemContent(HarEntryViewBar.TYPE_METHOD,"");
            setItemContent(HarEntryViewBar.TYPE_HOST,"");
            setItemContent(HarEntryViewBar.TYPE_PATH,"");
        }
        if (response != null) {
            setItemContent(HarEntryViewBar.TYPE_STATUS,String.valueOf(response.getStatus()));
            setItemContent(HarEntryViewBar.TYPE_TOTAL_SIZE,response.getBodySize()+ " bytes");
            setItemContent(HarEntryViewBar.TYPE_TOTAL_TIME,harEntry.getTime() + "ms");
        } else {
            setItemContent(HarEntryViewBar.TYPE_STATUS,"");
            setItemContent(HarEntryViewBar.TYPE_TOTAL_SIZE,"");
            setItemContent(HarEntryViewBar.TYPE_TOTAL_TIME,"");
        }
    }

    @Override
    public void onClick(View v) {
        if (mEntryTabDelegate != null) {
            mEntryTabDelegate.showHarEntry(mHarEntry);
        }
        mBarView.setBackgroundColor(Color.GREEN);
    }
}
