package com.zhengsr.zdwon_lib;

import android.content.Context;

import com.zhengsr.zdwon_lib.entrance.RequestManager;

/**
 * @auther by zhengshaorui on 2020/3/21
 * describe: 一个对外的统一接口
 */
public class ZDown {
    public static RequestManager with(Context context){
        return new RequestManager().with(context);
    }
}
