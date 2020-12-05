package com.zhuangfei.adapterlib.callback;

import android.content.Context;

import java.io.Serializable;

public interface ILoginFinishListener extends Serializable {
    void onLoginSuccess(Context context);
}
