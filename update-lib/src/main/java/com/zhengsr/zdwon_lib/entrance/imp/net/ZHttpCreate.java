package com.zhengsr.zdwon_lib.entrance.imp.net;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by zhengshaorui
 * Time on 2018/8/14
 */

public class ZHttpCreate {

    private static final int TIME_OUT = 20;

    /**
     * 获取retrofit服务
     * @return
     */
    public static ZHttpServer getService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/")
                //转字符串
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(getOkhttpClient())
                .build();
        return retrofit.create(ZHttpServer.class);
    }

    public static OkHttpClient getOkhttpClient(){
        return OkHttpHolder.BUILDER;
    }

    /**
     * 配置okhttp3 client
     */
    private static class OkHttpHolder{
         static OkHttpClient BUILDER = new OkHttpClient.Builder()
                 .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                 .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                 .writeTimeout(TIME_OUT+TIME_OUT,TimeUnit.SECONDS)
                 .addInterceptor(new Interceptor() {
                     @Override
                     public Response intercept(Chain chain) throws IOException {
                         Request original = chain.request();

                         //防止获取不到 length 的情况
                         Request request = original.newBuilder()
                                 .header("Accept-Encoding", "identity")
                                 .build();

                         return chain.proceed(request);

                     }
                 })
                 .build();
    }
}
