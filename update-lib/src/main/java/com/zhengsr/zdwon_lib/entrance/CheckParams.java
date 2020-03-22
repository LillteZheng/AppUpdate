package com.zhengsr.zdwon_lib.entrance;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.widget.InvisiabelFragment;

import java.io.File;

/**
 * Created by zhengshaorui
 * Time on 2018/12/6
 */

public class CheckParams {
    private static final String TAG = "CheckParams";
    public CheckParams() {
    }

    public ZBean check(ZBean info){
        //url肯定是必须的
        if (TextUtils.isEmpty(info.url)){
            throw new RuntimeException("url can not be null");
        }

        //如果没写路径，则以默认路径 来
        if (TextUtils.isEmpty(info.filePath)){
            boolean isLake = lacksPermission(info.context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT >= 23 && isLake){
                throw new RuntimeException("you need reuqest WRITE_EXTERNAL_STORAGE");
            }else {
                info.filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "ZDown";
                File file = new File(info.filePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }else{
            //有点话，帮他创建路径
            File file = new File(info.filePath);
            if (!file.exists()){
                file.mkdirs();
            }
        }
        //如果没写文件名，则以url的文件名来识别
        if (TextUtils.isEmpty(info.fileName)){
            info.fileName = info.url.substring(info.url.lastIndexOf("/")+1);
        }
        //默认刷新时间1s
        if (info.reFreshTime < 1000){
            info.reFreshTime = 1000;
        }
        if (info.listener == null){
            throw new RuntimeException("you need register listener to get network status");
        }

        //约定最大线程数
        if (info.threadCount >= 8){
            info.threadCount = 8;
        }

        register(info);

        return info;
    }


    public ZBean checkJsonUrl(ZBean info){
        //url肯定是必须的
        if (TextUtils.isEmpty(info.url)){
            throw new RuntimeException("url can not be null");
        }
        if (info.listener == null){
            throw new RuntimeException("you need register jsonListener to get network status");
        }

        return info;
    }

    /**
     * 判断有该权限，true表示没有
     * @param mContexts
     * @param permission
     * @return
     */
    private  boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) !=
                PackageManager.PERMISSION_GRANTED;

    }


    /**
     * 添加一个没有页面的 fragment，用来监听生命周期
     * @param info
     */
    private void register(final ZBean info){
        if (info.context instanceof FragmentActivity){
            // Log.d(TAG, "zsr --> register: "+info.context);
            FragmentActivity activity = (FragmentActivity) info.context;
            if (activity.isDestroyed()){
                throw new IllegalArgumentException("You cannot start a load task for a destroyed activity");
            }
            //添加一个隐形的 fragment ，用来管理生命周期
            Fragment lifeFramgnet = activity.getSupportFragmentManager().findFragmentByTag(info.url);
            InvisiabelFragment fragment ;
            if (lifeFramgnet != null){
                fragment = (InvisiabelFragment) lifeFramgnet;
            }else{
                fragment = InvisiabelFragment.newInstance();
            }
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            if (!fragment.isAdded()){
                ft.add(fragment,info.url);
                ft.commit();
            }

            fragment.setLifecyleListener(new InvisiabelFragment.LifecyleListener() {
                @Override
                public void onResume() {
                    //Log.d(TAG, "zsr --> onResume: ");
                }

                @Override
                public void onStop() {

                }

                @Override
                public void onDestroy() {
                    // Log.d(TAG, "zsr --> onDestroy: ");
                }
            });

        }
    }
}
