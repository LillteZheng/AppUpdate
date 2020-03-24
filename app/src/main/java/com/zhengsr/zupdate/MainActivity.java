package com.zhengsr.zupdate;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zhengsr.zdwon_lib.ZDown;
import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.callback.DownListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://raw.githubusercontent.com/LillteZheng/WanAndroid/master/apk/update.json";
        String fileUrl = "http://192.168.1.157:8089/xampp.exe";

        mTextView = findViewById(R.id.text);

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
                        //Log.d(TAG, "zsr onDownloading: "+bean);
                        String curMsg = Formatter.formatFileSize(MainActivity.this, bean.curLength);
                        String totalMsg = Formatter.formatFileSize(MainActivity.this, bean.totalLength);
                        StringBuilder sb = new StringBuilder();
                        sb.append("当前下载: ").append(curMsg).append(" / ").append(totalMsg).append(" / 速度: ").append(bean.speed);

                    }

                    @Override
                    public void onFail(String errorMsg) {
                        Log.d(TAG, "zsr onFail: "+errorMsg);
                    }
                }).down();
    }

    public void pause(View view) {
        ZDown.pause();
    }
}
