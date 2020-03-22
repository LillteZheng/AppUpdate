package com.zhengsr.zupdate;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.zhengsr.zdwon_lib.ZDown;
import com.zhengsr.zdwon_lib.callback.BaseListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://raw.githubusercontent.com/LillteZheng/WanAndroid/master/apk/update.json";
        String fileUrl = "https://raw.githubusercontent.com/LillteZheng/WanAndroid/master/apk/wanandroid.apk";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        ZDown.with(this)
                .url(fileUrl)
                .filePath(path)
                .reFreshTime(1000)
                .listener(new BaseListener() {
                    @Override
                    public void onFail(String errorMsg) {
                        Log.d(TAG, "zsr onFail: "+errorMsg);
                    }
                }).down();
    }
}
