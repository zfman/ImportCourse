package com.zhuangfei.adapterlib.apis.model;

/**
 * Created by Liu ZhuangFei on 2018/9/28.
 */
public class ParseJsModel {
    private String parsejs;
    private String url;
    private int enable = 1;
    private boolean needVip = false;

    public void setNeedVip(boolean needVip) {
        this.needVip = needVip;
    }

    public boolean isNeedVip() {
        return needVip;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getEnable() {
        return enable;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getParsejs() {
        return parsejs;
    }

    public void setParsejs(String parsejs) {
        this.parsejs = parsejs;
    }
}
