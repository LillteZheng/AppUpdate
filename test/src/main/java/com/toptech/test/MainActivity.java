package com.toptech.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rachel.updatelib.IDownloadView;
import com.rachel.updatelib.VersionManager;
import com.rachel.updatelib.callback.VersionCallback;
import com.rachel.updatelib.enties.FileInfo;
import com.rachel.updatelib.enties.LocalInfo;
import com.rachel.updatelib.presenter.UpdatePresenter;
import com.rachel.updatelib.toolUtils.ToolUtils;
import com.rachel.updatelib.toolUtils.log;

public class MainActivity extends AppCompatActivity implements IDownloadView{
    private TextView mDownloadSize,mFileSize,mProgressText;
    private ProgressBar mProgressBar;

    private UpdatePresenter mUpdatePresenter;
    private View rootview;
    private PopupWindow mPopupWindow;
    private boolean ispause = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUpdatePresenter = UpdatePresenter.getInstance(this);
        rootview = LayoutInflater.from(this).inflate(R.layout.activity_main,null);

        FileInfo fileInfo = new FileInfo.Builder()
                .setFileName("小白点")
                .setVersionCode(2)
                .setVersionName("1.1")
                .setVersionMsg("1、添加builder模式\n2、添加任务删除方法")
                .setFileUrl("http://downloads.jianshu.io/apps/haruki/jianShu-release-2.1.3-JianShu.apk")
                .builder();
        VersionManager.getInstance(this).checkUpdateUseFileInfo(fileInfo, new VersionCallback() {
            @Override
            public void success(FileInfo fileInfo, LocalInfo localInfo) {
                log.d("success");
                showPopupWindow(MainActivity.this,rootview,fileInfo);
            }

            @Override
            public void lastest() {

            }
        });
    }

    public  void showPopupWindow(Context context, View rootView, final FileInfo fileInfo){
        View view = LayoutInflater.from(context).inflate(R.layout.apkupdatewindow, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        TextView textVersion = (TextView) view.findViewById(R.id.app_version);
        TextView textMsg = (TextView) view.findViewById(R.id.app_updatemsg);
        TextView textsize = (TextView) view.findViewById(R.id.app_size);


        textVersion.setText(context.getResources().getString(R.string.appversion) + fileInfo.getVersionname());
        long filesize =  fileInfo.getFilesize();

        textsize.setText(context.getResources().getString(R.string.appsize) +
                ToolUtils.getFloatSize(filesize));


        textMsg.setText(fileInfo.getVersionmsg());


        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                mUpdatePresenter.startDownload(fileInfo);
                showDownloadInfo(MainActivity.this,rootview);

            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    public  void showDownloadInfo(Context context, View rootView){
        View view = LayoutInflater.from(context).inflate(R.layout.filedownload, null);
        mPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mDownloadSize = (TextView) view.findViewById(R.id.downloadsize);
        mFileSize = (TextView) view.findViewById(R.id.filesize);
        mProgressText = (TextView) view.findViewById(R.id.progresstext);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        final Button btnStatus = (Button) view.findViewById(R.id.appstatus);

        //暂停
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ispause){
                    btnStatus.setText("暂停");
                    ispause = true;
                    if (mUpdatePresenter != null){
                        mUpdatePresenter.pause();
                    }
                }else{
                    ispause = false;
                    btnStatus.setText("开始");
                    mUpdatePresenter.restart();

                }
            }
        });


        //删除
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdatePresenter.delete();
                mPopupWindow.dismiss();
                Toast.makeText(MainActivity.this, "下载任务已经删除", Toast.LENGTH_SHORT).show();
            }
        });

        mPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    @Override
    public void setDownloadProgress(int progress){
        log.d("activity: "+progress);
        mProgressBar.setProgress(progress);
        mProgressText.setText(progress+"%");
    }

    @Override
    public void setDownloadSize(String downloadSize) {
        mDownloadSize.setText(downloadSize);
    }

    @Override
    public void setFileSize(String fileSize) {
        mFileSize.setText(fileSize);
    }

    @Override
    public void downloadSuccess() {
        if (mPopupWindow != null){
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void downloadFail(String errorMsg) {
        if (mPopupWindow != null){
          //  mPopupWindow.dismiss();
            Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUpdatePresenter.onDestroy();
    }



}
