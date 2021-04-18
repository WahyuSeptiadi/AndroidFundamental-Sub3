package com.kevin.git.data.remote;

import com.kevin.git.data.remote.response.UserListResponse;
import com.kevin.git.data.remote.response.UserDetailResponse;
import com.kevin.git.data.remote.response.UserResultResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/users/{username}")
    Call<UserDetailResponse> getUserDetail(@Path("username") String username, @Header("Authorization") String token);

    @GET("search/users")
    Call<UserListResponse> cariUser(@Query("q") String username, @Header("Authorization") String token);

    @GET("/users/{username}/followers")
    Call<List<UserResultResponse>> getUserFollowers(@Path("username") String username, @Header("Authorization") String token);

    @GET("/users/{username}/following")
    Call<List<UserResultResponse>> getUserFollowing(@Path("username") String username, @Header("Authorization") String token);
}
