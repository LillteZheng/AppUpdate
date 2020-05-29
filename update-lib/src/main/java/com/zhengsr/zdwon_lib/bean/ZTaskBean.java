package com.zhengsr.zdwon_lib.bean;

import android.content.Context;

import com.zhengsr.zdwon_lib.callback.BaseListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @auther by zhengshaorui on 2020/3/21
 * describe:
 */
public class ZTaskBean {
    /**
     * context
     */
    public Context context;
    /**
     * url
     */
    public String url;

    /**
     * 线程个数，默认单线程,最多8个线程，线程数量不是越多越好，一般3-5个最佳
     */
    public int threadCount = 1;

    /**
     * UI刷新时间，默认1s
     */
    public int reFreshTime = 1000;

    /**
     * 文件下载路径，默认内部存储
     */
    public String filePath;

    /**
     * 文件名称，默认根据 url 后缀来
     */
    public String fileName;

    /**
     * post 的参数
     */
    public Map<String,String> paramsMap = new LinkedHashMap<>();
    /**
     * 是否允许后台更新
     */
    public boolean allowBackDownload = false;

    /**
     * 是否允许断点续传，即数据保存
     */
    public boolean useBreakPoint;

    /**
     * 文件长度，当json本身就给了文件大小，可通过该选项去做一些判断和分割下载
     */
    public long fileLength = -1;

    /**
     * 监听回调
     */
    public BaseListener listener;

    /**
     * 是否为 get 请求，默认为true
     */
    public boolean isGet = true;

    @Override
    public String toString() {
        return "ZTaskBean{" +
                "context=" + context +
                ", url='" + url + '\'' +
                ", threadCount=" + threadCount +
                ", reFreshTime=" + reFreshTime +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", paramsMap=" + paramsMap +
                ", allowBackDownload=" + allowBackDownload +
                ", useBreakPoint=" + useBreakPoint +
                ", fileLength=" + fileLength +
                ", listener=" + listener +
                ", isGet=" + isGet +
                '}';
    }
}
