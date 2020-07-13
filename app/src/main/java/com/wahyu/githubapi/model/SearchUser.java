package com.wahyu.githubapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wahyu_septiadi on 28, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

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
