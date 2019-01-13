package com.hlq.capture.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.hlq.capture.HLog;

import net.lightbody.bmp.core.har.HarEntry;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by hlq on 2019/1/12 0012.
 */

public class SaveHarTask extends AsyncTask<HarEntry,Void,File> {

    private final WeakReference<Fragment> mFragmentRef;
    private File mOutFile;
    private ProgressDialog mPD;

    public SaveHarTask(Fragment fragment, File outFile){
        mFragmentRef = new WeakReference<>(fragment);
        mOutFile = outFile;
    }

    @Override
    protected void onPreExecute() {
        Fragment fragment = mFragmentRef.get();
        if (fragment != null && fragment.isAdded()) {
            mPD = new ProgressDialog(fragment.getActivity());
            mPD.setCancelable(false);
            mPD.setMessage("请稍后...");
            mPD.show();
        }
    }

    @Override
    protected File doInBackground(HarEntry... harEntries) {
        try {
            harEntries[0].writeTo(mOutFile);
            return mOutFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final File file) {
        if (mPD != null) {
            mPD.dismiss();
            mPD = null;
        }
        Fragment fragment = mFragmentRef.get();
        if (fragment != null && fragment.isAdded() &&fragment.getView() !=null) {
            Snackbar snackbar;
            if (file != null) {
                snackbar = Snackbar.make(fragment.getView(), "文件保存成功！路径：/sdcard/capture/logs/"+file.getName(), Snackbar.LENGTH_LONG);
                snackbar.setAction("去分享", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Context context = v.getContext();
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
                            } else {
                                uri = Uri.fromFile(file);
                            }
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                            intent.setType("text/plain");
                            context.startActivity(Intent.createChooser(intent,file.getName()));
                        } catch (Exception e) {
                            HLog.e(e);
                        }
                    }
                });
            } else {
                snackbar = Snackbar.make(fragment.getView(), "文件保存失败！", Snackbar.LENGTH_SHORT);
            }
            snackbar.show();
        }

    }
}
