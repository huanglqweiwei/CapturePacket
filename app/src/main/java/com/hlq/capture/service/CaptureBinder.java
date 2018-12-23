package com.hlq.capture.service;

import android.os.Binder;
import android.support.annotation.Nullable;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarCallback;
import net.lightbody.bmp.core.har.HarEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class CaptureBinder extends Binder {


    private BrowserMobProxyServer mProxyServer;
    private boolean mProxyStarted;
    private OnProxyStartedListener mStartedListener;

    public boolean isProxyStarted(){
        return mProxyStarted;
    }
    public void setStartedListener(OnProxyStartedListener listener){
        mStartedListener = listener;
    }

    public void setProxyServer(BrowserMobProxyServer proxyServer) {
        mProxyServer = proxyServer;
    }

    public void setProxyStarted(boolean proxyStarted) {
        mProxyStarted = proxyStarted;
        if (proxyStarted) {
            if (mStartedListener != null) {
                mStartedListener.onProxyStarted();
            }
        }
    }

    @Nullable
    public byte[] getCerBytes() {
        if (mProxyStarted) {

            FileInputStream stream = null;
            try {
                stream = new FileInputStream(mProxyServer.getCerPath());
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int length;
                while ((length = stream.read(bytes)) != -1){
                    outStream.write(bytes,0,length);
                }
                return outStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public void setHarCallback(HarCallback callback){
        mProxyServer.mHarCallback = callback;
    }

    public List<HarEntry> getHarEntries() {
        return mProxyServer == null ? null : mProxyServer.getHar().getLog().getEntries();
    }

    public interface OnProxyStartedListener {
        void onProxyStarted();
    }
}
