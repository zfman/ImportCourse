package com.zhuangfei.adapterlib.recodeevent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liu ZhuangFei on 2019/9/28.
 */
public class MessageRecordData {
    private Map<String,String> recordMap=new HashMap<>();

    public Map<String, String> getRecordMap() {
        return recordMap;
    }

    public MessageRecordData setRecordMap(Map<String, String> recordMap) {
        this.recordMap = recordMap;
        return this;
    }

    public MessageRecordData put(String key,String value){
        recordMap.put(key,value);
        return this;
    }
}
