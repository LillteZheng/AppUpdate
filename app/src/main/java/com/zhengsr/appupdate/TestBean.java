package com.zhengsr.appupdate;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe:
 */
public class TestBean {

    /**
     * content : 1、增加了夜间模式，支持护眼
     2、优化了一些bug，体验更流畅
     * url : https://raw.githubusercontent.com/LillteZheng/WanAndroid/master/apk/wanandroid.apk
     * versioncode : 1.1
     */

    private String content;
    private String url;
    private double versioncode;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(double versioncode) {
        this.versioncode = versioncode;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", versioncode=" + versioncode +
                '}';
    }
}
