package com.zzz.webscanf;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static ApiManager instance;
    private Retrofit retrofit;
    private String host="https://www.mxnzp.com";
    public static ApiManager getInstance() {
        if (instance == null) {
            synchronized (ApiManager.class) {
                if (instance == null) {
                    instance = new ApiManager();
                }
            }
        }
        return instance;
    }

    private ApiManager() {
        init();
    }

    private void init(){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new CommonInterceptor())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public  <T> T create(Class<T> clazz) {
        return retrofit.create(clazz);
    }



}
