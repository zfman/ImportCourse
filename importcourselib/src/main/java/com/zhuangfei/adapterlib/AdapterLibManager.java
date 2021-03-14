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
    private static int libVersionNumber=30;
    private static String libVersionName="lib-3.0.0";

    public static int appVersionNumber=64;

    //包名和appkey
    private static String appKey;

    public static String getLibVersionName() {
        return libVersionName;
    }

    public static String getAppKey() {
        return appKey;
    }

    public static int getLibVersionNumber() {
        return libVersionNumber;
    }

    public static void register(String appkey){
        AdapterLibManager.appKey=appkey;

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }
}
