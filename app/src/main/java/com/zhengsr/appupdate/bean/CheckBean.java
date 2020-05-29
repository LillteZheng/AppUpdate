package com.zhengsr.appupdate.bean;

/**
 * @author by  zhengshaorui on 2019/9/6
 * Describe:
 */
public class CheckBean {


    /**
     * erroCode : 0
     * message : null
     * data :
     */

    private int erroCode;
    private Object message;
    private DataBean data;

    public int getErroCode() {
        return erroCode;
    }

    public void setErroCode(int erroCode) {
        this.erroCode = erroCode;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CheckBean{" +
                "erroCode=" + erroCode +
                ", message=" + message +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * channelid : 10
         * channelname : 欢迎词
         * remark : 1、增加多人签名功能，效率更高、2、增加多种字体，满足会议需求
         * packagename : com.hk.hkwelcome
         * classname :
         * img :
         * apk :
         * tag : null
         * isdelete : null
         * versioncode : 2
         * versionname : 1.1.0.9
         * uptime : 2020-05-20 15:27:33
         * apksize : 42342500
         * downtimes : 0
         * isforce : 0
         */

        private int channelid;
        private String channelname;
        private String remark;
        private String packagename;
        private String classname;
        private String img;
        private String apk;
        private Object tag;
        private Object isdelete;
        private String versioncode;
        private String versionname;
        private String uptime;
        private int apksize;
        private int downtimes;
        private int isforce;

        public int getChannelid() {
            return channelid;
        }

        public void setChannelid(int channelid) {
            this.channelid = channelid;
        }

        public String getChannelname() {
            return channelname;
        }

        public void setChannelname(String channelname) {
            this.channelname = channelname;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getPackagename() {
            return packagename;
        }

        public void setPackagename(String packagename) {
            this.packagename = packagename;
        }

        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getApk() {
            return apk;
        }

        public void setApk(String apk) {
            this.apk = apk;
        }

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public Object getIsdelete() {
            return isdelete;
        }

        public void setIsdelete(Object isdelete) {
            this.isdelete = isdelete;
        }

        public String getVersioncode() {
            return versioncode;
        }

        public void setVersioncode(String versioncode) {
            this.versioncode = versioncode;
        }

        public String getVersionname() {
            return versionname;
        }

        public void setVersionname(String versionname) {
            this.versionname = versionname;
        }

        public String getUptime() {
            return uptime;
        }

        public void setUptime(String uptime) {
            this.uptime = uptime;
        }

        public int getApksize() {
            return apksize;
        }

        public void setApksize(int apksize) {
            this.apksize = apksize;
        }

        public int getDowntimes() {
            return downtimes;
        }

        public void setDowntimes(int downtimes) {
            this.downtimes = downtimes;
        }

        public int getIsforce() {
            return isforce;
        }

        public void setIsforce(int isforce) {
            this.isforce = isforce;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "channelid=" + channelid +
                    ", channelname='" + channelname + '\'' +
                    ", remark='" + remark + '\'' +
                    ", packagename='" + packagename + '\'' +
                    ", classname='" + classname + '\'' +
                    ", img='" + img + '\'' +
                    ", apk='" + apk + '\'' +
                    ", tag=" + tag +
                    ", isdelete=" + isdelete +
                    ", versioncode='" + versioncode + '\'' +
                    ", versionname='" + versionname + '\'' +
                    ", uptime='" + uptime + '\'' +
                    ", apksize=" + apksize +
                    ", downtimes=" + downtimes +
                    ", isforce=" + isforce +
                    '}';
        }
    }
}
