package com.hlq.capture.fragment;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hlq.capture.widget.HarEntryViewBar;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hlq on 2018/12/23 0023.
 */

public class CaptureEntryViewHolder extends RecyclerView.ViewHolder {

    public final HarEntryViewBar mBarView;

    public CaptureEntryViewHolder(Context context) {
        super(new HarEntryViewBar(context));
        mBarView = (HarEntryViewBar) itemView;
    }

    public void setItemContent(int type, String content) {
        TextView textView = (TextView) mBarView.getChildAt(type);
        textView.setText(content);
    }

    void setData(HarEntry harEntry,int position,SimpleDateFormat format){
        for (int i = 0; i < mBarView.getChildCount(); i++) {
            TextView textView = (TextView) mBarView.getChildAt(i);
            textView.setText("");
        }

        HarRequest request = harEntry.getRequest();
        HarResponse response = harEntry.getResponse();
        setItemContent(HarEntryViewBar.TYPE_ID,String.valueOf(position));
        Date startedDateTime = harEntry.getStartedDateTime();
        if (startedDateTime != null) {

            setItemContent(HarEntryViewBar.TYPE_STARTED_TIME, format.format(startedDateTime));
        }

        if (request != null) {
            setItemContent(HarEntryViewBar.TYPE_METHOD,request.getMethod());
            String url = request.getUrl();
            if (url != null) {
                Uri uri = Uri.parse(url);
                setItemContent(HarEntryViewBar.TYPE_HOST,uri.getHost());
                setItemContent(HarEntryViewBar.TYPE_PATH,uri.getPath());
            }
        }
        if (response != null) {
            setItemContent(HarEntryViewBar.TYPE_STATUS,String.valueOf(response.getStatus()));
            setItemContent(HarEntryViewBar.TYPE_TOTAL_SIZE,response.getBodySize()+ " bytes");
            setItemContent(HarEntryViewBar.TYPE_TOTAL_TIME,harEntry.getTime() + "ms");
        }
    }
}
