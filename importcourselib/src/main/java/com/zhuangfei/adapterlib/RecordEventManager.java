package com.zhuangfei.adapterlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.BaseResult;
import com.zhuangfei.adapterlib.utils.GsonUtils;
import com.zhuangfei.toolkit.tools.ShareTools;
import com.zhuangfei.toolkit.tools.ToastTools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Liu ZhuangFei on 2019/9/27.
 */
public class RecordEventManager {
    public static final String TYPE_NORMAL="display";
    public static final String TYPE_CLICK="click";
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    private static void initSharePreference(Context context){
        if(preferences==null){
            preferences=context.getSharedPreferences("share_record_event",Context.MODE_PRIVATE);
            editor=preferences.edit();
        }
    }

    private static void recordUserEvent(Context context, String type, String operator, Map<String, String> map) {
        String json = "";
        if(map!=null){
            json = GsonUtils.getGson().toJson(map);
        }
        if(TextUtils.isEmpty(AdapterLibManager.getAppKey())){
            return;
        }
        recordUserEventForContent(context, type, operator, json);
    }

    private static void recordUserEventForContent(Context context, String type, String operator, String content) {
        initSharePreference(context);
        Set<String> set = preferences.getStringSet("event_keys",new HashSet<String>());
        long time = System.currentTimeMillis();
        set.add(type+"|"+time + "|" + operator+"|"+content);
        editor.putStringSet("event_keys",set);
        editor.commit();
    }

    public static void upload(final Context context){
        if(context==null) return;
        initSharePreference(context);
        HashSet<String> list = (HashSet<String>) preferences.getStringSet("event_keys",new HashSet<String>());
        if(list.size()==0) return;
        StringBuilder builder=new StringBuilder();
        for (String s : list) {
            builder.append(s).append("\r\n");
        }
        if(TextUtils.isEmpty(AdapterLibManager.getAppKey())){
            ToastTools.show(context,"appkey is null");
            return;
        }
        TimetableRequest.recordUserEvent(context, builder.toString(), AdapterLibManager.getAppKey(), new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                BaseResult result = response.body();
                if(result!=null && result.getCode()==200){
                    editor.remove("event_keys");
                    editor.commit();
                }

            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
            }
        });
    }

    public static void recordDisplayEvent(Context context, String operator) {
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
        recordUserEvent(context,TYPE_CLICK,operator,null);
    }

    public static void recordClickEvent(Context context, String operator, String key,String...values) {
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
