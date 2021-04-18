package com.kevin.consumer.view.following;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.consumer.BuildConfig;
import com.kevin.consumer.data.remote.response.UserResultResponse;
import com.kevin.consumer.data.remote.ServiceGenerator;
import com.kevin.consumer.data.remote.GithubService;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingViewModel extends ViewModel {
    private final MutableLiveData<List<UserResultResponse>> searchUserInfo = new MutableLiveData<>();

    public LiveData<List<UserResultResponse>> getFollowingData(){
        return searchUserInfo;
    }

    public void setFollowingData(String username) {
        GithubService gitService = ServiceGenerator.build().create(GithubService.class);

        Call<List<UserResultResponse>> callAsync = gitService.getUserFollowing(username, BuildConfig.GithubToken);

        callAsync.enqueue(new Callback<List<UserResultResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserResultResponse>> call, @NonNull Response<List<UserResultResponse>> response) {
                if(response.body() != null) {
                    searchUserInfo.setValue(response.body());
                }else{
                    assert response.body() != null;
                    Log.e("ERROR FOLLOWING", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserResultResponse>> call, @NonNull Throwable t) {
                Log.e("FAILURE FOLLOWING", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}