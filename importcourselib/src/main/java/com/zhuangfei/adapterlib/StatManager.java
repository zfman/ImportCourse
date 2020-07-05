package com.zhuangfei.adapterlib;

import android.content.Context;

import java.util.Map;

public class StatManager {

    private static IStatSendCallback statSendCallback;

    public static void register(IStatSendCallback callback){
        StatManager.statSendCallback=callback;
    }

    public static void sendKVEvent(Context context, String eventId, Map<String,String> params){
        if(statSendCallback!=null){
            statSendCallback.sendKVEvent(context,eventId,params);
        }
    }
}
