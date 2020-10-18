package com.zhuangfei.adapterlib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.zhuangfei.adapterlib.apis.model.ValuePair;
import com.zhuangfei.adapterlib.callback.OnValueCallback;
import com.zhuangfei.adapterlib.callback.OnVersionFindCallback;

import java.util.HashMap;

/**
 * Created by Liu ZhuangFei on 2018/10/19.
 */
public class AdapterLibManager {
    private static final String TAG = "AdapterLibManager";

    //核心库版本号
    private static int libVersionNumber=21;
    private static String libVersionName="lib-2.1.0";

    public static int appVersionNumber=1;

    //包名和appkey
    private static String appKey;
    private static String stat="default";

    public static String getLibVersionName() {
        return libVersionName;
    }

    public static String getAppKey() {
        return appKey;
    }

    public static int getLibVersionNumber() {
        return libVersionNumber;
    }

    public static String getStat() {
        return stat;
    }

    public static void register(String appkey, String stat){
        AdapterLibManager.appKey=appkey;
        AdapterLibManager.stat=stat;

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }

    public static void checkUpdate(final Context context,String updateId, final OnVersionFindCallback callback){
        if(context==null) return;
        ShareManager.getValue(context, updateId, new OnValueCallback() {
            @Override
            public void onSuccess(ValuePair pair) {
                handleUpdateResult(context,pair,callback);
            }
        });
    }

    private static void handleUpdateResult(final Context context, ValuePair pair,final OnVersionFindCallback callback){
        if(context==null) return;
        String value = pair.getValue();
        try{
            String[] vals = value.split("#");
            if (vals.length >= 3) {
                int v = Integer.parseInt(vals[0]);
                if (v > getLibVersionNumber()) {
                    if(context==null) return;
                    if(callback!=null){
                        callback.onNewVersionFind(v,vals[1],vals[2]);
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, "handleUpdateResult: ",e );
        }
    }
}
