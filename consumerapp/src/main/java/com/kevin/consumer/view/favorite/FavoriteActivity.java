package com.kevin.consumer.view.favorite;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.consumer.R;
import com.kevin.consumer.data.local.DatabaseContract;
import com.kevin.consumer.data.local.FavoriteModel;
import com.kevin.consumer.data.local.MappingHelper;
import com.kevin.consumer.view.setting.SettingsActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.mateware.snacky.Snacky;

public class FavoriteActivity extends AppCompatActivity implements LoadFavCallback, View.OnClickListener {

    private FavoriteListAdapter favoriteListAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private static final String EXTRA_STATE = "EXTRA STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        progressBar = findViewById(R.id.progress_favorite);
        ImageView imgSettings = findViewById(R.id.img_setting);

        recyclerView = findViewById(R.id.rv_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        favoriteListAdapter = new FavoriteListAdapter(this);
        recyclerView.setAdapter(favoriteListAdapter);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.FavColumns.CONTENT_URI, true, myObserver);

        if (savedInstanceState == null) {
            new LoadFavAsync(this, this).execute();
        } else {
            ArrayList<FavoriteModel> favoriteModelArrayList = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (favoriteModelArrayList != null) {
                favoriteListAdapter.setFavoriteModelArrayList(favoriteModelArrayList);
            }
            progressBar.setVisibility(View.GONE);
        }

        imgSettings.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, favoriteListAdapter.getFavoriteModelArrayList());
    }

    @Override
    public void preExecute() {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    }

    @Override
    public void postExecute(ArrayList<FavoriteModel> favMod) {
        progressBar.setVisibility(View.GONE);

        if (favMod.size() > 0) {
            favoriteListAdapter.setFavoriteModelArrayList(favMod);
        } else {
            favoriteListAdapter.setFavoriteModelArrayList(new ArrayList<>());
            Snacky.builder()
                    .setView(recyclerView)
                    .centerText()
                    .setText(getResources().getString(R.string.not_yet))
                    .setDuration(Snacky.LENGTH_LONG)
                    .info().show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    private static class LoadFavAsync extends AsyncTask<Void, Void, ArrayList<FavoriteModel>> {

        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavCallback> weakCallback;

        @SuppressWarnings("deprecation")
        private LoadFavAsync(Context context, LoadFavCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<FavoriteModel> doInBackground(Void... voids) {

            Context context = weakContext.get();
            Cursor dataCursor = context.getContentResolver().query(DatabaseContract.FavColumns.CONTENT_URI, null, null, null, null);
            assert dataCursor != null;

            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<FavoriteModel> favoriteModels) {
            super.onPostExecute(favoriteModels);
            weakCallback.get().postExecute(favoriteModels);
        }
    }

    public static class DataObserver extends ContentObserver {
        final Context context;

        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadFavAsync(context, (LoadFavCallback) context).execute();
        }
    }
}