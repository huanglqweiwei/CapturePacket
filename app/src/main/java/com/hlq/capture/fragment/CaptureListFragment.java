package com.hlq.capture.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hlq.capture.R;
import com.hlq.capture.service.CaptureBinder;

import net.lightbody.bmp.core.har.HarCallback;
import net.lightbody.bmp.core.har.HarEntry;

/**
 * Created by hlq on 2018/12/22 0022.
 */

public class CaptureListFragment extends Fragment implements HarCallback, Toolbar.OnMenuItemClickListener, SearchView.OnQueryTextListener {
    public static final String TAG = "CaptureListFragment";
    private View mRootView;
    private CaptureListAdapter mAdapter;
    private CaptureBinder mCaptureBinder;
    private SearchView mSearchView;
    private boolean mAutoScroll = true;
    private EntryTabDelegate mTabDelegate;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_capture_list, container, false);
            Toolbar toolBar = mRootView.findViewById(R.id.tool_bar);
            toolBar.inflateMenu(R.menu.capture_list_bar);
            toolBar.setOnMenuItemClickListener(this);
            MenuItem item = toolBar.getMenu().findItem(R.id.filter);
            mSearchView = (SearchView) item.getActionView();
            mSearchView.setQueryHint("过滤Url");
            mSearchView.setOnQueryTextListener(this);

            mRecyclerView = mRootView.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(null,LinearLayoutManager.VERTICAL,false));
            mHandler  = new RefreshHandler(this);
            mAdapter = new CaptureListAdapter();
            if (mCaptureBinder != null) {
                mAdapter.setHarEntries(mCaptureBinder.getHarEntries());
            }
            mRecyclerView.setAdapter(mAdapter);
            mTabDelegate = new EntryTabDelegate((TabLayout) mRootView.findViewById(R.id.tab), (ViewGroup) mRootView.findViewById(R.id.fl_detail));
            mTabDelegate.setRefreshHandler(mHandler);
            mAdapter.setEntryTabDelegate(mTabDelegate);
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
            msg.obj = harEntry;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onEntryChanged(HarEntry harEntry,int changeItem) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = RefreshHandler.WHAT_HAR_ENTRY_CHANGED;
            msg.obj = harEntry;
            msg.arg1 = changeItem;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onClearEntries() {
        if (mCaptureBinder != null && mAdapter != null) {
            mAdapter.clearEntries();
            mAdapter.setHarEntries(mCaptureBinder.getHarEntries());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                    System.exit(0);
                }
                return true;
            case R.id.help:

                break;
            case R.id.clear:
                if (mCaptureBinder != null) {
                    mCaptureBinder.clearHarEntries();
                    onClearEntries();
                }
                break;
            case R.id.auto_scroll:
                mAutoScroll = !item.isChecked();
                item.setChecked(mAutoScroll);
                break;
            case R.id.float_window:
                Toast toast = Toast.makeText(getActivity(), "开发中，敬请期待！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSearchView != null && mSearchView.hasFocus()) {
            mSearchView.clearFocus();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return false;
    }

    private int mDivid = 0;

    public void onDispatchTouchEvent(MotionEvent ev) {
        if (mSearchView != null && mSearchView.hasFocus()) {
            if (mDivid == 0) {
                int[] leftTop = new int[2];
                mSearchView.getLocationInWindow(leftTop);
                mDivid = mSearchView.getHeight() + leftTop[1];
            }
            if (ev.getY() > mDivid) {
                mSearchView.clearFocus();
            }
        }
    }

    public static class RefreshHandler extends Handler{
        public static final int WHAT_HAR_ENTRY_ADD = 0;
        private static final int WHAT_DATA_SET_CHANGE = 1;
        public static final int WHAT_HAR_ENTRY_CHANGED = 2;
        public static final int WHAT_HAR_ENTRY_CHANGED_2 = 3;
        private CaptureListFragment mFragment;

        RefreshHandler(CaptureListFragment fragment){
            mFragment = fragment;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_HAR_ENTRY_ADD:
                    if (mFragment.mAdapter != null) {
                        mFragment.mAdapter.addToOriginList((HarEntry)msg.obj);
                        if (mFragment.mAdapter.isOrigin()) {
                            int position = mFragment.mAdapter.getItemCount() - 1;
                            if (position > -1) {
                                mFragment.mAdapter.notifyItemInserted(position);
                                mFragment.autoScroll(position);
                            }
                        }
                    }
                    break;
                case WHAT_DATA_SET_CHANGE:
                    if (mFragment.mAdapter != null) {
                        mFragment.mAdapter.notifyDataSetChanged();
                    }
                    break;
                case WHAT_HAR_ENTRY_CHANGED:
                    if (mFragment.mAdapter != null) {
                        HarEntry harEntry = (HarEntry) msg.obj;
                        if (mFragment.mAdapter.isOrigin()) {
                            mFragment.mAdapter.onHarEntryChanged(harEntry);
                        }else{
                            SearchView searchView = mFragment.mSearchView;
                            if (searchView != null) {
                                String queryString = searchView.getQuery().toString().trim();
                                if (!TextUtils.isEmpty(queryString)){
                                    int changeItem = msg.arg1;
                                    if (changeItem == HarCallback.ITEM_REQUEST) {
                                        String url = harEntry.getRequest().getUrl();
                                        if (!TextUtils.isEmpty(url)) {
                                            url = url.toLowerCase();
                                            if (url.contains(queryString)) {
                                                int position = mFragment.mAdapter.addHarEntry(harEntry);
                                                mFragment.autoScroll(position);
                                            }
                                        }
                                    } else {
                                        mFragment.mAdapter.onHarEntryChanged(harEntry);
                                    }
                                }
                            }
                        }
                    }

                    break;
                case WHAT_HAR_ENTRY_CHANGED_2:
                    if (mFragment.mAdapter != null) {
                        HarEntry entry = (HarEntry) msg.obj;
                        mFragment.mAdapter.onHarEntryChanged(entry);
                    }
                    break;
            }
        }
    }

    private void autoScroll(int position) {
        if (mAutoScroll && mRecyclerView != null) {
            if (position > -1) {
                mRecyclerView.smoothScrollToPosition(position);
            }
        }
    }
}
