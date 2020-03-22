package com.zhengsr.zdwon_lib.callback;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe: 检查版本的listener
 */
public abstract class CheckListener<T> implements BaseListener {

    public Class<?> mclazz;

    public CheckListener(Class<?> mclazz) {
        this.mclazz = mclazz;
    }

    public abstract void onCheck(T data);

}
