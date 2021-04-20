package com.kevin.provider.data.remote;

import com.kevin.provider.data.remote.response.UserListResponse;
import com.kevin.provider.data.remote.response.UserDetailResponse;
import com.kevin.provider.data.remote.response.UserResultResponse;

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
    Call<UserListResponse> searchUser(@Query("q") String username, @Header("Authorization") String token);

    @GET("/users/{username}/followers")
    Call<List<UserResultResponse>> getUserFollowers(@Path("username") String username, @Header("Authorization") String token);

    @GET("/users/{username}/following")
    Call<List<UserResultResponse>> getUserFollowing(@Path("username") String username, @Header("Authorization") String token);
}
