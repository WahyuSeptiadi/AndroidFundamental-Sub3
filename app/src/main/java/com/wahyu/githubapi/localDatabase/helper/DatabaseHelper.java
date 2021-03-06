package com.wahyu.githubapi.localDatabase.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wahyu.githubapi.localDatabase.contract.DatabaseContract;

/**
 * Created by wahyu_septiadi on 03, July 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dbfavorite";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_FAVORITE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT UNIQUE)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.FavColumns._ID,
            DatabaseContract.FavColumns.AVATAR,
            DatabaseContract.FavColumns.USERNAME,
            DatabaseContract.FavColumns.TYPEUSER,
            DatabaseContract.FavColumns.IDUSER
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}