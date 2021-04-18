package com.wahyu.githubapi.view;

import com.wahyu.githubapi.model.FavoriteModel;

import java.util.ArrayList;

interface LoadFavCallback {
    void preExecute();
    void postExecute(ArrayList<FavoriteModel> favMod);
}
