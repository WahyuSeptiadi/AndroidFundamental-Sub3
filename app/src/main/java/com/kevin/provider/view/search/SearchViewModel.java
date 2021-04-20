package com.kevin.provider.view.search;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.provider.BuildConfig;
import com.kevin.provider.data.remote.ApiService;
import com.kevin.provider.data.remote.Network;
import com.kevin.provider.data.remote.response.UserListResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<UserListResponse> searchUser = new MutableLiveData<>();

    public void setSearchData(String username) {
        loadJSON(username);
    }

    public LiveData<UserListResponse> getSearchData() {
        return searchUser;
    }

    public void loadJSON(String username) {
        ApiService gitService = Network.build().create(ApiService.class);

        Call<UserListResponse> callAsync = gitService.searchUser(username, BuildConfig.GithubToken);

        callAsync.enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserListResponse> call, @NonNull Response<UserListResponse> response) {
                if (response.body() != null) {
                    UserListResponse git = response.body();
                    Log.e("SUCCESS SEARCH", String.valueOf(response.body()));
                    searchUser.setValue(git);
                } else {
                    Log.e("ERROR SEARCH", "response body = null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserListResponse> call, @NonNull Throwable t) {
                Log.e("FAILURE SEARCH", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
