package com.hlq.capture.fragment.nv;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hlq.capture.R;

import net.lightbody.bmp.core.har.INameValue;

/**
 * Created by hlq on 2019/1/5 0005.
 */

public class SpanViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTextView;

    public SpanViewHolder(ViewGroup parent) {
        super(getView(parent));
        mTextView = (TextView) itemView;
    }

    private static View getView(ViewGroup parent){
        TextView textView = new TextView(parent.getContext());
        int dp_5 = parent.getResources().getDimensionPixelSize(R.dimen.dp_5);
        textView.setPadding(dp_5,0,dp_5,0);
        textView.setTextSize(13);
        textView.setTextColor(0xff323232);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    public void onBindViewHolder(INameValue nameValue) {
        String name = nameValue.getName();
        SpannableString spanStr = new SpannableString(name + ": " + nameValue.getValue());
        spanStr.setSpan(new ForegroundColorSpan(0xffFF4081),0, name.length()+1 , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mTextView.setText(spanStr);
    }
}
