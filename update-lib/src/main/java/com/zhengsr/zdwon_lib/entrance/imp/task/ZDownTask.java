package com.zhengsr.zdwon_lib.entrance.imp.task;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;

import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.bean.ZThreadBean;
import com.zhengsr.zdwon_lib.callback.BaseListener;
import com.zhengsr.zdwon_lib.callback.TaskListener;
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
import java.util.concurrent.atomic.AtomicBoolean;

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
    private TaskListener mListener;

    private AtomicBoolean isReady = new AtomicBoolean(true);
    public ZDownTask(ZTaskBean bean) {
        super(bean);
        mListener = (TaskListener) bean.listener;
        mThreadMap = new ConcurrentHashMap<>();
        mCurrentLength = 0;

        mExecutorService = new ThreadPoolExecutor(bean.threadCount,bean.threadCount*2+1,0,
                TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(128));
        checkMemory(bean);
    }



    @Override
    public void handleData(ZTaskBean bean) {
        if (isReady.get()) {

            if (mExecutorService != null && mExecutorService.isShutdown()){
                mExecutorService = new ThreadPoolExecutor(bean.threadCount,bean.threadCount*2+1,0,
                        TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(128));
            }

            mThreadMap.clear();
            mCurrentLength = 0;
            //每一块的大小
            long blockSize = bean.fileLength / bean.threadCount;

            //1 首先检查数据库
            List<ZThreadBean> threadBeans = ZDBManager.getInstance().getAllInfo(bean.url);

            if (threadBeans != null && threadBeans.size() > 0) {
                //如果已经有数据保存了

                for (int i = 0; i < threadBeans.size(); i++) {


                    //重新弄开始和结束的点
                    ZThreadBean cacheBean = threadBeans.get(i);
                    //起点加上上一次的下载的长度
                    cacheBean.startPos = cacheBean.startPos + cacheBean.threadLength;
                    mCurrentLength += cacheBean.threadLength;

                    //重新保存数据库
                    ZDBManager.getInstance().saveOrUpdate(cacheBean);
                    DownloadThread downloadThread = new DownloadThread(cacheBean, bean, new checkTask());
                    mThreadMap.put(i, downloadThread);
                }

            } else {
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
                    DownloadThread downloadThread = new DownloadThread(threadBean, bean, new checkTask());
                    mThreadMap.put(i, downloadThread);
                }
            }

            /**
             * 启动所有线程
             */
            int size = mThreadMap.size();
            Log.d(TAG, "zsr handleData: " + size);
            for (int i = 0; i < size; i++) {
                DownloadThread downloadThread = mThreadMap.get(i);
                mExecutorService.execute(downloadThread);
            }
            isReady.set(false);
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
        statusListener listener;
        public DownloadThread(ZThreadBean bean,  ZTaskBean zBean, final statusListener listener){
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





    interface statusListener {
        void onFail(String msg);
        void onProgress(long len);
        void checkIsFinish();
    }


    static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 从线程转换成 UI 线程
     */
    @SuppressLint("CheckResult")
    class checkTask implements statusListener {

        @Override
        public void onFail(final String msg) {
            /**
             * 重置一些属性
             */
            pause();
            resetData();
            mExecutorService.shutdownNow();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mTaskBean.listener.onFail(msg);
                }
            });
        }


        @Override
        public synchronized void checkIsFinish() {

            isReady.set(true);
            //已经下载完成
            handler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isDone = true;
                    int size = mThreadMap.size();
                    for (int i = 0; i < size; i++) {
                        DownloadThread thread = mThreadMap.get(i);
                        if (thread != null && !thread.isDone){
                            isDone = false;
                            break;
                        }
                    }
                    if (isDone){
                        ZDBManager.getInstance().deleteAll(mTaskBean.url);
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
                                resetData();
                            }else{
                                onFail("size different! file length:("+file.length()+") > fileLength("+mTaskBean.fileLength+")");
                            }
                        }else{
                            onFail("cannot find file "+file.getAbsolutePath());
                        }
                    }

                }
            });


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
                            if (mTaskBean.listener instanceof TaskListener) {
                                ((TaskListener) mTaskBean.listener).onDownloading(mBean);
                            }
                            mLastSize = mCurrentLength;

                            //判断是否文件大小错乱了
                            if (mBean.curLength > mTaskBean.fileLength){
                                onFail("file download fail , curLength("+mCurrentLength+") > fileLength("+mTaskBean.fileLength+")");
                                deleteCache();
                            }

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
        mThreadMap.clear();
        isReady.set(true);
    }

    /**
     * 删除本地文件和数据库
     */
    public void deleteCache() {
        synchronized (this) {
            ZDBManager.getInstance().deleteAll(mTaskBean.url);
            File file = new File(mTaskBean.filePath, mTaskBean.fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 更新接口
     * @param listener
     */
    public void updateListener(BaseListener listener) {
        mTaskBean.listener = listener;
        mListener = (TaskListener) listener;
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
            isReady.set(true);
        }

    }

    public void start(){
        handleData(mTaskBean);
    }


    public boolean isRunning(){
        return mExecutorService != null && !mExecutorService.isTerminated();
    }


}
