package com.kevin.consumer.data.local;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteModel implements Parcelable {
    private int id;
    private final String avatar;
    private String username;
    private final String userType;
    private final String userId;

    public FavoriteModel(int id, String avatar, String username, String userType, String userId) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.userType = userType;
        this.userId = userId;
    }

    protected FavoriteModel(Parcel in) {
        id = in.readInt();
        avatar = in.readString();
        username = in.readString();
        userType = in.readString();
        userId = in.readString();
    }

    public static final Creator<FavoriteModel> CREATOR = new Creator<FavoriteModel>() {
        @Override
        public FavoriteModel createFromParcel(Parcel in) {
            return new FavoriteModel(in);
        }

        @Override
        public FavoriteModel[] newArray(int size) {
            return new FavoriteModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(avatar);
        parcel.writeString(username);
        parcel.writeString(userType);
        parcel.writeString(userId);
    }
}
