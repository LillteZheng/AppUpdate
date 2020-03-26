package com.zhengsr.zupdate;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.zhengsr.zdwon_lib.ZDown;
import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.callback.CheckListener;
import com.zhengsr.zdwon_lib.callback.TaskListener;
import com.zhengsr.zdwon_lib.utils.ZCommontUitls;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //  String fileUrlTest = "http://192.168.1.154:8089/xampp.exe";

    /**
     * github 的json，可能会比较卡
     */
    String url = "https://raw.githubusercontent.com/LillteZheng/WanAndroid/master/apk/update.json";
    /**
     * 自己的服务器
     */
    String jsonUrlTest = "http://192.168.1.154:8089/update.json";
    String fileUrlTest = "http://192.168.1.154:8089/jianshu.apk";
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPath = Environment.getExternalStorageDirectory().getAbsolutePath();



    }

    public void check(View view) {
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        builder.setTicker("收到新消息"); //设置提示的内容
        builder.setContentTitle("你收到一条新信息"); // 设置标题
        builder.setContentText("废话不多说，今晚网吧见，打不上黄金3不睡觉！！"); // 设置内容
        builder.setSubText("通宵走起");    // 设置下面一段小子
        builder.setDefaults(Notification.DEFAULT_ALL);  // 震动，声音，还信息灯闪烁
        builder.setSmallIcon(R.mipmap.delete);  // 小图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));  // 大图标
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,notification);
        ZDown.with(this)
                .url(jsonUrlTest)
                .listener(new CheckListener<TestBean>(TestBean.class) {
                    @Override
                    public void onCheck(final TestBean data) {

                        final CusDialog dialog = new CusDialog.Builder()
                                .setContext(MainActivity.this)
                                .setLayoutId(R.layout.update_layout)
                                .showAlphaBg(true)
                                .builder();
                        dialog.setDismissByid(R.id.update_dismiss);
                        dialog.setTextView(R.id.update_info,data.getContent());
                        final Button updateBtn = dialog.getViewbyId(R.id.update_btn);
                        final NumberProgressBar progressBar = dialog.getViewbyId(R.id.number_progress_bar);


                        dialog.setOnClickListener(R.id.update_btn, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ZDown.with(MainActivity.this)
                                        .url(fileUrlTest)
                                        .threadCount(3)
                                        .reFreshTime(500)
                                        .filePath(mPath)
                                        .listener(new TaskListener() {
                                            @Override
                                            public void onSuccess(String filePath, String md5Msg) {
                                                ZCommontUitls.installApk(MainActivity.this,filePath);
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onDownloading(ZBean bean) {
                                                int progress = (int) bean.progress;
                                                updateBtn.setVisibility(View.GONE);
                                                progressBar.setVisibility(View.VISIBLE);
                                                progressBar.setProgress(progress);

                                            }

                                            @Override
                                            public void onFail(String errorMsg) {
                                                Log.d(TAG, "zsr onFail: " + errorMsg);
                                                dialog.dismiss();
                                            }
                                        }).down();




                            }
                        });


                    }


                    @Override
                    public void onFail(String errorMsg) {
                        Log.d(TAG, "zsr onFail: " + errorMsg);
                    }
                }).check();
    }


}
