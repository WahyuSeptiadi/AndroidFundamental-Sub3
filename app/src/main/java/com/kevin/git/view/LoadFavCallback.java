package com.kevin.git.view;

import com.kevin.git.model.FavoriteModel;

import java.util.ArrayList;

interface LoadFavCallback {
    void preExecute();
    void postExecute(ArrayList<FavoriteModel> favMod);
}
