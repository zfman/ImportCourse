package com.zhuangfei.adapterlib.utils;

import android.content.Context;
import android.text.TextUtils;

import com.zhuangfei.adapterlib.apis.model.School;
import com.zhuangfei.toolkit.tools.ShareTools;

public class SchoolDaoUtils {
    public static void saveSchool(Context context,School school){
        if(school!=null){
            ShareTools.putString(context,"cur_school_value",GsonUtils.getGson().toJson(school));
        }
    }

    public static void clear(Context context){
        ShareTools.putString(context,"cur_school_value",null);
    }

    public static School getSchool(Context context){
        String schoolValue = ShareTools.getString(context,"cur_school_value",null);
        if(!TextUtils.isEmpty(schoolValue)){
            School school = GsonUtils.getGson().fromJson(schoolValue,School.class);
            return school;
        }
        return null;
    }

}
