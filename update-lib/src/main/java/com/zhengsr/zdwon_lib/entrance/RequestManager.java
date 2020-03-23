package com.zhengsr.zdwon_lib.entrance;

import android.content.Context;

import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.callback.BaseListener;
import com.zhengsr.zdwon_lib.entrance.imp.task.ZCheckTask;
import com.zhengsr.zdwon_lib.entrance.imp.task.ZDownTask;

import java.util.Map;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe:
 */
public class RequestManager {
    private ZTaskBean mBean;

    public RequestManager() {
        mBean = new ZTaskBean();
    }

    public RequestManager with(Context context){
        mBean.context = context;
        return this;
    }

    public RequestManager url(String url){
        mBean.url = url;
        return this;
    }

    public RequestManager filePath(String filePath) {
        mBean.filePath = filePath;
        return this;
    }
    public RequestManager fileName(String fileName) {
        mBean.fileName = fileName;
        return this;
    }

    public RequestManager threadCount(int  threadCount) {
        mBean.threadCount = threadCount;
        return this;
    }

    public RequestManager fileLength(long fileLength) {
        mBean.fileLength = fileLength;
        return this;
    }

    public RequestManager useBreakPoint(boolean useBreakPoint){
        mBean.useBreakPoint = useBreakPoint;
        return this;
    }


    public RequestManager reFreshTime(int reFreshTime) {
        mBean.reFreshTime = reFreshTime;
        return this;
    }

    public RequestManager listener(BaseListener listener){
        mBean.listener = listener;
        return this;
    }

    public RequestManager allowBackDownload(boolean allowBackDownload) {
        mBean.allowBackDownload = allowBackDownload;
        return this;
    }


    public RequestManager paramsMap(Map<String,String> map){
        mBean.paramsMap.clear();
        mBean.paramsMap.putAll(map);
        return this;
    }

    public RequestManager params(String key,String value){
        mBean.paramsMap.put(key,value);
        return this;
    }


    /**
     * 检查版本
     */
    public void check(){
        mBean = new CheckParams().checkJsonUrl(mBean);
        new ZCheckTask(mBean);
    }

    /**
     * 下载
     */
    public void down(){
        mBean = new CheckParams().check(mBean);
        new ZDownTask(mBean);
    }
}
