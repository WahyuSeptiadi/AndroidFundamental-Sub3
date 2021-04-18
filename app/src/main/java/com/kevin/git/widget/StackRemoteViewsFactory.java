package com.kevin.git.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.kevin.git.model.FavoriteModel;
import com.kevin.git.R;
import com.kevin.git.localDatabase.helper.MappingHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kevin.git.localDatabase.contract.DatabaseContract.FavColumns.CONTENT_URI;

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final List<Bitmap> mWidgetItems = new ArrayList<>();
    private final Context mContext;
    private Cursor cursor;

    StackRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null){
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();

        cursor = mContext.getContentResolver().query(CONTENT_URI, null, null, null, null);
        ArrayList<FavoriteModel> list = MappingHelper.mapCursorToArrayList(Objects.requireNonNull(cursor));

        for (FavoriteModel favlist : list) {
            mWidgetItems.add(getBitmapFromURL(favlist.getAvatar()));
        }
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems.get(position));

        Bundle extras = new Bundle();
        extras.putInt(ImageFavoriteWidget.EXTRA_ITEM, position);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
