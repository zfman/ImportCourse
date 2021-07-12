package com.zhuangfei.adapterlib.utils;

import android.content.Context;
import android.content.Intent;

import com.zhuangfei.adapterlib.activity.TinyAuthActivity;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.callback.ILoginFinishListener;
import com.zhuangfei.adapterlib.station.TinyUserManager;
import com.zhuangfei.adapterlib.station.model.TinyUserInfo;
import com.zhuangfei.toolkit.tools.ToastTools;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public static void checkLoginStatusByOpenid(final Context context, String openid, final ILoginFinishListener listener){
        TinyUserInfo curInfo = TinyUserManager.get(context).getUserInfo();
        if(curInfo!=null && openid.equals(curInfo.getOpenid())){
            if(listener!=null){
                listener.onLoginSuccess(context);
            }
            return;
        }
        TimetableRequest.loginUser(context, "4", openid, new Callback<ObjResult<TinyUserInfo>>(){

            @Override
            public void onResponse(Call<ObjResult<TinyUserInfo>> call, Response<ObjResult<TinyUserInfo>> response) {
                ObjResult<TinyUserInfo> body = response.body();
                if(body!=null){
                    TinyUserInfo info = body.getData();
                    if(info!=null){
                        TinyUserManager.get(context).saveUserInfo(info);
                        if(listener!=null){
                            listener.onLoginSuccess(context);
                        }
                    }else{
                        ToastTools.show(context,body.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<ObjResult<TinyUserInfo>> call, Throwable t) {
                ToastTools.show(context,"fail when login user");
            }
        });
    }
}
