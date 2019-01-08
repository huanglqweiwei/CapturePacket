package com.hlq.capture.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hlq on 2018/12/23 0023.
 */

class CaptureListAdapter extends RecyclerView.Adapter<CaptureEntryViewHolder> implements Filterable {
    private List<HarEntry> mHarEntries;
    private List<HarEntry> mOrgHarEntries;
    private SimpleDateFormat mDateFormat;
    private EntryTabDelegate mEntryTabDelegate;
    private CaptureFilter mFilter;
    private final Object mLock  = new Object();

    public void setHarEntries(List<HarEntry> harEntries){
        harEntries = new ArrayList<>(harEntries);
        mOrgHarEntries = harEntries;
        mHarEntries = harEntries;
    }
    @NonNull
    @Override
    public CaptureEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaptureEntryViewHolder(parent.getContext(),mEntryTabDelegate);
    }

    @Override
    public void onBindViewHolder(@NonNull CaptureEntryViewHolder holder, int position) {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        }
        HarEntry harEntry = mHarEntries.get(position);
        holder.setData(harEntry,position,mDateFormat);


    }

    @Override
    public int getItemCount() {
        return mHarEntries == null ? 0 : mHarEntries.size();
    }

    public void setEntryTabDelegate(EntryTabDelegate entryTabDelegate) {
        mEntryTabDelegate = entryTabDelegate;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CaptureFilter();
        }
        return mFilter;
    }


    public synchronized boolean isOrigin() {
        return mHarEntries == mOrgHarEntries;

    }

    public int addHarEntry(HarEntry entry) {
        if (mHarEntries != null) {
            mHarEntries.add(entry);
            int position = mHarEntries.size() - 1;
            notifyItemInserted(position);
            return position;
        }
        return -1;
    }

    public void onHarEntryChanged(HarEntry entry) {
        if (mHarEntries != null) {
            int index = mHarEntries.indexOf(entry);
            if (index != -1) {
                notifyItemChanged(index);
            }
        }
    }

    public void addToOriginList(HarEntry harEntry) {
        if (mOrgHarEntries != null) {
            mOrgHarEntries.add(harEntry);
        }
    }

    private class CaptureFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            if (mOrgHarEntries == null) {
                return null;
            }
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                results.values = mOrgHarEntries;
                results.count = mOrgHarEntries.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<HarEntry> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOrgHarEntries);
                }

                int count = values.size();
                ArrayList<HarEntry> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    HarEntry value = values.get(i);
                    HarRequest request = value.getRequest();
                    if (request != null) {
                        String url = request.getUrl();
                        if (url != null) {
                            String valueText = url.toLowerCase();

                            if (valueText.contains(prefixString)) {
                                newValues.add(value);
                            } else {
                                String[] words = valueText.split(" ");
                                for (String word : words) {
                                    if (word.contains(prefixString)) {
                                        newValues.add(value);
                                        break;
                                    }
                                }
                            }
                        }

                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null) {
                mHarEntries = (List<HarEntry>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
