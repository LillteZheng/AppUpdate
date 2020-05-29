package com.zhengsr.appupdate;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.zhengsr.appupdate.bean.CheckBean;
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




        ZDown.checkWith(this)
                .url(jsonUrlTest)
                .get()
                .listener(new CheckListener<TestBean>() {
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
