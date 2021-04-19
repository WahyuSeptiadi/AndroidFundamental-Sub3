package com.kevin.consumer.view.favorite;

import com.kevin.consumer.data.local.FavoriteModel;

import java.util.ArrayList;

interface LoadFavCallback {
    void preExecute();

    void postExecute(ArrayList<FavoriteModel> favMod);
}
