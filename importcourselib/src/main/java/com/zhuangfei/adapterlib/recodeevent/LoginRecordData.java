package com.zhuangfei.adapterlib.recodeevent;

/**
 * Created by Liu ZhuangFei on 2019/9/27.
 */
public class

LoginRecordData {
    protected String version;
    protected String deviceId;
    protected String packageName;
    protected String sign;
    protected String localTime;
    protected String sign2;

    public String getVersion() {
        if(version==null) return "";
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceId() {
        if(deviceId==null) return "";
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPackageName() {
        if(packageName==null) return "";
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getLocalTime() {
        if(localTime==null) return "";
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getSign2() {
        return sign2;
    }

    public void setSign2(String sign2) {
        this.sign2 = sign2;
    }
}
