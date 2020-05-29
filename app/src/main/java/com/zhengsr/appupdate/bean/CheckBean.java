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


}
