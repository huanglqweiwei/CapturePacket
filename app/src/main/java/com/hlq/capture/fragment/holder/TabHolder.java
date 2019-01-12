package com.hlq.capture.fragment.holder;

import android.content.Context;
import android.view.View;

import net.lightbody.bmp.core.har.HarEntry;

/**
 * Created by hlq on 2019/1/5 0005.
 */

public interface TabHolder {
    View getView(Context context);
    void onBindHolder(HarEntry harEntry,String tabText);
}
