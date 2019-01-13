package com.hlq.capture;

import android.support.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;

public class CaptureApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "bf984fa8b1", BuildConfig.DEBUG);
    }
}
