package com.wahyu.githubapi.service;

import com.wahyu.githubapi.constant.Base;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wahyu_septiadi on 27, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class ServiceGenerator {
    public static Retrofit build() {

        return new Retrofit.Builder()
                .baseUrl(Base.GITHUB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
}
