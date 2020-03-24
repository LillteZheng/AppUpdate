package com.zhengsr.zdwon_lib.bean;

/**
 * @author by  zhengshaorui on 2019/9/6
 * Describe: 给远不调用的Bean
 */
public class ZBean {
    /**
     * 总长度
     */
    public long totalLength;
    /**
     * 当前下载的长度
     */
    public long curLength;
    /**
     * 下载速度
     */
    public String speed;

    /**
     * 当前进度百分比
     */
    public float progress;

    @Override
    public String toString() {
        return "ZBean{" +
                "totalLength=" + totalLength +
                ", curLength=" + curLength +
                ", speed='" + speed + '\'' +
                '}';
    }
}
