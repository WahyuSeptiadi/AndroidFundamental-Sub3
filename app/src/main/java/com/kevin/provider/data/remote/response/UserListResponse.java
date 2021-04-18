package com.kevin.provider.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserListResponse {
    @SerializedName("total_count")
    long total_count;
    @SerializedName("items")
    List<UserResultResponse> items;

    public long getTotal_count() {
        return total_count;
    }

    public List<UserResultResponse> getItems() {
        return items;
    }
}
