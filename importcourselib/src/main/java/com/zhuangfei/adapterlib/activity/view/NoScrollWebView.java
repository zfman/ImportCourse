package com.zhuangfei.adapterlib.activity.view;

import android.content.Context;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebView;

public class NoScrollWebView extends WebView {
    public NoScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public NoScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NoScrollWebView(Context context) {
        super(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
