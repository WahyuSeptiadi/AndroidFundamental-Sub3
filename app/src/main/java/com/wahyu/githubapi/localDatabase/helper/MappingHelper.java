package com.wahyu.githubapi.localDatabase.helper;

import android.database.Cursor;

import com.wahyu.githubapi.model.FavoriteModel;
import com.wahyu.githubapi.localDatabase.contract.DatabaseContract;

import java.util.ArrayList;

/**
 * Created by wahyu_septiadi on 05, July 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class MappingHelper {

    public static ArrayList<FavoriteModel> mapCursorToArrayList(Cursor dataCursor) {
        ArrayList<FavoriteModel> favoriteModelArrayList = new ArrayList<>();
        while(dataCursor.moveToNext()){
            int id = dataCursor.getInt(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns._ID));
            String avatar = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR));
            String username = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME));
            String typeuser = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TYPEUSER));
            String iduser = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.IDUSER));
            favoriteModelArrayList.add(new FavoriteModel(id, avatar, username, typeuser, iduser));
        }
        return favoriteModelArrayList;
    }
}
