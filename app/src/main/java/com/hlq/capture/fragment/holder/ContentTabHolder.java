package com.hlq.capture.fragment.holder;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hlq.capture.R;

import net.lightbody.bmp.core.har.HarContent;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarResponse;

import java.lang.ref.WeakReference;

/**
 * Created by hlq on 2019/1/6 0006.
 */
//TODO : AsyncTask处理json
public class ContentTabHolder implements TabHolder<HarEntry> {

    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    public View getView(Context context) {
        if (mScrollView == null) {
            mScrollView = new ScrollView(context);
        }
        return mScrollView;
    }

    @Override
    public void onBindHolder(HarEntry harEntry) {
        String text = getText(harEntry);
        if (text != null) {
            if (mTextView == null) {
                mTextView = getTextView();
                mScrollView.addView(mTextView);
            }

            mTextView.setTag(text);
            new JsonFormatTask(mTextView).execute(text);
        } else {
            if (mTextView != null) {
                mTextView.setText("");
            }
        }
    }

    private String getText(HarEntry harEntry) {
        String text = null;
        HarResponse response = harEntry.getResponse();
        if (response != null) {
            HarContent content = response.getContent();
            if (content != null) {
                text = content.getText();
                //mime_type = image/gif
//                HLog.e("TabHolder","mime_type = "+content.getMimeType());
//                HLog.e("TabHolder","text = "+text);
            }
        }
        return text;
    }

    private TextView getTextView(){
        TextView textView = new TextView(mScrollView.getContext());
        int dp_5 = mScrollView.getResources().getDimensionPixelSize(R.dimen.dp_5);
        textView.setPadding(dp_5,0,dp_5,0);
        textView.setTextSize(13);
        textView.setTextColor(0xff323232);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return textView;
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
