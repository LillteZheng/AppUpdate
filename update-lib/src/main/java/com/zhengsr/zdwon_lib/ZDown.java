package com.zhengsr.zdwon_lib;

import android.content.Context;

import com.zhengsr.zdwon_lib.entrance.RequestManager;
import com.zhengsr.zdwon_lib.entrance.imp.db.ZDBManager;

/**
 * @auther by zhengshaorui on 2020/3/21
 * describe: 一个对外的统一接口
 */
public class ZDown {
    private static RequestManager mRequestManager;
    public static RequestManager with(Context context){
        ZDBManager.getInstance().config(context.getApplicationContext());
        mRequestManager = new RequestManager();
        return mRequestManager.with(context);
    }

    public static void pause(){
       if (mRequestManager != null){
           if (mRequestManager.mTask != null) {
               mRequestManager.mTask.pause();
           }
       }
    }
}
