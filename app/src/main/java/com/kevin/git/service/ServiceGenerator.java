package com.kevin.git.service;

import com.kevin.git.constant.Base;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    public static Retrofit build() {

        return new Retrofit.Builder()
                .baseUrl(Base.GITHUB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
}
