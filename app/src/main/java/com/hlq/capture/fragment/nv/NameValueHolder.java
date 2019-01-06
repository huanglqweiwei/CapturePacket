package com.hlq.capture.fragment.nv;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hlq.capture.fragment.holder.TabHolder;

import net.lightbody.bmp.core.har.INameValue;

import java.util.List;

/**
 * Created by hlq on 2019/1/5 0005.
 */

public class NameValueHolder implements TabHolder<List<? extends INameValue>> {

    private RecyclerView mRecyclerView;
    private NVRecAdapter mNVRecAdapter;

    @Override
    public View getView(Context context) {
        if (mRecyclerView == null) {
            mRecyclerView = new RecyclerView(context);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(null));
        }
        return mRecyclerView;
    }

    @Override
    public void onBindHolder(List<? extends INameValue> nameValues) {
        if (mNVRecAdapter == null) {
            mNVRecAdapter = new NVRecAdapter(nameValues);
            mRecyclerView.setAdapter(mNVRecAdapter);
        } else {
            mNVRecAdapter.setData(nameValues);
        }
    }
}
