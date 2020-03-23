package com.zhengsr.zupdate;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zhengsr.zdwon_lib.ZDown;
import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.callback.BaseListener;
import com.zhengsr.zdwon_lib.callback.DownListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://raw.githubusercontent.com/LillteZheng/WanAndroid/master/apk/update.json";
        String fileUrl = "http://192.168.1.154:8089/xampp.exe";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        ZDown.with(this)
                .url(fileUrl)
                .filePath(path)
                .reFreshTime(1000)
                .threadCount(3)
                .listener(new DownListener() {
                    @Override
                    public void onSuccess(String filePath, String md5Msg) {
                        Log.d(TAG, "zsr onSuccess: "+filePath+" "+md5Msg);
                    }

                    @Override
                    public void onDownloading(ZBean bean) {
                        Log.d(TAG, "zsr onDownloading: "+bean);
                    }

                    @Override
                    public void onFail(String errorMsg) {
                        Log.d(TAG, "zsr onFail: "+errorMsg);
                    }
                }).down();
    }
}
