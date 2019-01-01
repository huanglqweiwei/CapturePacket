package com.hlq.capture.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hlq.capture.R;
import com.hlq.capture.service.CaptureBinder;

import net.lightbody.bmp.core.har.HarCallback;
import net.lightbody.bmp.core.har.HarEntry;

/**
 * Created by hlq on 2018/12/22 0022.
 */

public class CaptureListFragment extends Fragment implements HarCallback {
    public static final String TAG = "CaptureListFragment";
    private View mRootView;
    private CaptureListAdapter mAdapter;
    private CaptureBinder mCaptureBinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_capture_list, container, false);
            Toolbar toolBar = mRootView.findViewById(R.id.tool_bar);
            toolBar.inflateMenu(R.menu.capture_list_bar);
            RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(null,LinearLayoutManager.VERTICAL,false));
            mHandler  = new RefreshHandler(this);
            mAdapter = new CaptureListAdapter();
            if (mCaptureBinder != null) {
                mAdapter.setHarEntries(mCaptureBinder.getHarEntries());
            }
            recyclerView.setAdapter(mAdapter);
            EntryTabDelegate entryTabDelegate = new EntryTabDelegate((TabLayout) mRootView.findViewById(R.id.tab), (ViewGroup) mRootView.findViewById(R.id.fl_detail));
            mAdapter.setEntryTabDelegate(entryTabDelegate);
        }
        return mRootView;
    }

    public void onProxyStarted(CaptureBinder captureBinder) {
        mCaptureBinder = captureBinder;
        if (mAdapter != null) {
            mAdapter.setHarEntries(mCaptureBinder.getHarEntries());
            mHandler.sendEmptyMessage(RefreshHandler.WHAT_DATA_SET_CHANGE);
        }
        mCaptureBinder.setHarCallback(this);
    }
    private RefreshHandler mHandler;
    @Override
    public void onAddEntry(HarEntry harEntry, int position) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = RefreshHandler.WHAT_HAR_ENTRY_ADD;
            msg.arg1 = position;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onEntryChanged(HarEntry harEntry) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = RefreshHandler.WHAT_HAR_ENTRY_CHANGED;
            msg.arg1 = mCaptureBinder.getHarEntries().indexOf(harEntry);
            mHandler.sendMessage(msg);
        }
    }

    private static class RefreshHandler extends Handler{
        public static final int WHAT_HAR_ENTRY_ADD = 0;
        private static final int WHAT_DATA_SET_CHANGE = 1;
        public static final int WHAT_HAR_ENTRY_CHANGED = 2;
        private CaptureListFragment mFragment;

        RefreshHandler(CaptureListFragment fragment){
            mFragment = fragment;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_HAR_ENTRY_ADD:
                    if (mFragment.mAdapter != null) {
                        mFragment.mAdapter.notifyItemInserted(msg.arg1);
                    }
                    break;
                case WHAT_DATA_SET_CHANGE:
                    if (mFragment.mAdapter != null) {
                        mFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case WHAT_HAR_ENTRY_CHANGED:
                    if (mFragment.mAdapter != null) {
                        mFragment.mAdapter.notifyItemChanged(msg.arg1);
                    }
                    break;
            }
        }
    }
}
