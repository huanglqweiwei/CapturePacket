package com.hlq.capture;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.security.KeyChain;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.hlq.capture.fragment.CaptureListFragment;
import com.hlq.capture.service.CaptureBinder;
import com.hlq.capture.service.CaptureService;
import com.hlq.capture.util.ProxyUtil;
import com.hlq.capture.util.SPUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CaptureBinder.OnProxyStartedListener {


    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_INSTALL_CER = 2;
    private ServiceConnection mConn;
    private CaptureBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_content,new CaptureListFragment(),CaptureListFragment.TAG)
                    .commitAllowingStateLoss();
        }
        startProxy();
    }

    private void startProxy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            bindCaptureService();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE);
        }
    }


    private void bindCaptureService() {
        if (mConn != null) {
            return;
        }
        Intent intent = new Intent(this, CaptureService.class);
        File keyStoreDir = new File(Environment.getExternalStorageDirectory(), "capture");
        if (!keyStoreDir.exists() || !keyStoreDir.isDirectory()) {
            keyStoreDir.mkdir();
        }
        intent.putExtra(CaptureService.KEY_STORE_DIR,keyStoreDir.getAbsolutePath());
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (CaptureBinder) service;
                if (mBinder.isProxyStarted()) {
                    onProxyStarted();
                } else {
                    mBinder.setStartedListener(MainActivity.this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, mConn ,BIND_AUTO_CREATE);

    }

    private void installCertificate(byte[] cerBytes){
        Intent intent = KeyChain.createInstallIntent();
        intent.putExtra(KeyChain.EXTRA_CERTIFICATE, cerBytes);
        intent.putExtra(KeyChain.EXTRA_NAME, "CapturePacket CA Certificate");
        startActivityForResult(intent, REQUEST_INSTALL_CER);
    }

    @Override
    public void onProxyStarted() {
        if (!SPUtil.getBoolean(this,SPUtil.KEY_IS_INSTALL_CER,false)) {
            byte[] cerBytes = mBinder.getCerBytes();
            if (cerBytes != null) {
                installCertificate(cerBytes);
            }
        }
        boolean result = ProxyUtil.setProxy(this, CaptureService.PROXY_PORT);
        final String text = result ? "Set proxyHost success !!!" : "Set proxyHost failure ~~~";
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Snackbar.make(getWindow().getDecorView(),text,Snackbar.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(getWindow().getDecorView(),text,Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        if (result) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CaptureListFragment.TAG);
            if (fragment instanceof CaptureListFragment) {
                ((CaptureListFragment) fragment).onProxyStarted(mBinder);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        bindCaptureService();
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[i])) {

                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL_CER) {
            String tipText;
            if (resultCode == RESULT_OK) {
                SPUtil.putBoolean(this,SPUtil.KEY_IS_INSTALL_CER,true);
                tipText = "Install certificate success !!!";
            } else {
                tipText = "Install certificate failure !!!";
            }
            Snackbar.make(getWindow().getDecorView(),tipText,Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConn != null) {
            unbindService(mConn);
        }

    }

    @Override
    public void onBackPressed() {
        if (!moveTaskToBack(false)) {
            super.onBackPressed();
        }
    }
}
