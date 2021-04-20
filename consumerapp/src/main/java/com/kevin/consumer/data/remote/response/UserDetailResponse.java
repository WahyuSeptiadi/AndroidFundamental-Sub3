package com.kevin.consumer.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class UserDetailResponse {
    @SerializedName("login")
    private final String login;
    @SerializedName("id")
    private long id;
    @SerializedName("avatar_url")
    private final String avatar_url;
    @SerializedName("name")
    private final String name;
    @SerializedName("company")
    private final String company;
    @SerializedName("location")
    private final String location;
    @SerializedName("public_repos")
    private final int public_repos;
    @SerializedName("followers")
    private final int followers;
    @SerializedName("following")
    private final int following;

    public UserDetailResponse(String login, String avatar_url, String name, String company, String location, int public_repos, int followers, int following) {
        this.login = login;
        this.avatar_url = avatar_url;
        this.name = name;
        this.company = company;
        this.location = location;
        this.public_repos = public_repos;
        this.followers = followers;
        this.following = following;
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
