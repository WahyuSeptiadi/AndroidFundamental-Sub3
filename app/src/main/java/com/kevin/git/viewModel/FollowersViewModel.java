package com.kevin.git.viewModel;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.git.BuildConfig;
import com.kevin.git.service.ServiceGenerator;
import com.kevin.git.model.SearchUserInfo;
import com.kevin.git.service.GithubService;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersViewModel extends ViewModel {
    private final MutableLiveData<List<SearchUserInfo>> searchUserInfo = new MutableLiveData<>();

    public LiveData<List<SearchUserInfo>> getFollowersData(){
        return searchUserInfo;
    }

    public void setFollowersData(String username) {
        GithubService gitService = ServiceGenerator.build().create(GithubService.class);

        Call<List<SearchUserInfo>> callAsync = gitService.getUserFollowers(username, BuildConfig.GithubToken);

        callAsync.enqueue(new Callback<List<SearchUserInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchUserInfo>> call, @NonNull Response<List<SearchUserInfo>> response) {
                if(response.body() != null) {
                    searchUserInfo.setValue(response.body());
                }else{
                    assert response.body() != null;
                    Log.e("ERROR FOLLOWERS", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchUserInfo>> call, @NonNull Throwable t) {
                Log.e("FAILURE FOLLOWERS", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
