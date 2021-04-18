package com.kevin.provider.view.favorite;

import com.kevin.provider.data.local.FavoriteModel;

import java.util.ArrayList;

interface FavoriteLoadCallback {
    void preExecute();

    void postExecute(ArrayList<FavoriteModel> favMod);
}
