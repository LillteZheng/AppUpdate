package com.zhengsr.zdwon_lib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe:
 */
public class ZCommontUitls {

    /**
     * 获取已经存储的大小
     * @return
     */
    public static long getAvailDiskSize(String path){
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSizeLong();
        long availCount = sf.getAvailableBlocksLong();

        return blockSize * availCount;
    }

    /**
     * 封装线程调度
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T,T> rxScheduers(){
        return new ObservableTransformer<T,T>(){
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 获取 file 的 md5 字符串
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytesToHexString(digest.digest());
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 安装apk
     */

    public static void installApk(Context context,String filePath){
        File file = new File(filePath);
        if (!file.exists()){
            return ;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
