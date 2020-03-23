package com.zhengsr.zdwon_lib.entrance.imp.task;

import android.os.Handler;
import android.os.Looper;

import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.bean.ZThreadBean;
import com.zhengsr.zdwon_lib.entrance.imp.net.ZHttpCreate;
import com.zhengsr.zdwon_lib.utils.ZCommontUitls;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.functions.Function;
import io.reactivex.internal.observers.BlockingBaseObserver;
import okhttp3.ResponseBody;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe:
 */
public class ZDownTask extends DownWorker {
    private static final String TAG = "ZDownTask";
    private volatile long mTaskSize = 0;
    private long mLastTime = 0;

    public ZDownTask(ZTaskBean bean) {
        super(bean);
    }



    @Override
    public void handleData(ZTaskBean bean) {
        //每一块的大小
        long blockSize = bean.fileLength / bean.threadCount;
        //使用数据
        if (bean.useBreakPoint){

        }else{
            //分多个任务
            for (int i = 0; i < bean.threadCount; i++) {
                long start = i * blockSize;
                long end = (i+1) * blockSize;
                //最后一个用文件长度代替
                if (i == bean.threadCount - 1){
                    end = bean.fileLength;
                }
                ZThreadBean threadBean = new ZThreadBean();
                threadBean.url = bean.url;
                threadBean.name = bean.fileName;
                threadBean.startPos = start;
                threadBean.endPos = end;
                threadBean.threadId = i;
                downFile(threadBean,bean,new checkTask());
            }
        }
    }


    private void downFile(final ZThreadBean bean, final ZTaskBean zBean, final TaskListener listener){
        String rangeHeader = "bytes="+bean.startPos+"-"+bean.endPos;
        addSubscribe(
                ZHttpCreate.getService().download(bean.url,rangeHeader)
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody body) throws Exception {
                        //拿到文件的输入流

                        RandomAccessFile raf = null;

                        try {
                            InputStream is = body.byteStream();
                            File file = new File(zBean.filePath, zBean.fileName);
                            raf = new RandomAccessFile(file, "rwd");
                            //找到上一次的点
                            raf.seek(bean.startPos);
                            byte[] bytes = new byte[1024 * 2];
                            int len;
                            while ((len = is.read(bytes)) != -1) {
                                raf.write(bytes, 0, len);

                                listener.onProgress(len);
                            }
                            listener.onSuccess();
                            return "success";
                        }catch (Exception e){
                            listener.onFail(e.getMessage());
                            return "error: "+e.getMessage();
                        }finally {
                            if (raf != null) {
                                raf.close();
                            }
                        }


                    }
                }).compose(ZCommontUitls.<String>rxScheduers())
                .subscribeWith(new BlockingBaseObserver<String>() {
                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onFail(e.getMessage());
                    }
                })

        );
    }


    interface TaskListener{
        void onFail(String msg);
        void onSuccess();
        void onProgress(long len);
    }


    static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 从线程转换成 UI 线程
     */
    class checkTask implements TaskListener{

        @Override
        public void onFail(String msg) {

        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onProgress(long len) {
            mTaskSize = mTaskSize + len;
            long now = System.currentTimeMillis();
            if (now - mLastTime >= mTaskBean.reFreshTime){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBean.curLength = mTaskSize;
                        mBean.fileLength = mTaskBean.fileLength;
                    }
                });
                mLastTime = now;
            }
        }
    }



}
