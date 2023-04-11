package com.batson.reliefweb.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */

public class Constants {
///fetching jobs via reliefweb API

    public static final String BASE_URL = "https://api.reliefweb.int/v1/";
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit;
    }
}
