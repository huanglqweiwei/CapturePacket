package com.hlq.capture.fragment;

import android.support.design.widget.TabLayout;
import android.view.ViewGroup;

import net.lightbody.bmp.core.har.HarEntry;


/**
 * Created by hlq on 2018/12/25 0025.
 */

public class EntryTabDelegate implements TabLayout.OnTabSelectedListener {
    private final TabLayout mTabLayout;
    private final ViewGroup mContentView;
    private HarEntry mHarEntry;

    EntryTabDelegate(TabLayout tabLayout, ViewGroup contentView) {
        mTabLayout = tabLayout;
        mContentView = contentView;
    }

    private void initTab() {
        if (mTabLayout.getTabCount() == 0) {
            TabLayout tabLayout = mTabLayout;
            String[] tabTitles = {"Overview", "Headers", "Cookies", "Params", "Content"};
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
        switch (tab.getPosition()) {
            case 0://Overview
                break;
            case 1://Headers
                break;
            case 2://Cookies
                break;
            case 3://Params
                break;
            case 4://Content
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    void showHarEntry(HarEntry harEntry) {
        if (mHarEntry != harEntry) {
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
}
