package com.zhengsr.zdwon_lib.entrance.imp.task;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;

import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.bean.ZThreadBean;
import com.zhengsr.zdwon_lib.callback.DownListener;
import com.zhengsr.zdwon_lib.entrance.imp.db.ZDBManager;
import com.zhengsr.zdwon_lib.entrance.imp.net.ZHttpCreate;
import com.zhengsr.zdwon_lib.utils.ZCommontUitls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe:
 */
public class ZDownTask extends DownWorker {
    private static final String TAG = "ZDownTask";
    private volatile long mCurrentLength = 0;
    private ExecutorService mExecutorService ;
    private long mLastTime = 0;
    private long mLastSize = 0;
    private ConcurrentHashMap<Integer, DownloadThread> mThreadMap;
    private DownListener mListener;

    public ZDownTask(ZTaskBean bean) {
        super(bean);
        mListener = (DownListener) bean.listener;
        mThreadMap = new ConcurrentHashMap<>();
        mCurrentLength = 0;
        mExecutorService = new ThreadPoolExecutor(bean.threadCount,bean.threadCount*2+1,0,
                TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(128));

        checkMemory(bean);
    }



    @Override
    public void handleData(ZTaskBean bean) {
        mThreadMap.clear();
        //每一块的大小
        long blockSize = bean.fileLength / bean.threadCount;

        //1 首先检查数据库
        List<ZThreadBean> threadBeans = ZDBManager.getInstance().getAllInfo();

        if (threadBeans != null && threadBeans.size() > 0) {
            //如果已经有数据保存了
            for (int i = 0; i < threadBeans.size(); i++) {
                long end = (i + 1) * blockSize ;
                //最后一个除不尽，用文件长度代替
                if (i == bean.threadCount - 1) {
                    end = bean.fileLength;
                }

                //重新弄开始和结束的点
                ZThreadBean cacheBean = threadBeans.get(i);
                //起点加上上一次的下载的长度
                cacheBean.startPos = cacheBean.startPos + cacheBean.threadLength;

                //重新保存数据库
                ZDBManager.getInstance().saveOrUpdate(cacheBean);
                DownloadThread downloadThread = new DownloadThread(cacheBean, bean,new checkTask());
                mThreadMap.put(i,downloadThread);
            }

        }else{
            //新任务，先删除数据库和本地文件
            deleteCache();
            for (int i = 0; i < bean.threadCount; i++) {
                long start = i * blockSize;
                //不去减 1 也可以
                long end = (i + 1) * blockSize;
                //最后一个除不尽，用文件长度代替
                if (i == bean.threadCount - 1) {
                    end = bean.fileLength;
                }
                ZThreadBean threadBean = new ZThreadBean();
                threadBean.url = bean.url;
                threadBean.name = bean.fileName;
                threadBean.startPos = start;
                threadBean.endPos = end;
                threadBean.threadId = i;
                //先保存数据库
                ZDBManager.getInstance().saveOrUpdate(threadBean);
                DownloadThread downloadThread = new DownloadThread(threadBean, bean,new checkTask());
                mThreadMap.put(i,downloadThread);
            }
        }

        /**
         * 启动所有线程
         */
        int size = mThreadMap.size();
        for (int i = 0; i < size; i++) {
            DownloadThread downloadThread = mThreadMap.get(i);
            mExecutorService.execute(downloadThread);
        }


    }


    /**
     * 下载线程
     */
    class DownloadThread extends  Thread{
        boolean isDone = false;
        boolean isPause = false;
        ZThreadBean bean;
        ZTaskBean taskBean;
        TaskListener listener;
        public DownloadThread(ZThreadBean bean,  ZTaskBean zBean, final TaskListener listener){
            this.bean = bean;
            taskBean = zBean;
            this.listener = listener;
        }

