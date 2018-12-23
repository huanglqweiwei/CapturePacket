package com.hlq.capture.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hlq.capture.MainActivity;
import com.hlq.capture.R;
import com.hlq.capture.HLog;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CaptureService extends Service implements Runnable {
    private static final String TAG = "CaptureService";
    private BrowserMobProxyServer mProxyServer;
    private String mKeyStoreDir;
    public static final String KEY_STORE_DIR = "keyStoreDir";
    private CaptureBinder mCaptureBinder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        HLog.w(TAG, "onBind");
        mKeyStoreDir = intent.getStringExtra(KEY_STORE_DIR);

        mCaptureBinder = new CaptureBinder();
        mCaptureBinder.setProxyServer(mProxyServer);
        mCaptureBinder.setProxyStarted(mProxyStarted);
        return mCaptureBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HLog.w(TAG, "onCreate");
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("Capture is running")
                .setContentTitle("Capture Packet")
                .setTicker("Capture")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(PendingIntent.getActivity(this,0,new Intent(this, MainActivity.class),0))
                .build();
        startForeground(R.id.capture_notification, notification);

        new Thread(this).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HLog.w(TAG, "onStartCommand");
        mKeyStoreDir = intent.getStringExtra(KEY_STORE_DIR);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        HLog.w(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mProxyServer != null) {
            mProxyServer.stop();
            HLog.w(TAG, "onDestroy,mProxyServer stop");
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        HLog.w(TAG, "onLowMemory()");
        Har har = mProxyServer.getHar();
        if (har != null) {
            har.getLog().clearAllEntries();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    private boolean mProxyStarted = false;
    public static final int PROXY_PORT = 8888;

    @Override
    public void run() {
        mProxyServer = new BrowserMobProxyServer(mKeyStoreDir);
        mProxyServer.setTrustAllServers(true);
        mProxyServer.start(PROXY_PORT);

        mProxyServer.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.RESPONSE_CONTENT);

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(System.currentTimeMillis()));
        mProxyServer.newHar(time);
        mProxyStarted = true;
        if (mCaptureBinder != null) {
            mCaptureBinder.setProxyServer(mProxyServer);
            mCaptureBinder.setProxyStarted(true);
        }

    }
}
