package com.hlq.capture;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.security.KeyChain;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.hlq.capture.fragment.CaptureListFragment;
import com.hlq.capture.fragment.HelpFragment;
import com.hlq.capture.service.CaptureBinder;
import com.hlq.capture.service.CaptureService;
import com.hlq.capture.util.ProxyUtil;
import com.hlq.capture.util.SPUtil;

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
        intent.putExtra(CaptureService.KEY_STORE_DIR,SPUtil.getCaptureDir().getAbsolutePath());
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (CaptureBinder) service;
                if (mBinder.getProxyState() != CaptureService.STATE_INIT) {
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

    @Override
    public void onProxyStarted() {
        if (!mBinder.isProxyStarted()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(getWindow().getDecorView(),"端口被占用或其他异常，启动失败！",Snackbar.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (!SPUtil.getBoolean(this,SPUtil.KEY_IS_INSTALL_CER,false)) {
            byte[] cerBytes = mBinder.getCerBytes();
            if (cerBytes != null) {
                Intent intent = KeyChain.createInstallIntent();
                intent.putExtra(KeyChain.EXTRA_CERTIFICATE, cerBytes);
                intent.putExtra(KeyChain.EXTRA_NAME, "CapturePacket CA Certificate");
                startActivityForResult(intent, REQUEST_INSTALL_CER);
            }
        }
        boolean result = ProxyUtil.setProxy(this, CaptureService.PROXY_PORT);
        final String text = result ? "Set proxy host success !!!" : "Set proxy host failure ~~~";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getWindow().getDecorView(),text,Snackbar.LENGTH_SHORT).show();
            }
        });

        if (result) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CaptureListFragment.TAG);
            if (fragment instanceof CaptureListFragment) {
                ((CaptureListFragment) fragment).onProxyStarted(mBinder);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        System.exit(0);
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
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "需要允许读写SD卡的权限！", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("去设置", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:"+getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                        snackbar.show();
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
        if (getSupportFragmentManager().findFragmentByTag(HelpFragment.TAG) != null
                && getSupportFragmentManager().popBackStackImmediate()){
            return;
        }
        if (mBinder == null || !mBinder.isProxyStarted()) {
            super.onBackPressed();
        } else {
            if (!moveTaskToBack(false)) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(CaptureListFragment.TAG);
            if (fragment != null) {
                CaptureListFragment listFragment = (CaptureListFragment) fragment;
                listFragment.onDispatchTouchEvent(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
