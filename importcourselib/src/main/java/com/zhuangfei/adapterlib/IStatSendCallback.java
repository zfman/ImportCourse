package com.zhuangfei.adapterlib;

import android.content.Context;

import java.util.Map;

public interface IStatSendCallback {
    void sendKVEvent(Context context, String eventId, Map<String,String> params);
}
