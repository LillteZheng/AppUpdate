package com.zhengsr.zdwon_lib.entrance.imp.task;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.zhengsr.zdwon_lib.bean.ZTaskBean;
import com.zhengsr.zdwon_lib.callback.BaseListener;
import com.zhengsr.zdwon_lib.callback.CheckListener;
import com.zhengsr.zdwon_lib.entrance.imp.net.ZHttpCreate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * @auther by zhengshaorui on 2020/3/22
 * describe: 检查版本的任务
 */

public class ZCheckTask {
    private static final String TAG = "ZCheckTask";

    public ZCheckTask(final ZTaskBean info) {
        Call<String> call;
        if (info.paramsMap != null && info.paramsMap.size() > 0) {
            call = ZHttpCreate.getService().getJson(info.url,info.paramsMap);
        }else{
            call = ZHttpCreate.getService().getJson(info.url);

        }
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (info.listener instanceof CheckListener) {
                    CheckListener listener = (CheckListener) info.listener;
                    try {
                        String json = response.body();
                        Class mclazz = listener.mclazz;
                        if (mclazz==String.class){
                            listener.onCheck(json);
                        }else{
                            Object data =  JSON.parseObject( json, mclazz);
                            listener.onCheck(data);
                        }
                    } catch (Exception e) {
                        listener.onFail(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                info.listener.onFail(t.toString());
            }
        });

    }


}
