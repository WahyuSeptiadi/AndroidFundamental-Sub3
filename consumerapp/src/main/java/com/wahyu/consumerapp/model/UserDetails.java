package com.wahyu.consumerapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wahyu_septiadi on 28, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class UserDetails {
    @SerializedName("login")
    String login;
    @SerializedName("id")
    long id;
    @SerializedName("avatar_url")
    String avatar_url;
    @SerializedName("name")
    String name;
    @SerializedName("company")
    String company;
    @SerializedName("location")
    String location;
    @SerializedName("public_repos")
    int public_repos;
    @SerializedName("followers")
    int followers;
    @SerializedName("following")
    int following;

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public int getPublic_repos() {
        return public_repos;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

}
