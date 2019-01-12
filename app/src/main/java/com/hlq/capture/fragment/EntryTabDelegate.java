package com.hlq.capture.fragment;

import android.os.Message;
import android.support.design.widget.TabLayout;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.hlq.capture.fragment.headers.HeadersTabHolder;
import com.hlq.capture.fragment.holder.ContentTabHolder;
import com.hlq.capture.fragment.holder.TabHolder;

import net.lightbody.bmp.core.har.HarEntry;


/**
 * Created by hlq on 2018/12/25 0025.
 */

public class EntryTabDelegate implements TabLayout.OnTabSelectedListener {
    private final TabLayout mTabLayout;
    private final ViewGroup mContentView;
    public HarEntry mHarEntry;
    private SparseArray<TabHolder> mSparseArray;
    private static final int TYPE_HEADS = 0;
    private static final int TYPE_CONTENT = 1;
    public static final String TAB_OVERVIEW = "Overview";
    public static final String TAB_HEADERS = "Headers";
    public static final String TAB_COOKIES = "Cookies";
    public static final String TAB_QUERY = "Query";
    public static final String TAB_PARAMS = "Params";
    public static final String TAB_CONTENT = "Content";

    private CaptureListFragment.RefreshHandler mRefreshHandler;

    EntryTabDelegate(TabLayout tabLayout, ViewGroup contentView) {
        mTabLayout = tabLayout;
        mContentView = contentView;
    }

    private void initTab() {
        if (mTabLayout.getTabCount() == 0) {
            TabLayout tabLayout = mTabLayout;
            String[] tabTitles = {TAB_OVERVIEW, TAB_HEADERS, TAB_COOKIES,TAB_QUERY,TAB_PARAMS,TAB_CONTENT};
            for (String title : tabTitles) {
                TabLayout.Tab tab = tabLayout.newTab();
                tab.setText(title);
                tabLayout.addTab(tab,false);
            }
            tabLayout.addOnTabSelectedListener(this);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mContentView.removeAllViews();
        if (mHarEntry != null) {
            CharSequence text = tab.getText();
            if (text != null) {
                String tabText = text.toString();
                TabHolder tabHolder = getTabHolder(getViewTypeByTab(tabText));
                if (tabHolder != null) {
                    View view = tabHolder.getView(mContentView.getContext());
                    if (view.getLayoutParams() == null) {
                         mContentView.addView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else {
                        mContentView.addView(view);
                    }
                    tabHolder.onBindHolder(mHarEntry,tabText);
                }
            }
        }
    }

    private int getViewTypeByTab(String tab) {
        switch (tab){
            case TAB_OVERVIEW://Overview
            case TAB_COOKIES://Cookies
            case TAB_QUERY://Query
            case TAB_PARAMS://Params
            case TAB_CONTENT://Content
                return TYPE_CONTENT;
            case TAB_HEADERS://Headers
                return TYPE_HEADS;
        }
        return -1;
    }

    private TabHolder getTabHolder(int viewType) {
        TabHolder tabHolder = null;
        if (mSparseArray != null) {
            tabHolder = mSparseArray.get(viewType);
        }
        if (tabHolder == null) {
            switch (viewType) {
                case TYPE_HEADS://Headers
                    tabHolder = new HeadersTabHolder();
                    break;
                case TYPE_CONTENT://Content
                    tabHolder = new ContentTabHolder();
                    break;
            }
            if (mSparseArray == null) {
                mSparseArray = new SparseArray<>(2);
            }
            mSparseArray.put(viewType,tabHolder);
        }
        return tabHolder;
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    void showHarEntry(HarEntry harEntry) {
        if (mHarEntry != harEntry) {
            if (mHarEntry != null && mRefreshHandler != null) {
                Message msg = Message.obtain();
                msg.what = CaptureListFragment.RefreshHandler.WHAT_HAR_ENTRY_CHANGED_2;
                msg.obj = mHarEntry;
                mRefreshHandler.sendMessage(msg);
            }
            mHarEntry = harEntry;
            initTab();
            int selectedTabPosition = mTabLayout.getSelectedTabPosition();
            if (selectedTabPosition >= 0) {
                onTabSelected(mTabLayout.getTabAt(selectedTabPosition));
            } else {
                mTabLayout.getTabAt(0).select();
            }
        }

    }

    public void setRefreshHandler(CaptureListFragment.RefreshHandler refreshHandler) {
        mRefreshHandler = refreshHandler;
    }
}
