package com.kevin.provider.view.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.provider.R;
import com.kevin.provider.databinding.ActivitySearchBinding;
import com.kevin.provider.view.favorite.FavoriteActivity;
import com.kevin.provider.view.setting.SettingsActivity;

import de.mateware.snacky.Snacky;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private SearchListAdapter adapter;
    private SearchViewModel searchViewModel;

    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setRecyclerView();

        searchViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory())
                .get(SearchViewModel.class);

        if (savedInstanceState != null) {
            String data = savedInstanceState.getString("key");

            searchViewModel.setSearchData(data);
            getDataUser();
            binding.progressCircular.setVisibility(View.GONE);
        }

        binding.rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && binding.fabFavorite.isShown()) {
                    binding.fabFavorite.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fabFavorite.show();
                }
                if (!recyclerView.canScrollVertically(1)) {
                    binding.fabFavorite.hide();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String username = binding.etSearch.getText().toString();

                if (!TextUtils.isEmpty(username)) {
                    searchViewModel.setSearchData(username);
                    searchData();
                } else {
                    Snacky.builder()
                            .setView(v)
                            .centerText()
                            .setText(getResources().getString(R.string.toast_enter_key))
                            .setDuration(Snacky.LENGTH_LONG)
                            .warning().show();
                    binding.progressCircular.setVisibility(View.GONE);
                }

                hideSoftKeyboard();
                return true;
            }
            return false;
        });

        binding.imgSetting.setOnClickListener(this);
        binding.fabFavorite.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", binding.etSearch.getText().toString());
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
        if (TextUtils.isEmpty(binding.etSearch.getText().toString())) {
            Snacky.builder()
                    .setView(binding.rvSearch)
                    .centerText()
                    .setText(getResources().getString(R.string.toast_enter_key))
                    .setDuration(Snacky.LENGTH_LONG)
                    .warning().show();
            binding.progressCircular.setVisibility(View.GONE);
        } else {
            searchViewModel.setSearchData(binding.etSearch.getText().toString());
            getDataUser();
        }
    }

    private void getDataUser() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.tvMessage.setVisibility(View.INVISIBLE);
        searchViewModel.getSearchData().observe(this, git_user -> {
            if (git_user.getTotal_count() > 0) {
                adapter.setData(git_user.getItems());

                binding.rvSearch.setAdapter(adapter);

                binding.tvMessage.setVisibility(View.INVISIBLE);
                binding.progressCircular.setVisibility(View.GONE);
            } else {
                adapter.clearList(git_user.getItems());

                binding.progressCircular.setVisibility(View.GONE);
                binding.tvMessage.setText(R.string.string_not_found);
                binding.tvMessage.setVisibility(View.VISIBLE);
            }
            binding.etSearch.setText("");
        });
    }

    private void setRecyclerView() {
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setHasFixedSize(true);
        binding.rvSearch.smoothScrollToPosition(0);
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