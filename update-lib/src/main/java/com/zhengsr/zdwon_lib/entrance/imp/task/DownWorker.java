package com.zhengsr.zdwon_lib.entrance.imp.task;

import android.annotation.SuppressLint;

import com.zhengsr.zdwon_lib.bean.ZBean;
import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.entrance.imp.net.ZHttpCreate;
import com.zhengsr.zdwon_lib.utils.ZCommontUitls;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.BlockingBaseObserver;
import okhttp3.ResponseBody;

/**
 * @auther by zhengshaorui on 2020/3/22
 * describe: 实现下载的统一worker
 */
public abstract class DownWorker {
    private static final String TAG = "DownWorker";
    private CompositeDisposable mDisposable;
    protected ZTaskBean mTaskBean;
    protected ZBean mBean;
    public DownWorker(ZTaskBean bean) {
        mTaskBean = bean;
        mBean = new ZBean();
        checkMemory(bean);
    }


    /**
     * 文件是否超过内存大小
     * @param bean
     */
    @SuppressLint("CheckResult")
    private void checkMemory(final ZTaskBean bean){

        //如果传递了文件长度，则对比是否超过内存大小了
        if (bean.fileLength != -1){
           if (!isCanDown(bean.filePath,bean.fileLength)){
               bean.listener.onFail(bean.filePath+"error : No buffer space here");
           }else{
               handleData(bean);
           }
        }else{
            ZHttpCreate.getService().getFileLength(bean.url)
                    .compose(ZCommontUitls.<ResponseBody>rxScheduers())
                    .subscribeWith(new BlockingBaseObserver<ResponseBody>() {
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            long contentLength = responseBody.contentLength();

                            if (!isCanDown(bean.filePath,contentLength)){
                                bean.listener.onFail(bean.filePath+"error : No buffer space here");
                            }else{
                                bean.fileLength = contentLength;
                                handleData(bean);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            bean.listener.onFail(e.getMessage());
                        }
                    });
        }
    }

    /**
     * 是否有多余空间
     * @return
     */
    private boolean isCanDown(String filePath,long fileLength){
        long deviceSize = ZCommontUitls.getAvailDiskSize(filePath);
        if (fileLength > deviceSize){
            return false;
        }
        return true;
    }

    public abstract void handleData(ZTaskBean bean);

    public void addSubscribe(Disposable disposable){
        if (mDisposable == null){
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    public void dispose(){
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable.clear();
        }
    }
}
