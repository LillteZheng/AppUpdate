package com.zhengsr.zdwon_lib.bean;


/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class ZThreadBean {
    /**
     * 线程id
     */
    public int threadId;
    /**
     * 文件url
     */
    public String url;
    /**
     * 文件名
     */
    public String name;
    /**
     * 文件开始的点
     */
    public long startPos;
    /**
     * 文件结束点
     */
    public long endPos;
    /**
     * 单个线程文件长度
     */
    public long threadLength = 0;


    @Override
    public String toString() {
        return "ZThreadBean{" +
                "threadId=" + threadId +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", threadLength=" + threadLength +
                '}';
    }
}