        @Override
        public void run() {
            super.run();
            String rangeHeader = "bytes="+bean.startPos+"-"+bean.endPos;
            RandomAccessFile raf = null;
            try {
                Call<ResponseBody> call = ZHttpCreate.getService().download(bean.url,rangeHeader);
                Response<ResponseBody> response = call.execute();
                if (response != null && response.body() != null) {
                    /**
                     * 拿到文件的输入流
                     */
                    InputStream is = response.body().byteStream();
                    /**
                     * 把数据写到文件中
                     */
                    File file = new File(taskBean.filePath,taskBean.fileName);
                    raf = new RandomAccessFile(file, "rwd");
                    //找到上一次的点
                    raf.seek(bean.startPos);
                    byte[] bytes = new byte[1024 * 2];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        raf.write(bytes, 0, len);
                        //保存下载的数据
                        bean.threadLength = bean.threadLength+len;
                        listener.onProgress(len);

                        //暂停
                        if (isPause){
                            ZDBManager.getInstance().saveOrUpdate(bean);
                            return;
                        }
                    }

                    isDone = true;
                }else{
                   throw new RuntimeException("cannot get ResponseBody ");
                }
            } catch (IOException e) {
                listener.onFail(e.getMessage());
               // e.printStackTrace();
            }finally {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            /**
             * 检查是否全部完成
             */
            listener.checkIsFinish();
        }
    }





    interface TaskListener{
        void onFail(String msg);
        void onProgress(long len);
        void checkIsFinish();
    }


    static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 从线程转换成 UI 线程
     */
    @SuppressLint("CheckResult")
    class checkTask implements TaskListener{

        @Override
        public void onFail(final String msg) {
            /**
             * 重置一些属性
             */
            resetData();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mTaskBean.listener.onFail(msg);
                }
            });
        }


        @Override
        public void checkIsFinish() {
            boolean isDone = true;
            int size = mThreadMap.size();
            for (int i = 0; i < size; i++) {
                DownloadThread thread = mThreadMap.get(i);
                if (!thread.isDone){
                    isDone = false;
                    break;
                }
            }
            //已经下载完成
            if (isDone){
                ZDBManager.getInstance().deleteAll();
                mExecutorService.shutdownNow();
                mCurrentLength = 0;
                File file = new File(mTaskBean.filePath,mTaskBean.fileName);
                if (file.exists()){
                    if (file.length() == mTaskBean.fileLength){
                        String mdMsg = ZCommontUitls.getFileMD5(file);
                        mBean.curLength = mTaskBean.fileLength;
                        mBean.progress = 100;
                        mBean.totalLength = mTaskBean.fileLength;
                        mBean.speed = "";
                        mListener.onDownloading(mBean);
                        mListener.onSuccess(file.getAbsolutePath(),mdMsg);
                    }else{
                       mListener.onFail("size different! file length: "+file.length()+" / server size: "+mTaskBean.fileLength);
                    }
                }
            }


        }

        @Override
        public void onProgress(long len) {
            synchronized (ZDownTask.class) {
                mCurrentLength = mCurrentLength + len;
                long now = System.currentTimeMillis();
                if (now - mLastTime >= mTaskBean.reFreshTime) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            float progress = mCurrentLength * 100.0f / mTaskBean.fileLength;
                            long size = mCurrentLength - mLastSize;

                            mBean.curLength = mCurrentLength;
                            mBean.progress = progress;
                            mBean.totalLength = mTaskBean.fileLength;
                            mBean.speed = Formatter.formatFileSize(mTaskBean.context, size);
                            if (mTaskBean.listener instanceof DownListener) {
                                ((DownListener) mTaskBean.listener).onDownloading(mBean);
                            }
                            mLastSize = mCurrentLength;
                            Log.d(TAG, "zsr run: "+mBean.progress);

                        }
                    });
                    mLastTime = now;
                }
            }
        }
    }

    /**
     * 重置一些属性
     */
    private void resetData() {
        mCurrentLength = 0;
    }

    /**
     * 删除本地文件和数据库
     */
    private void deleteCache() {
        synchronized (this) {
            ZDBManager.getInstance().deleteAll();
            File file = new File(mTaskBean.filePath, mTaskBean.fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    /**
     * 暂停
     */
    public void pause(){
        synchronized (this){
            int size = mThreadMap.size();
            for (int i = 0; i < size; i++) {
                DownloadThread downloadThread = mThreadMap.get(i);
                if (downloadThread != null) {
                    downloadThread.isPause = true;
                }
            }
        }

    }

    public void start(){
        handleData(mTaskBean);
    }



}
