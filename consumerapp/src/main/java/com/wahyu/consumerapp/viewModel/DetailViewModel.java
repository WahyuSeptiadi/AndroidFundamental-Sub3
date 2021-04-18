package com.wahyu.consumerapp.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wahyu.consumerapp.BuildConfig;
import com.wahyu.consumerapp.model.UserDetails;
import com.wahyu.consumerapp.service.ServiceGenerator;
import com.wahyu.consumerapp.service.GithubService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends ViewModel {
    private final MutableLiveData<UserDetails> detailUser = new MutableLiveData<>();

    public LiveData<UserDetails> getDetailUser(){
        return detailUser;
    }

    public void setDetailUser(String username) {
        GithubService gitService = ServiceGenerator.build().create(GithubService.class);

        Call<UserDetails> callAsync = gitService.getUserDetail(username, BuildConfig.GithubToken);

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
