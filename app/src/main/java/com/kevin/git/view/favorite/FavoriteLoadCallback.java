package com.kevin.git.view.favorite;

import com.kevin.git.data.local.FavoriteModel;

import java.util.ArrayList;

interface FavoriteLoadCallback {
    void preExecute();
    void postExecute(ArrayList<FavoriteModel> favMod);
}
