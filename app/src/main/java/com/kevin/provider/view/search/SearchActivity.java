package com.kevin.provider.view.search;

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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kevin.provider.R;
import com.kevin.provider.view.favorite.FavoriteActivity;
import com.kevin.provider.view.setting.SetReminderActivity;

import de.mateware.snacky.Snacky;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchListAdapter adapter;
    private EditText etUsername;
    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;

    private TextView message;
    private ImageView imgLanguage, imgSettings, imgReminder;
    private static boolean count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.rv_search);
        etUsername = findViewById(R.id.editTextSearch);
        message = findViewById(R.id.tv_message);

        imgLanguage = findViewById(R.id.imgLanguage);
        imgReminder = findViewById(R.id.imgReminder);
        imgSettings = findViewById(R.id.imgSetting);

        progressBar = findViewById(R.id.progress_circular);
        progressBar.setProgress(0);
        FloatingActionButton fab = findViewById(R.id.fab_favorite);

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
            getDataUser();
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
                }
                if (!recyclerView.canScrollVertically(1)) {
                    fab.hide();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        etUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String username = etUsername.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Snacky.builder()
                            .setView(recyclerView)
                            .centerText()
                            .setText(getResources().getString(R.string.toast_enter_key))
                            .setDuration(Snacky.LENGTH_LONG)
                            .warning().show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    searchViewModel.setSearchData(username);
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
                }

                hideSoftKeyboard();
                return true;
            }
            return false;
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
        outState.putString("key", etUsername.getText().toString());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (checkInternet()) {
            getDataUser();
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

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchData() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            Snacky.builder()
                    .setView(recyclerView)
                    .centerText()
                    .setText(getResources().getString(R.string.toast_enter_key))
                    .setDuration(Snacky.LENGTH_LONG)
                    .warning().show();
            progressBar.setVisibility(View.GONE);
        } else {
            Snacky.builder()
                    .setView(recyclerView)
                    .centerText()
                    .setText(getResources().getString(R.string.toast_searching))
                    .setDuration(Snacky.LENGTH_LONG)
                    .info().show();
            searchViewModel.setSearchData(etUsername.getText().toString());
            getDataUser();
        }
    }

    private void getDataUser() {
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
            etUsername.setText("");
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.smoothScrollToPosition(0);
        adapter = new SearchListAdapter(SearchActivity.this);
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