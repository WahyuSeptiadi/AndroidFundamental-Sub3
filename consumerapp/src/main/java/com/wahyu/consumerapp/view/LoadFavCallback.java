package com.wahyu.consumerapp.view;

import com.wahyu.consumerapp.model.FavoriteModel;

import java.util.ArrayList;

interface LoadFavCallback {
    void preExecute();
    void postExecute(ArrayList<FavoriteModel> favMod);
}
