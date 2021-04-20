package com.kevin.provider.data.local;

import android.database.Cursor;

import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<FavoriteModel> mapCursorToArrayList(Cursor dataCursor) {
        ArrayList<FavoriteModel> favoriteModelArrayList = new ArrayList<>();
        while (dataCursor.moveToNext()) {
            int id = dataCursor.getInt(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns._ID));
            String avatar = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR));
            String username = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME));
            String userType = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.TYPE_USER));
            String userId = dataCursor.getString(dataCursor.getColumnIndexOrThrow(DatabaseContract.FavColumns.ID_USER));
            favoriteModelArrayList.add(new FavoriteModel(id, avatar, username, userType, userId));
        }
        return favoriteModelArrayList;
    }
}
