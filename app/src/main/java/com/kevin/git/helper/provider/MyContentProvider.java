package com.kevin.git.helper.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.kevin.git.data.local.FavoriteHelper;

import java.util.Objects;

import static com.kevin.git.data.local.DatabaseContract.AUTHORITY;
import static com.kevin.git.data.local.DatabaseContract.FavColumns.CONTENT_URI;
import static com.kevin.git.data.local.DatabaseContract.TABLE_NAME;

public class MyContentProvider extends ContentProvider {
    private static final int FAV = 1; //mengambil semua data
    private static final int FAV_ID = 2; // mengambil data sesuai id

    private FavoriteHelper favoriteHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // content://com.kevin.git/favorite BUAT NAMBAH DATA
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAV);
        // content://com.kevin.git/favorite/id BUAT HAPUS DATA byId (FAV_ID)
        sUriMatcher.addURI(AUTHORITY,
                TABLE_NAME + "/#",
                FAV_ID);
    }

    @Override
    public boolean onCreate() {
        favoriteHelper = FavoriteHelper.getInstance(getContext());
        favoriteHelper.open();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            // content://com.kevin.git/favorite
            case FAV :
                cursor = favoriteHelper.queryAll();
                break;
            // content://com.kevin.git/favorite/id ---> cuma diambil id nya
            case FAV_ID :
                cursor = favoriteHelper.queryById(selection);
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deleted;
        if (sUriMatcher.match(uri) == FAV) {
            deleted = 0;
        } else {
            deleted = favoriteHelper.deleteByIdUser(uri.getLastPathSegment());
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(CONTENT_URI, null);

        return deleted;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long added;
        if (sUriMatcher.match(uri) == FAV) {
            added = favoriteHelper.insert(values);
        } else {
            added = 0;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" + added);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updated;
        if (sUriMatcher.match(uri) == FAV) {
            updated = favoriteHelper.update(uri.getLastPathSegment(), values);
        } else {
            updated = 0;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(CONTENT_URI, null);

        return updated;
    }
}
