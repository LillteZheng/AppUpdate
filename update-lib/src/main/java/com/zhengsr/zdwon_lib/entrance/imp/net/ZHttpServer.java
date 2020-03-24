package com.zhengsr.zdwon_lib.entrance.imp.net;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zhengshaorui
 * Time on 2018/8/14
 */

public interface ZHttpServer {

    @Streaming
    @GET
    Observable<ResponseBody> getFileLength(@Url String url);

    @Streaming
    @GET
    Observable<ResponseBody> getFileLength(@Url String url, @FieldMap Map<String, String> paramsMap);

    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url, @Header("RANGE") String range);



    @GET
    Call<String> getJson(@Url String url);

    @FormUrlEncoded
    @POST
    Call<String> getJson(@Url String url, @FieldMap Map<String, String> paramsMap);
}
