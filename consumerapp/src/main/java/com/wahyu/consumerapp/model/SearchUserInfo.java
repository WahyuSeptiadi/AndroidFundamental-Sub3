package com.wahyu.consumerapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wahyu_septiadi on 25, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class SearchUserInfo {

    @SerializedName("login")
    private final String login;
    @SerializedName("id")
    private long id;
    @SerializedName("avatar_url")
    private final String avatarUrl;
    @SerializedName("type")
    private final String type;

    public SearchUserInfo(String login, String avatarUrl, String type) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.type = type;
    }

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getType() {
        return type;
    }

}
