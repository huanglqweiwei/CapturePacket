package com.hlq.capture;

import android.util.Log;

import com.hlq.proxylib.BuildConfig;


public class HLog {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "CaptureHttps";

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(TAG + "_" + tag, msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(TAG + "_" + tag, msg);
        }
    }

    public static void e(Throwable e) {
        if (DEBUG) {
            Log.e(TAG, "HLog", e);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (DEBUG) {
            Log.e(TAG + "_" + tag, msg, e);
        }
    }


}
