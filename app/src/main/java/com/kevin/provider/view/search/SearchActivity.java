package com.kevin.provider.view.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kevin.provider.R;
import com.kevin.provider.view.favorite.FavoriteActivity;
import com.kevin.provider.view.follower.FollowersListAdapter;
import com.kevin.provider.view.setting.SetReminderActivity;

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FollowersListAdapter adapter;
    private EditText et_username;
    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;
    private ConstraintLayout searching;

    private TextView message;
    private ImageView imgLanguage, imgSettings, imgReminder;
    private static boolean count;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.rv_search);
        ImageView btnSearch = findViewById(R.id.btnCari);
        et_username = findViewById(R.id.editTextSearch);
        message = findViewById(R.id.tv_message);

        imgLanguage = findViewById(R.id.imgLanguage);
        imgReminder = findViewById(R.id.imgReminder);
        imgSettings = findViewById(R.id.imgSetting);

        progressBar = findViewById(R.id.progress_circular);
        progressBar.setProgress(0);
        FloatingActionButton fab = findViewById(R.id.fab_favorite);
        searching = findViewById(R.id.searching);

        setRecyclerView();

        //set ANIMATION
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        shake.setDuration(1000);
        imgSettings.setAnimation(shake);

        searchViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(SearchViewModel.class);

        if (savedInstanceState != null) {
            String data = savedInstanceState.getString("key");

            searchViewModel.setSearchData(data);
            getData();
            progressBar.setVisibility(View.GONE);
        }

        fab.setOnClickListener(view -> {
            Intent toFavorite = new Intent(SearchActivity.this, FavoriteActivity.class);
            startActivity(toFavorite);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                    searching.setVisibility(View.GONE);
                }
                // cek if rv in bottom last list
                if (!recyclerView.canScrollVertically(1)) {
                    fab.hide();
                    searching.setVisibility(View.VISIBLE);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        btnSearch.setOnClickListener(v -> {
            if (checkInternet()) {
                if (count) {
                    Snacky.builder()
                            .setView(recyclerView)
                            .setIcon(R.drawable.ic_signal_on)
                            .centerText()
                            .setText(getResources().getString(R.string.msg_internet_on))
                            .setDuration(Snacky.LENGTH_LONG)
                            .success().show();
                    count = false;
                } else {
                    searchData();
                }
            } else {
                Snacky.builder()
                        .setView(recyclerView)
                        .setIcon(R.drawable.ic_signal_off)
                        .centerText()
                        .setText(getResources().getString(R.string.msg_internet_off))
                        .setDuration(Snacky.LENGTH_LONG)
                        .error().show();
                count = true;
            }

            // autohide after search keyword
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        imgLanguage.setOnClickListener(v -> {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
            imgReminder.setVisibility(View.INVISIBLE);
            imgLanguage.setVisibility(View.INVISIBLE);
            imgSettings.setVisibility(View.VISIBLE);
        });

        imgReminder.setOnClickListener(v -> {
            Intent toReminder = new Intent(SearchActivity.this, SetReminderActivity.class);
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
        outState.putString("key", et_username.getText().toString());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (checkInternet()) {
            getData();
            progressBar.setVisibility(View.GONE);
        } else {
            Snacky.builder()
                    .setView(recyclerView)
                    .setIcon(R.drawable.ic_signal_on)
                    .centerText()
                    .setText(getResources().getString(R.string.msg_internet_off))
                    .setDuration(Snacky.LENGTH_LONG)
                    .error().show();
        }
    }

    private void searchData() {
        if (TextUtils.isEmpty(et_username.getText().toString())) {
            Toasty.error(this, getResources().getString(R.string.toast_enter_key), Toast.LENGTH_SHORT, true).show();
            progressBar.setVisibility(View.GONE);
        } else {
            Toasty.info(this, getResources().getString(R.string.toast_searching), Toast.LENGTH_SHORT, true).show();
            searchViewModel.setSearchData(et_username.getText().toString());
            getData();
        }
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        message.setVisibility(View.INVISIBLE);
        searchViewModel.getSearchData().observe(this, git_user -> {
            if (git_user.getTotal_count() > 0) {
                adapter.setData(git_user.getItems());

                recyclerView.setAdapter(adapter);

                message.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                adapter.clearList(git_user.getItems());

                progressBar.setVisibility(View.GONE);
                message.setText(R.string.str_message);
                message.setVisibility(View.VISIBLE);
            }
            et_username.setText("");
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.smoothScrollToPosition(0);
        adapter = new FollowersListAdapter(SearchActivity.this);
        adapter.notifyDataSetChanged();
    }

    public boolean checkInternet() {
        boolean connectStatus;
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();

        return connectStatus;
    }

}