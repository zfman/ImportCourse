package com.zhuangfei.adapterlib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.zhuangfei.adapterlib.activity.AdapterSameTypeActivity;
import com.zhuangfei.adapterlib.activity.AdapterSchoolActivity;
import com.zhuangfei.adapterlib.activity.OnDoActionListener;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.apis.model.ParseJsModel;
import com.zhuangfei.adapterlib.apis.model.School;
import com.zhuangfei.adapterlib.apis.model.TemplateJsV2;
import com.zhuangfei.adapterlib.apis.model.TemplateModel;
import com.zhuangfei.smartalert.core.LoadAlert;
import com.zhuangfei.toolkit.tools.ToastTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JumpCmdUtils {

    public static LoadAlert loadAlert;

    public static void showLoadAlert(Context context,String message){
        loadAlert = new LoadAlert(context);
        loadAlert.setMessage(message).create().show();
    }

    public static void hideLoadAlert(Context context){
        if(loadAlert!=null){
            loadAlert.hide();
        }
    }

    public static void jumpJwImportPage(final Context context, final School school, final boolean openScan){
        if(school==null){
            return;
        }
        showLoadAlert(context,"加载中..");
        TimetableRequest.getAdapterParseJs(context,school.getAid(), new Callback<ObjResult<ParseJsModel>>() {
            @Override
            public void onResponse(Call<ObjResult<ParseJsModel>> call, Response<ObjResult<ParseJsModel>> response) {
                hideLoadAlert(context);
                ObjResult<ParseJsModel> objResult=response.body();
                if(objResult!=null){
                    if (objResult.getCode() == 200) {
                        ParseJsModel parseJsModel=objResult.getData();
                        if(parseJsModel!=null){
                            if(!TextUtils.isEmpty(parseJsModel.getUrl())){
                                school.setUrl(parseJsModel.getUrl());
                                SchoolDaoUtils.saveSchool(context,school);
                            }
                            if(parseJsModel.getEnable()==0){
                                ToastTools.show(context,"该学校解析已被管理员临时关闭，待修复！");
                                return;
                            }
                            realHandleItemClickedForSchool(context,school,parseJsModel,openScan);
                        }
                    } else {
                        Toast.makeText(context, objResult.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "templatejs response is null!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjResult<ParseJsModel>> call, Throwable t) {
                hideLoadAlert(context);
                Toast.makeText(context,"Fail:请求失败，请检查网络！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void realHandleItemClickedForSchool(Context context,School school,ParseJsModel jsModel,boolean openScan){
        if(school!=null){
            Map<String,String> params=new HashMap<>();
            params.put("school",school.getSchoolName());
            Intent intent=new Intent(context, AdapterSchoolActivity.class);
            intent.putExtra(AdapterSchoolActivity.EXTRA_URL,school.getUrl());
            intent.putExtra(AdapterSchoolActivity.EXTRA_SCHOOL,school.getSchoolName());
            intent.putExtra(AdapterSchoolActivity.EXTRA_TYPE,school.getType());
            intent.putExtra(AdapterSchoolActivity.EXTRA_PARSEJS,jsModel.getParsejs());
            intent.putExtra(AdapterSchoolActivity.EXTRA_OPEN_SCAN,openScan);
            context.startActivity(intent);
        }
    }

    public static void jumpCommonImportPage(final Activity context){
        if(!checkTemplateJs()){
            requestTemplateJs(context,new OnDoActionListener() {
                @Override
                public void doActionAfter() {
                    handleCommonParse(context,templateModels);
                }
            });
        }else{
            handleCommonParse(context,templateModels);
        }
    }

    private static boolean checkTemplateJs(){
        if(baseJs==null || templateModels==null || templateModels.size()==0){
            return false;
        }
        return true;
    }

    private static List<TemplateModel> templateModels;
    private static String baseJs;

    public static void clearCache(){
        templateModels = null;
        baseJs = null;
    }

    private static void requestTemplateJs(final Context context, final OnDoActionListener doActionListener){
        showLoadAlert(context,"加载中..");
        TimetableRequest.getTemplateJs(context, new Callback<ObjResult<TemplateJsV2>>() {
            @Override
            public void onResponse(Call<ObjResult<TemplateJsV2>> call, Response<ObjResult<TemplateJsV2>> response) {
                hideLoadAlert(context);
                ObjResult<TemplateJsV2> objResult=response.body();
                if(objResult!=null){
                    if (objResult.getCode() == 200) {
                        TemplateJsV2 templateJsV2=objResult.getData();
                        if(templateJsV2!=null){
                            baseJs=templateJsV2.getBase();
                            templateModels=templateJsV2.getTemplate();
                        }
                        if(doActionListener!=null){
                            doActionListener.doActionAfter();
                        }
                    } else {
                        Toast.makeText(context, objResult.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "templatejs response is null!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjResult<TemplateJsV2>> call, Throwable t) {
                hideLoadAlert(context);
                Toast.makeText(context,"Fail:"+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void handleCommonParse(final Activity context, final List<TemplateModel> templateModels){
        if(templateModels!=null){
            String[] items=new String[templateModels.size()];
            for(int r=0;r<items.length;r++){
                items[r]=templateModels.get(r).getTemplateName();
            }
            SoftInputUtils.hideInput(context);
            AlertDialog.Builder builder=new AlertDialog.Builder(context)
                    .setTitle("选择通用模板")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TemplateModel templateModel = (TemplateModel) templateModels.get(i);
                            if (templateModel!=null){
                                handleCustomTemplate(context,templateModel);
                            }
                        }
                    });
            builder.create().show();
        }
    }

    private static void handleCustomTemplate(Context context,TemplateModel templateModel){
        if(baseJs==null){
            Toast.makeText(context,"基础函数库发生异常，请联系qq:1193600556",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent=new Intent(context, AdapterSameTypeActivity.class);
            intent.putExtra(AdapterSameTypeActivity.EXTRA_TYPE,templateModel.getTemplateName());
            intent.putExtra(AdapterSameTypeActivity.EXTRA_JS,baseJs+templateModel.getTemplateJs());
            context.startActivity(intent);
        }
    }
}
