package com.hlq.capture.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hlq.capture.R;

import net.lightbody.bmp.core.har.INameValue;

import java.util.List;

/**
 * Created by hlq on 2019/1/12 0012.
 */

public class ViewUtil {
    private ViewUtil(){}

    public static TextView getContentTextView(Context context){
        TextView textView = new TextView(context);
        int dp_5 = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
        textView.setPadding(dp_5,0,dp_5,0);
        textView.setTextSize(13);
        textView.setTextColor(0xff323232);
        textView.setTextIsSelectable(true);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public static void setNameValueSpan(TextView textView, List<? extends INameValue> nameValues){
        if (nameValues == null || nameValues.isEmpty()) {
            textView.setText("");
        } else {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            SpannableString spanStr;
            String name;
            int size = nameValues.size();
            for (int i = 0; i < size; i++){
                INameValue nameValue = nameValues.get(i);
                name = nameValue.getName();
                spanStr = new SpannableString(name + " : " + nameValue.getValue());
                spanStr.setSpan(new ForegroundColorSpan(0xffFF4081),0, name.length()+2 , Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                ssb.append(spanStr);
                if (i != size - 1) {
                    ssb .append('\n');
                }
            }
            textView.setText(ssb);
        }
    }
}
