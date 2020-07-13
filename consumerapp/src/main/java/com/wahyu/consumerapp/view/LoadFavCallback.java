package com.wahyu.consumerapp.view;

import com.wahyu.consumerapp.model.FavoriteModel;

import java.util.ArrayList;

/**
 * Created by wahyu_septiadi on 05, July 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

interface LoadFavCallback {
    void preExecute();
    void postExecute(ArrayList<FavoriteModel> favMod);
}
