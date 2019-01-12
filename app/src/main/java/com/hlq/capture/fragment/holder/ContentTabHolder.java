package com.hlq.capture.fragment.holder;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hlq.capture.fragment.EntryTabDelegate;
import com.hlq.capture.util.ViewUtil;

import net.lightbody.bmp.core.har.HarContent;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarPostData;
import net.lightbody.bmp.core.har.HarPostDataParam;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.core.har.INameValue;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlq on 2019/1/6 0006.
 */
public class ContentTabHolder implements TabHolder{

    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    public View getView(Context context) {
        if (mScrollView == null) {
            mTextView = ViewUtil.getContentTextView(context);
            mScrollView = new ScrollView(context);
            mScrollView.addView(mTextView);
        }
        return mScrollView;
    }

    @Override
    public void onBindHolder(HarEntry harEntry,String tabText) {
        mTextView.setTag("");
        if ( EntryTabDelegate.TAB_OVERVIEW.equals(tabText)){
             ViewUtil.setNameValueSpan(mTextView,getOverViewList(harEntry));
        }else if ( EntryTabDelegate.TAB_COOKIES.equals(tabText)){
            HarRequest request = harEntry.getRequest();
            if (request != null) {
                ViewUtil.setNameValueSpan(mTextView, request.getCookies());
            } else {
                mTextView.setText("");
            }
        }else if ( EntryTabDelegate.TAB_QUERY.equals(tabText)){
            HarRequest request = harEntry.getRequest();
            if (request != null) {
                ViewUtil.setNameValueSpan(mTextView, request.getQueryString());
            }else {
                mTextView.setText("");
            }
        }else if ( EntryTabDelegate.TAB_PARAMS.equals(tabText)){
            HarRequest harRequest = harEntry.getRequest();
            if (harRequest != null) {
                HarPostData postData = harRequest.getPostData();
                if (postData != null) {
                    List<HarPostDataParam> nameValues = postData.getParams();
                    if (nameValues == null || nameValues.size() == 0) {
                        mTextView.setText(postData.getText());
                    } else {
                        ViewUtil.setNameValueSpan(mTextView, nameValues);
                    }
                } else {
                    mTextView.setText("");
                }
            } else {
                mTextView.setText("");
            }
        }else if ( EntryTabDelegate.TAB_CONTENT.equals(tabText)){
            HarResponse response = harEntry.getResponse();
            String text = null;
            if (response != null) {
                HarContent content = response.getContent();
                if (content != null) {
                    text = content.getText();
                }
            }
            if (text != null) {
                mTextView.setTag(text);
                new JsonFormatTask(mTextView).execute(text);
            } else {
                mTextView.setText("");
            }
        } else {
            mTextView.setText("");
        }
    }

    private List<? extends INameValue> getOverViewList(HarEntry harEntry) {
        ArrayList<HarNameValuePair> pairs = new ArrayList<>();
        HarRequest harRequest = harEntry.getRequest();
        HarResponse harResponse = harEntry.getResponse();
        if (harRequest != null) {
            String url = harRequest.getUrl();
            try {
                url = URLDecoder.decode(url, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            pairs.add(new HarNameValuePair("URL", url));

            pairs.add(new HarNameValuePair("Method", harRequest.getMethod()));
        }
        if (harResponse != null) {
            pairs.add(new HarNameValuePair("Code", harResponse.getStatus() + ""));
            pairs.add(new HarNameValuePair("Size", harResponse.getBodySize() + "Bytes"));
        }
        pairs.add(new HarNameValuePair("TotalTime", harEntry.getTime() + "ms"));
        return pairs;
    }

    private static class JsonFormatTask extends AsyncTask<String,Void,String>{

        private WeakReference<TextView> mTextViewRef;
        private Gson mGson;
        private String src;

        JsonFormatTask(TextView textView){
            mTextViewRef = new WeakReference<>(textView);
        }

        @Override
        protected void onPreExecute() {
            TextView textView = mTextViewRef.get();
            if (textView != null) {
                textView.setText("(请稍后...)");
            }
        }

        @Override
        protected String doInBackground(String... text) {
            String s = src = text[0];
            try {
               return jsonFormatter(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView textView = mTextViewRef.get();
            if (textView != null && textView.getTag() == src) {
                textView.setText(s);
            }
        }

        public String jsonFormatter(String src) throws Exception {
            if (mGson == null) {
                mGson = new GsonBuilder().setPrettyPrinting().create();
            }
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(src);
            return mGson.toJson(je);
        }
    }
}
