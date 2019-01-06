package com.hlq.capture.fragment;

import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.hlq.capture.fragment.headers.HeadersTabHolder;
import com.hlq.capture.fragment.holder.TabHolder;
import com.hlq.capture.fragment.nv.NameValueHolder;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarPostData;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.core.har.INameValue;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hlq on 2018/12/25 0025.
 */

public class EntryTabDelegate implements TabLayout.OnTabSelectedListener {
    private final TabLayout mTabLayout;
    private final ViewGroup mContentView;
    private HarEntry mHarEntry;
    private SparseArray<TabHolder> mSparseArray;
    private static final int TYPE_NAME_VALUE = 0;
    private static final int TYPE_HEADS = 1;
    private static final int TYPE_CONTENT = 2;
    public int mClickedPosition = -1;
    private CaptureListAdapter mRecAdapter;

    EntryTabDelegate(TabLayout tabLayout, ViewGroup contentView) {
        mTabLayout = tabLayout;
        mContentView = contentView;
    }

    private void initTab() {
        if (mTabLayout.getTabCount() == 0) {
            TabLayout tabLayout = mTabLayout;
            String[] tabTitles = {"Overview", "Headers", "Cookies","Query", "Params", "Content"};
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
        View view = null;
        int position = tab.getPosition();
        int viewType = getTabViewType(position);
        TabHolder tabHolder = getTabHolder(viewType);
        if (tabHolder != null) {
            view = tabHolder.getView(mContentView.getContext());
            updateData(position, viewType,tabHolder);
        }
        if (view != null) {
            mContentView.addView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private int getTabViewType(int position) {
        switch (position){
            case 0://Overview
            case 2://Cookies
            case 3://Params
            case 4://Params
                return TYPE_NAME_VALUE;
            case 1://Headers
                return TYPE_HEADS;
            case 5://Content
                return TYPE_CONTENT;
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
                case TYPE_NAME_VALUE://nameValue
                    tabHolder = new NameValueHolder();
                    break;
                case TYPE_HEADS://Headers
                    tabHolder = new HeadersTabHolder();
                    break;
                case TYPE_CONTENT://Content
                    break;
            }
            if (mSparseArray == null) {
                mSparseArray = new SparseArray<>(3);
            }
            mSparseArray.put(viewType,tabHolder);
        }
        return tabHolder;
    }

    private void updateData(int position, int viewType, TabHolder tabHolder) {
        if (viewType == TYPE_NAME_VALUE){
            List<? extends INameValue> nameValues = null;
            switch (position){
                case 0://Overview
                    nameValues = getOverViewList();
                    break;
                case 2://Cookies
                    HarRequest request = mHarEntry.getRequest();
                    if (request != null) {
                        nameValues = request.getCookies();
                    }
                    break;
                case 3://Query
                    HarRequest request1 = mHarEntry.getRequest();
                    if (request1 != null) {
                        nameValues = request1.getQueryString();
                    }
                    break;
                case 4://Params
                    HarRequest harRequest = mHarEntry.getRequest();
                    if (harRequest != null) {
                        HarPostData postData = harRequest.getPostData();
                        if (postData != null) {
                            nameValues = postData.getParams();
                            if (nameValues == null || nameValues.size() == 0) {
                                String text = postData.getText();
                                if (!TextUtils.isEmpty(text)) {
                                    List<HarNameValuePair> pairs = new ArrayList<>();
                                    pairs.add(new HarNameValuePair("PostData",text));
                                    nameValues = pairs;
                                }

                            }
                        }
                    }
                    break;
            }
            NameValueHolder holder = (NameValueHolder) tabHolder;
            holder.onBindHolder(nameValues);
        } else if (viewType == TYPE_HEADS){
            ((HeadersTabHolder)tabHolder).onBindHolder(mHarEntry);
        }
    }

    private List<? extends INameValue> getOverViewList() {
        ArrayList<HarNameValuePair> pairs = new ArrayList<>();
        HarRequest harRequest = mHarEntry.getRequest();
        HarResponse harResponse = mHarEntry.getResponse();
        if (harRequest != null) {
            String url = harRequest.getUrl();
            try {
                url = URLDecoder.decode(url, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            pairs.add(new HarNameValuePair("URL", url));

            pairs.add(new HarNameValuePair("Method", harRequest.getMethod()));
        }
        if (harResponse != null) {
            pairs.add(new HarNameValuePair("Code", harResponse.getStatus() + ""));
            pairs.add(new HarNameValuePair("Size", harResponse.getBodySize() + "Bytes"));
        }
        pairs.add(new HarNameValuePair("TotalTime", mHarEntry.getTime() + "ms"));
        return pairs;
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    void showHarEntry(HarEntry harEntry, int clickedPosition) {
        if (mClickedPosition >= 0 && mClickedPosition < mRecAdapter.getItemCount()) {
            mRecAdapter.notifyItemChanged(mClickedPosition);
        }
        mClickedPosition = clickedPosition;
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

    public void setRecAdapter(CaptureListAdapter recAdapter) {
        mRecAdapter = recAdapter;
    }
}
