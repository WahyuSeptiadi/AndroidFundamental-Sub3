package com.kevin.consumer.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kevin.consumer.R;
import com.kevin.consumer.viewModel.SearchViewModel;
import com.kevin.consumer.adapter.DetailListAdapter;

public class SearchActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private DetailListAdapter adapter;
    private EditText et_username;
    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;

    //buat cek
    TextView message;
    private ImageView imgLanguage, imgSettings, imgReminder;
    private static boolean count;

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
        FloatingActionButton floatingActionButton=findViewById(R.id.fab_favorite);

        setRecyclerView();

        searchViewModel = new ViewModelProvider(this,
                          new ViewModelProvider.NewInstanceFactory()).get(SearchViewModel.class);

        if (savedInstanceState != null){
            String data = savedInstanceState.getString("key");

            searchViewModel.setSearchData(data);
            getData();
            progressBar.setVisibility(View.GONE);
        }

        floatingActionButton.setOnClickListener(view -> {
            Intent toFavorite = new Intent(SearchActivity.this, FavoriteActivity.class);
            startActivity(toFavorite);
        });

        btnSearch.setOnClickListener(v -> {
            if (checkInternet()){
                if (count){
                    Snackbar.make(recyclerView, getResources().getString(R.string.msg_internet_on), Snackbar.LENGTH_SHORT).show();
                    searchData();
                    count = false;
                }else {
                    searchData();
                }
            }else{
                Snackbar.make(recyclerView, getResources().getString(R.string.msg_internet_off), Snackbar.LENGTH_SHORT).show();
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
        if (checkInternet()){
            getData();
            progressBar.setVisibility(View.GONE);
        }else{
            Snackbar.make(recyclerView, getResources().getString(R.string.msg_internet_off), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void searchData(){
        if (TextUtils.isEmpty(et_username.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.toast_enterkey), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else{
            Toast.makeText(this, getResources().getString(R.string.toast_searching), Toast.LENGTH_SHORT).show();
            searchViewModel.setSearchData(et_username.getText().toString());
            getData();
        }
    }

    private void getData(){
        progressBar.setVisibility(View.VISIBLE);
        message.setVisibility(View.INVISIBLE);
        searchViewModel.getSearchData().observe(this, git_user -> {
            if (git_user.getTotal_count() > 0){
                adapter.setData(git_user.getItems());

                recyclerView.setAdapter(adapter);

                message.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
            }else{
                adapter.clearList(git_user.getItems());

                progressBar.setVisibility(View.GONE);
                message.setText(R.string.str_message);
                message.setVisibility(View.VISIBLE);
            }
            et_username.setText("");
        });
    }

    private void setRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.smoothScrollToPosition(0);
        adapter = new DetailListAdapter(SearchActivity.this);
        adapter.notifyDataSetChanged();
    }

    public boolean checkInternet(){
        boolean connectStatus;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();

        return connectStatus;
    }
}