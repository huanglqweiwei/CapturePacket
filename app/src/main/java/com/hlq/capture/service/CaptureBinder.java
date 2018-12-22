package com.hlq.capture.service;

import android.os.Binder;
import android.support.annotation.Nullable;

import net.lightbody.bmp.BrowserMobProxyServer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

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

    public interface OnProxyStartedListener {
        void onProxyStarted();
    }
}
