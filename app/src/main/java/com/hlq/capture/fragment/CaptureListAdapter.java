package com.hlq.capture.fragment;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hlq.capture.widget.HarEntryViewBar;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by hlq on 2018/12/23 0023.
 */

class CaptureListAdapter extends RecyclerView.Adapter<CaptureEntryViewHolder> {
    private List<HarEntry> mHarEntries;
    private SimpleDateFormat mDateFormat;

    public void setHarEntries(List<HarEntry> harEntries){
        mHarEntries = harEntries;
    }
    @NonNull
    @Override
    public CaptureEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaptureEntryViewHolder(parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CaptureEntryViewHolder holder, int position) {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);
        }
        HarEntry harEntry = mHarEntries.get(position);
        holder.setData(harEntry,position,mDateFormat);


    }

    @Override
    public int getItemCount() {
        return mHarEntries == null ? 0 : mHarEntries.size();
    }
}
