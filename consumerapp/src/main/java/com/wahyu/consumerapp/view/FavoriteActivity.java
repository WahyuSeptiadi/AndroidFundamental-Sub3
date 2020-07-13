package com.wahyu.consumerapp.view;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wahyu.consumerapp.model.FavoriteModel;
import com.wahyu.consumerapp.R;
import com.wahyu.consumerapp.adapter.FavoriteListAdapter;
import com.wahyu.consumerapp.localDatabase.contract.DatabaseContract;
import com.wahyu.consumerapp.localDatabase.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.mateware.snacky.Snacky;

public class FavoriteActivity extends AppCompatActivity implements LoadFavCallback {

    private FavoriteListAdapter favoriteListAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private static final String EXTRA_STATE = "EXTRA STATE";
    private ImageView imgLanguage, imgSettings, imgReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        progressBar = findViewById(R.id.progress_favorite);

        imgLanguage = findViewById(R.id.imgLanguage);
        imgReminder = findViewById(R.id.imgReminder);
        imgSettings = findViewById(R.id.imgSetting);

        // set Recyclerview
        recyclerView = findViewById(R.id.rv_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        favoriteListAdapter = new FavoriteListAdapter(this);
        recyclerView.setAdapter(favoriteListAdapter);

        //set ANIMATION
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        shake.setDuration(1000);
        imgSettings.setAnimation(shake);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.FavColumns.CONTENT_URI, true, myObserver);

        // Safe OrientationState
        if (savedInstanceState == null){
            new LoadFavAsync(this, this).execute();
        }else {
            ArrayList<FavoriteModel> favoriteModelArrayList = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (favoriteModelArrayList != null){
                favoriteListAdapter.setFavoriteModelArrayList(favoriteModelArrayList);
            }
            progressBar.setVisibility(View.GONE);
        }

        //SETTING
        imgLanguage.setOnClickListener(v -> {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
            imgReminder.setVisibility(View.INVISIBLE);
            imgLanguage.setVisibility(View.INVISIBLE);
            imgSettings.setVisibility(View.VISIBLE);
        });

        imgReminder.setOnClickListener(v -> {
            Intent toReminder = new Intent(FavoriteActivity.this, SetReminderActivity.class);
            startActivity(toReminder);
            imgLanguage.setVisibility(View.INVISIBLE);
            imgReminder.setVisibility(View.INVISIBLE);
            imgSettings.setVisibility(View.VISIBLE);
        });

        imgSettings.setOnClickListener(view -> {
            imgLanguage.setVisibility(View.VISIBLE);
            imgReminder.setVisibility(View.VISIBLE);
            imgSettings.setVisibility(View.INVISIBLE);
        });

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
        //setProgressbar GONE, setelah selesai di load
        progressBar.setVisibility(View.GONE);

        if (favMod.size() > 0){
            favoriteListAdapter.setFavoriteModelArrayList(favMod);
        }else {
            favoriteListAdapter.setFavoriteModelArrayList(new ArrayList<>());
            Snacky.builder()
                    .setView(recyclerView)
                    .centerText()
                    .setText(getResources().getString(R.string.not_yet))
                    .setDuration(Snacky.LENGTH_LONG)
                    .info().show();
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