package com.hlq.capture.fragment.nv;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.lightbody.bmp.core.har.INameValue;

import java.util.List;

/**
 * Created by hlq on 2019/1/5 0005.
 */

public class NVRecAdapter extends RecyclerView.Adapter<SpanViewHolder> {
    private List<? extends INameValue> mData;

    public NVRecAdapter(List<? extends INameValue> nameValues) {
        mData = nameValues;
    }

    @NonNull
    @Override
    public SpanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpanViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SpanViewHolder holder, int position) {
        holder.onBindViewHolder(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<? extends INameValue> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
