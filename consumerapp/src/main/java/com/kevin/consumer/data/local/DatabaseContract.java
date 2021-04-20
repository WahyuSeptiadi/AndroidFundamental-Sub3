package com.kevin.consumer.data.local;

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
        public static final String USERNAME = "user_name";
        public static final String TYPE_USER = "user_type";
        public static final String ID_USER = "user_id";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();
    }
}
