package com.hlq.capture.fragment.headers;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hlq.capture.R;

import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.INameValue;

import java.util.List;

/**
 * Created by hlq on 2019/1/6 0006.
 */

class HeaderExpandAdapter extends BaseExpandableListAdapter {

    private List<HarNameValuePair> mRequestHeader;
    private List<HarNameValuePair> mResponseHeader;

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0) {
            return mRequestHeader == null ? 0 : mRequestHeader.size();
        } else if (groupPosition == 1){
            return mResponseHeader == null ? 0 : mResponseHeader.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public INameValue getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0) {
           return mRequestHeader.get(childPosition);
        } else if (groupPosition == 1){
            return mResponseHeader.get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TextView textView = new TextView(parent.getContext());
            int dp_5 = parent.getResources().getDimensionPixelSize(R.dimen.dp_5);
            textView.setPadding(dp_5,dp_5,dp_5,dp_5);
            textView.setTextSize(14);
            textView.setTextColor(Color.DKGRAY);
            textView.getPaint().setFakeBoldText(true);
            textView.setGravity(Gravity.LEFT);
            textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.LTGRAY),new ColorDrawable(0xffdedede),null));
            }else {
                textView.setBackgroundColor(0xffdedede);
            }
            convertView  = textView;
        }
        TextView textView = (TextView) convertView;
        if (groupPosition == 0) {
            textView.setText("Request Header");
        }else if (groupPosition == 1){
            textView.setText("Response Header");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TextView textView = new TextView(parent.getContext());
            int dp_5 = parent.getResources().getDimensionPixelSize(R.dimen.dp_5);
            textView.setPadding(dp_5,0,dp_5,0);
            textView.setTextSize(13);
            textView.setTextColor(0xff323232);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            convertView = textView;
        }
        TextView textView = (TextView) convertView;
        onBindViewHolder(getChild(groupPosition,childPosition),textView);
        return convertView;
    }

    public void onBindViewHolder(INameValue nameValue ,TextView textView) {
        String name = nameValue.getName();
        SpannableString spanStr = new SpannableString(name + ":" + nameValue.getValue());
        spanStr.setSpan(new ForegroundColorSpan(0xffFF4081),0, name.length()+1 , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spanStr);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setHeaders(List<HarNameValuePair> requestHeader,List<HarNameValuePair> responseHeader) {
        mRequestHeader = requestHeader;
        mResponseHeader = responseHeader;
        notifyDataSetChanged();

    }


}