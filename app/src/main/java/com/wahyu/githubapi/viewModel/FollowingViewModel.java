package com.wahyu.githubapi.viewModel;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wahyu.githubapi.BuildConfig;
import com.wahyu.githubapi.service.ServiceGenerator;
import com.wahyu.githubapi.model.SearchUserInfo;
import com.wahyu.githubapi.service.GithubService;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wahyu_septiadi on 29, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class FollowingViewModel extends ViewModel {
    private final MutableLiveData<List<SearchUserInfo>> searchUserInfo = new MutableLiveData<>();

    public LiveData<List<SearchUserInfo>> getFollowingData(){
        return searchUserInfo;
    }

    public void setFollowingData(String username) {
        GithubService gitService = ServiceGenerator.build().create(GithubService.class);

        Call<List<SearchUserInfo>> callAsync = gitService.getUserFollowing(username, BuildConfig.APIGithub1);

        callAsync.enqueue(new Callback<List<SearchUserInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchUserInfo>> call, @NonNull Response<List<SearchUserInfo>> response) {
                if(response.body() != null) {
                    searchUserInfo.setValue(response.body());
                }else{
                    assert response.body() != null;
                    Log.e("ERROR FOLLOWING", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchUserInfo>> call, @NonNull Throwable t) {
                Log.e("FAILURE FOLLOWING", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
