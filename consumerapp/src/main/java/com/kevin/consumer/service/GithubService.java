package com.kevin.consumer.service;

import com.kevin.consumer.model.SearchUser;
import com.kevin.consumer.model.SearchUserInfo;
import com.kevin.consumer.model.UserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubService {
    @GET("/users/{username}")
    Call<UserDetails>getUserDetail(@Path("username") String username, @Header("Authorization") String token);

    @GET("search/users")
    Call<SearchUser>cariUser(@Query("q") String username, @Header("Authorization") String token);

    @GET("/users/{username}/followers")
    Call<List<SearchUserInfo>>getUserFollowers(@Path("username") String username, @Header("Authorization") String token);

    @GET("/users/{username}/following")
    Call<List<SearchUserInfo>>getUserFollowing(@Path("username") String username, @Header("Authorization") String token);
}