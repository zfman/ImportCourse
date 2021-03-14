package com.zhuangfei.adapterlib;

import android.content.Context;
import android.text.TextUtils;

import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.BaseResult;
import com.zhuangfei.adapterlib.utils.GsonUtils;
import com.zhuangfei.toolkit.tools.ToastTools;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Liu ZhuangFei on 2019/9/27.
 */
public class RecordEventManager {
    public static final String TYPE_NORMAL="display";
    public static final String TYPE_CLICK="click";

    private static void recordUserEvent(Context context, String type, String operator, Map<String, String> map) {
        String json = "";
        if(map!=null){
            json = GsonUtils.getGson().toJson(map);
        }
        if(TextUtils.isEmpty(AdapterLibManager.getAppKey())){
            return;
        }
        TimetableRequest.recordUserEvent(context, operator, type, json, AdapterLibManager.getAppKey(), new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {

            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
    }

    private static void recordUserEventForContent(Context context, String type, String operator, String content) {
        String json = content;
        if(TextUtils.isEmpty(AdapterLibManager.getAppKey())){
            return;
        }
        TimetableRequest.recordUserEvent(context, operator, type, json, AdapterLibManager.getAppKey(), new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {

            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
    }

    public static void recordDisplayEvent(Context context, String operator) {
        if(true){
            return;
        }
        recordUserEvent(context,TYPE_NORMAL,operator,null);
    }

    public static void recordDisplayEvent(Context context, String operator, String key,String...values) {
        Map<String,String> map = new HashMap<>();
        if(key==null){
            recordUserEventForContent(context,TYPE_NORMAL,operator,values[0]);
            return;
        }
        String[] keys = key.split(",");
        if(values != null && keys.length == values.length){
            for(int i=0;i<keys.length;i++){
                String[] arr = keys[i].split("=");
                map.put(arr[0],values[i]);
            }
        }
        recordUserEvent(context,TYPE_NORMAL,operator,map);
    }

    public static void recordClickEvent(Context context, String operator) {
        if(true){
            return;
        }
        recordUserEvent(context,TYPE_CLICK,operator,null);
    }

    public static void recordClickEvent(Context context, String operator, String key,String...values) {
        if(true){
            return;
        }
        Map<String,String> map = new HashMap<>();
        String[] keys = key.split(",");
        if(values != null && keys.length == values.length){
            for(int i=0;i<keys.length;i++){
                String[] arr = keys[i].split("=");
                map.put(arr[0],values[i]);
            }
        }
        recordUserEvent(context,TYPE_CLICK,operator,map);
    }
}
