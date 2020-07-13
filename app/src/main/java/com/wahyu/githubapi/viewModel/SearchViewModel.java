package com.wahyu.githubapi.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wahyu.githubapi.BuildConfig;
import com.wahyu.githubapi.service.ServiceGenerator;
import com.wahyu.githubapi.model.SearchUser;
import com.wahyu.githubapi.service.GithubService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wahyu_septiadi on 28, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<SearchUser> searchUser = new MutableLiveData<>();

    public void setSearchData(String username){
        loadJSON(username);
    }

    public LiveData<SearchUser> getSearchData(){
        return searchUser;
    }

    public void loadJSON(String username) {
        GithubService gitService = ServiceGenerator.build().create(GithubService.class);

        Call<SearchUser> callAsync = gitService.cariUser(username, BuildConfig.APIGithub1);

        callAsync.enqueue(new Callback<SearchUser>() {
            @Override
            public void onResponse(@NonNull Call<SearchUser> call, @NonNull Response<SearchUser> response) {
                if(response.body() != null) {
                    SearchUser git = response.body();
                    Log.e("SUKSES SEARCH", String.valueOf(response.body()));
                    searchUser.setValue(git);
                }else{
                    assert response.body() != null;
                    Log.e("ERROR SEARCH", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchUser> call, @NonNull Throwable t) {
                Log.e("FAILURE SEARCH", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
