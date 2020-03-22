package com.zhengsr.zdwon_lib.bean;


/**
 * Created by zhengshaorui
 * Time on 2018/9/12
 */

public class ZThreadBean {
    public int threadId;
    public String url;
    public String name;
    public long startPos;
    public long endPos;
    public long threadLength = 0; //单个线程文件长度


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
