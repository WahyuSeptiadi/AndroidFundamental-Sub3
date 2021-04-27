package com.kevin.provider.view.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.kevin.provider.view.setting.SettingsActivity;

import de.mateware.snacky.Snacky;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private SearchListAdapter adapter;
    private EditText etUsername;
    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;

    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.rv_search);
        etUsername = findViewById(R.id.et_search);
        message = findViewById(R.id.tv_message);

        ImageView imgSettings = findViewById(R.id.img_setting);

        progressBar = findViewById(R.id.progress_circular);
        FloatingActionButton fab = findViewById(R.id.fab_favorite);

        setRecyclerView();

        searchViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(SearchViewModel.class);

        if (savedInstanceState != null) {
            String data = savedInstanceState.getString("key");

            searchViewModel.setSearchData(data);
            getDataUser();
            progressBar.setVisibility(View.GONE);
        }

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

                if (!TextUtils.isEmpty(username)) {
                    searchViewModel.setSearchData(username);
                    searchData();
                } else {
                    Snacky.builder()
                            .setView(recyclerView)
                            .centerText()
                            .setText(getResources().getString(R.string.toast_enter_key))
                            .setDuration(Snacky.LENGTH_LONG)
                            .warning().show();
                    progressBar.setVisibility(View.GONE);
                }

                hideSoftKeyboard();
                return true;
            }
            return false;
        });

        imgSettings.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", etUsername.getText().toString());
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
                message.setText(R.string.string_not_found);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (view.getId() == R.id.fab_favorite) {
            startActivity(new Intent(SearchActivity.this, FavoriteActivity.class));
        }
    }
}