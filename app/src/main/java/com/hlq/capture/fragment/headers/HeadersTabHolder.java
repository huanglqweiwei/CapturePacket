package com.hlq.capture.fragment.headers;

import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;

import com.hlq.capture.fragment.holder.TabHolder;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

/**
 * Created by hlq on 2019/1/6 0006.
 */

public class HeadersTabHolder implements TabHolder {

    private HeaderExpandAdapter mAdapter;
    private ExpandableListView mListView;

    @Override
    public View getView(Context context) {
        if (mListView == null) {
            mListView = new ExpandableListView(context);
            mAdapter = new HeaderExpandAdapter();
            mListView.setAdapter(mAdapter);
            mListView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            mListView.expandGroup(0);
            mListView.expandGroup(1);
        }
        return mListView;
    }

    @Override
    public void onBindHolder(HarEntry harEntry,String tabText) {
        HarRequest request = harEntry.getRequest();
        HarResponse response = harEntry.getResponse();
        mAdapter.setHeaders(request == null ? null :request.getHeaders(),response == null ? null :response.getHeaders());
    }
}
