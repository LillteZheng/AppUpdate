package com.zhengsr.zdwon_lib;

import android.content.Context;

import com.zhengsr.zdwon_lib.callback.BaseListener;
import com.zhengsr.zdwon_lib.entrance.RequestManager;
import com.zhengsr.zdwon_lib.entrance.CheckRequest;
import com.zhengsr.zdwon_lib.entrance.imp.db.ZDBManager;

/**
 * @auther by zhengshaorui on 2020/3/21
 * describe: 一个对外的统一接口
 */
public class ZDown {

    public static CheckRequest checkWith(Context context){
        return CheckRequest.get(context);
    }

    public static RequestManager with(Context context){
        ZDBManager.getInstance().config(context.getApplicationContext());
        return RequestManager.getInstance().with(context);
    }

    /**
     * 暂停
     */
    public static void pause(){
        if (RequestManager.getInstance().mTask != null) {
            RequestManager.getInstance().mTask.pause();
        }
    }

    /**
     * 开始
     */
    public static void start(){
        if (RequestManager.getInstance().mTask != null) {
            RequestManager.getInstance().mTask.start();
        }
    }

    /**
     * 暂停任务，虽然已经监听activity的生命周期了，但是有些特殊情况还需要用户自己去判断
     */
    public static void stopTask(){
        if (RequestManager.getInstance().mTask != null) {
            RequestManager.getInstance().mTask.pause();
            RequestManager.getInstance().mTask = null;
        }
    }

    /**
     * 暂停任务，和删除缓存
     */
    public static void stopTaskAndDeleteCache() {
        if (RequestManager.getInstance().mTask != null) {
            RequestManager.getInstance().mTask.pause();
            RequestManager.getInstance().mTask.deleteCache();
            RequestManager.getInstance().mTask = null;
        }

    }

    /**
     * 是否存在
     * @return
     */
    public static boolean isTaskExists(){
        return RequestManager.getInstance().mTask != null;
    }

    /**
     * 是否正在下载
     * @return
     */
    public static boolean isRunning(){
        if (RequestManager.getInstance().mTask != null) {
            return RequestManager.getInstance().mTask.isRunning();
        }
        return false;
    }

    /**
     * 当存在时，可以直接跟新接口就行了
     * @param listener
     */
    public static void updateListener(BaseListener listener){
        if (RequestManager.getInstance().mTask != null) {
            RequestManager.getInstance().mTask.updateListener(listener);
        }
    }

    /**
     * 当存在错误下载不了，尝试把缓存文件和数据库删除了使用
     */
    public static void deleteCacheAndStart(){
        stopTaskAndDeleteCache();
        start();
    }
}
