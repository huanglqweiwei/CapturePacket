package com.hlq.capture.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hlq.capture.R;
import com.hlq.capture.fragment.SaveHarTask;
import com.hlq.capture.util.SPUtil;

import net.lightbody.bmp.core.har.HarEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hlq on 2019/1/12 0012.
 */

public class SaveLogDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "SaveLogDialogFragment";
    private HarEntry mHarEntry;
    private EditText mEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_save_log, null);
        mEditText = view.findViewById(R.id.et_file_name);
        if (mEditText != null && mHarEntry != null) {
            Date time = mHarEntry.getStartedDateTime();
            if (time != null) {
                String format = new SimpleDateFormat("yyyyMMdd'T'HH-mm-ss-SSS", Locale.CHINA).format(time);
                mEditText.setText("har_"+format+".txt");
                mEditText.setSelection(0,format.length()+4);
            }
        }
        return new AlertDialog.Builder(getActivity(), getTheme())
                .setTitle("保存文件")
                .setView(view)
                .setPositiveButton("确定", this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (mHarEntry != null && mEditText != null) {
                String fileName = mEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(fileName)) {
                    File logsFile = new File(SPUtil.getCaptureDir(), "logs");
                    if (!logsFile.exists() || !logsFile.isDirectory()) {
                        logsFile.mkdir();
                    }
                    new SaveHarTask(getParentFragment(),new File(logsFile,fileName)).execute(mHarEntry);
                }
            }
        }
    }

    public void setHarEntry(HarEntry harEntry) {
        mHarEntry = harEntry;
    }
}
