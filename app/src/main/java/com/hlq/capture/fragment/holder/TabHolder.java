package com.hlq.capture.fragment.holder;

import android.content.Context;
import android.view.View;

/**
 * Created by hlq on 2019/1/5 0005.
 */

public interface TabHolder<T> {
    View getView(Context context);
    void onBindHolder(T t);
}
