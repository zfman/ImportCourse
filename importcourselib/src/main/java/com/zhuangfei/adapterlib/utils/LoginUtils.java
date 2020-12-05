package com.zhuangfei.adapterlib.utils;

import android.content.Context;
import android.content.Intent;

import com.zhuangfei.adapterlib.activity.TinyAuthActivity;
import com.zhuangfei.adapterlib.callback.ILoginFinishListener;
import com.zhuangfei.adapterlib.station.TinyUserManager;

import java.io.Serializable;

public class LoginUtils {
    public static void checkLoginStatus(Context context, ILoginFinishListener listener){
        if(TinyUserManager.get(context).isLogin()){
            if(listener!=null){
                listener.onLoginSuccess(context);
            }
            return;
        }
        Intent intent = new Intent(context, TinyAuthActivity.class);
//        intent.putExtra("loginFinishListener", (Serializable) listener);
        context.startActivity(intent);
    }
}
