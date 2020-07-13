package com.wahyu.consumerapp.localDatabase.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wahyu_septiadi on 03, July 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class DatabaseContract {

    public static final String AUTHORITY = "com.wahyu.githubapi";
    private static final String SCHEME = "content";

    public DatabaseContract() {
    }

    public static final String TABLE_NAME = "favorite";
    public static final class FavColumns implements BaseColumns {
        public static final String AVATAR = "avatar";
        public static final String USERNAME = "username";
        public static final String TYPEUSER = "typeuser";
        public static final String IDUSER = "iduser";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();
    }
}
