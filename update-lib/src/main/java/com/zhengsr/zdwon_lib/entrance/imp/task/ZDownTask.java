package com.zhengsr.zdwon_lib.entrance.imp.task;

import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.bean.ZThreadBean;
import com.zhengsr.zdwon_lib.entrance.imp.net.ZHttpCreate;
import com.zhengsr.zdwon_lib.utils.ZCommontUitls;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.internal.subscribers.BlockingSubscriber;
import okhttp3.ResponseBody;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe:
 */
public class ZDownTask extends DownWorker {
    private CompositeDisposable mDisposable;
    public ZDownTask(ZBean bean) {
        super(bean);
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void handleData(ZBean bean) {
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
                downFile(bean,start,end);
            }
        }
    }


    private void downFile(final ZBean bean, final long start, long end){
        String rangeHeader = "bytes="+start+"-"+end;
        addSubscribe(
                ZHttpCreate.getService().download(bean.url,rangeHeader)
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody body) throws Exception {
                        //拿到文件的输入流

                        RandomAccessFile raf = null;

                        try {
                            InputStream is = body.byteStream();
                            File file = new File(bean.filePath, bean.fileName);
                            raf = new RandomAccessFile(file, "rwd");
                            //找到上一次的点
                            raf.seek(start);
                            byte[] bytes = new byte[1024 * 2];
                            int len;
                            while ((len = is.read(bytes)) != -1) {
                                raf.write(bytes, 0, len);
                            }

                        }catch (Exception e){
                            return "error: "+e.getMessage();
                        }finally {
                            if (raf != null) {
                                raf.close();
                            }
                        }

                        return null;
                    }
                }).compose(ZCommontUitls.<String>rxScheduers())
                .subscribeWith(new BlockingBaseObserver<String>() {
                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })

        );
    }

}
