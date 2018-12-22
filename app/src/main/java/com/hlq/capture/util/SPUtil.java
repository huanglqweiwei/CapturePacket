package com.hlq.capture.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static final String SP_NAME_CAPTURE = "sp_capture";
    public static final String KEY_IS_INSTALL_CER = "is_install_cer";

    public static boolean getBoolean(Context context,String key,boolean defaultVal){
        SharedPreferences sharedPref = context.getSharedPreferences(SP_NAME_CAPTURE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key,defaultVal);
    }

    public static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sharedPref = context.getSharedPreferences(SP_NAME_CAPTURE, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(key, value).apply();
    }
}
