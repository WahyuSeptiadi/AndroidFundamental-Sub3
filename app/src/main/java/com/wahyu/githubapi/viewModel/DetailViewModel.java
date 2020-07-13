package com.wahyu.githubapi.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wahyu.githubapi.BuildConfig;
import com.wahyu.githubapi.service.ServiceGenerator;
import com.wahyu.githubapi.model.UserDetails;
import com.wahyu.githubapi.service.GithubService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wahyu_septiadi on 29, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class DetailViewModel extends ViewModel {
    private final MutableLiveData<UserDetails> detailUser = new MutableLiveData<>();

    public LiveData<UserDetails> getDetailUser(){
        return detailUser;
    }

    public void setDetailUser(String username) {
        GithubService gitService = ServiceGenerator.build().create(GithubService.class);

        Call<UserDetails> callAsync = gitService.getUserDetail(username, BuildConfig.APIGithub1);

        callAsync.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(@NonNull Call<UserDetails> call, @NonNull Response<UserDetails> response) {

                if(response.body() != null) {
                    UserDetails git = response.body();
                    Log.e("SUKSES DETAIL", String.valueOf(response.body()));
                    detailUser.setValue(git);
                }else{
                    assert response.body() != null;
                    Log.e("ERROR DETAIL", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetails> call, @NonNull Throwable t) {
                Log.e("FAILURE DETAIL", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

}
