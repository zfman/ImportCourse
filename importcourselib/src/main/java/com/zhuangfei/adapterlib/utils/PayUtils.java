package com.zhuangfei.adapterlib.utils;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.apis.model.WxPayResult;
import com.zhuangfei.adapterlib.thirdlogin.ThirdLoginContants;
import com.zhuangfei.adapterlib.uikit.NetworkUtil;
import com.zhuangfei.toolkit.tools.ToastTools;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayUtils {

    private static IWXAPI api;

    public static void pay(final Context context,String goodName){
        if(api==null){
            api= WXAPIFactory.createWXAPI(context, ThirdLoginContants.APPID_WECHAT);
        }
        ToastTools.show(context,"请求中，请稍后。。");
        TimetableRequest.getWxPayOrder(context, goodName, new Callback<ObjResult<WxPayResult>>() {
            @Override
            public void onResponse(Call<ObjResult<WxPayResult>> call, Response<ObjResult<WxPayResult>> response) {
                ObjResult<WxPayResult> objResult=response.body();
                if(objResult!=null){
                    if(objResult.getCode()==200){
                        WxPayResult payResult=objResult.getData();
                        if(payResult!=null){
                            if(payResult.isSuccess()){
                                api.sendReq(getPayReq(payResult,context));
                            }
                        }
                    }else{
                        ToastTools.show(context,"Error:"+objResult.getMsg());
                    }
                }else{
                    ToastTools.show(context,"req ret is null");
                }
            }

            @Override
            public void onFailure(Call<ObjResult<WxPayResult>> call, Throwable t) {
                ToastTools.show(context,"Exception:"+t.getMessage());
            }
        });
    }

    private static PayReq getPayReq(WxPayResult payResult,Context context){
        PayReq req = new PayReq();
        req.appId			= payResult.getAppid();
        req.partnerId		= payResult.getMch_id();
        req.prepayId		= payResult.getPrepay_id();
        req.nonceStr		= payResult.getNonce_str();
        req.timeStamp		= ""+System.currentTimeMillis();
        req.packageValue	= "Sign=WXPay";
        req.extData			= "ext"; // optional

        StringBuilder sb=new StringBuilder();
        sb.append("appid=").append(payResult.getAppid())
                .append("&noncestr=").append(req.nonceStr)
                .append("&package=").append(req.packageValue)
                .append("&partnerid=").append(req.partnerId)
                .append("&prepayid=").append(req.prepayId)
                .append("&timestamp=").append(req.timeStamp)
                .append("&key=9b2c8a1e0559f754cc4c857422064b5a");
        String sign=Md5Security.encrypBy(sb.toString()).toUpperCase();
        req.sign=sign;

//        AlertDialog.Builder builder=new AlertDialog.Builder(context)
//                .setMessage(""+sb.toString()+"\n\nsign="+sign)
//                .setPositiveButton("ok",null);
//        builder.create().show();
        return req;
    }
}
