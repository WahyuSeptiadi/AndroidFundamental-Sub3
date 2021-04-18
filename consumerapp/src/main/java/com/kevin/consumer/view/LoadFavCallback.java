package com.kevin.consumer.view;

import com.kevin.consumer.model.FavoriteModel;

import java.util.ArrayList;

interface LoadFavCallback {
    void preExecute();
    void postExecute(ArrayList<FavoriteModel> favMod);
}
