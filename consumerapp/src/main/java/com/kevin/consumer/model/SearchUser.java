package com.kevin.consumer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchUser {
    @SerializedName("total_count")
    long total_count;
    @SerializedName("items")
    List<SearchUserInfo> items;

    public long getTotal_count() {
        return total_count;
    }

    public List<SearchUserInfo> getItems() {
        return items;
    }
}
