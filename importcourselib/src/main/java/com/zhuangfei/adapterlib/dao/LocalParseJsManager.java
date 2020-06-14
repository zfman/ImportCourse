package com.zhuangfei.adapterlib.dao;

import android.content.Context;

public class LocalParseJsManager {

    public Context context;

    public LocalParseJsManager(Context context){
        this.context=context;
    }

    public boolean hasLocalParseJs(int adapterId){
        return false;
    }

    public String getLocalParseJs(int adapterId){
        return null;
    }


}
