package com.kevin.provider.data.local;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    public static final String AUTHORITY = "com.kevin.provider";
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
