package com.zhengsr.zdwon_lib.entrance;

import android.content.Context;
import android.util.Log;

import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.callback.CheckListener;
import com.zhengsr.zdwon_lib.entrance.imp.task.ZCheckTask;

import java.util.Map;

/**
 * @author by  zhengshaorui on 2019/9/6
 * Describe:
 */
public class CheckRequest {
    private static final String TAG = "CheckRequest";
    private ZTaskBean mBean;

    public static CheckRequest get(Context context) {

        return new CheckRequest(context);
    }

    private CheckRequest(Context context) {
        mBean = new ZTaskBean();
        mBean.context = context.getApplicationContext();
    }

    public CheckRequest url(String url) {
        mBean.url = url;
        return this;
    }

    public CheckRequest listener(CheckListener listener) {
        mBean.listener = listener;
        return this;
    }

    public CheckRequest paramsMap(Map<String, String> map) {
        mBean.paramsMap.clear();
        mBean.paramsMap.putAll(map);
        return this;
    }

    public CheckRequest params(String key, String value) {
        mBean.paramsMap.put(key, value);
        return this;
    }


    public CheckRequest get() {
        mBean.isGet = true;
        return this;
    }


    public CheckRequest post() {
        mBean.isGet = false;
        return this;
    }

    public void check(){
        mBean = new CheckParams().checkJsonUrl(mBean);
        new ZCheckTask(mBean);
    }
}
