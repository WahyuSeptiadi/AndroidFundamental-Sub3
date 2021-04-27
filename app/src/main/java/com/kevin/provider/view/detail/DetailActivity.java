package com.kevin.provider.view.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kevin.provider.R;
import com.kevin.provider.databinding.ActivityDetailBinding;
import com.kevin.provider.helper.BaseConst;
import com.kevin.provider.view.follower.FollowersFragment;
import com.kevin.provider.view.following.FollowingFragment;
import com.kevin.provider.view.search.SearchActivity;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private DetailViewModel detailViewModel;

    private ActivityDetailBinding binding;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = getBaseContext();

        SectionPagerAdapter mSection = new SectionPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.vp_profile);
        mViewPager.setAdapter(mSection);

        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        String username = getIntent().getStringExtra(BaseConst.DATA_KEY);
        detailViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory())
                .get(DetailViewModel.class);
        detailViewModel.setDetailUser(username);

        getData();

        binding.imgBtnBack.setOnClickListener(v -> {
            startActivity(new Intent(DetailActivity.this, SearchActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SearchActivity.class));
    }

    private void getData() {
        detailViewModel.getDetailUser().observe(this, git_user -> {
            Picasso.get().load(git_user.getAvatar_url()).into(binding.civImgAvatar);
            binding.txtUsernameValue.setText(git_user.getLogin());
            binding.txtRepositoryValue.setText(String.valueOf(git_user.getPublic_repos()));
            binding.txtFollowerValue.setText(String.valueOf(git_user.getFollowers()));
            binding.txtFollowingValue.setText(String.valueOf(git_user.getFollowing()));

            if (git_user.getName() == null) {
                binding.txtNameValue.setText(R.string.string_not_set);
            } else {
                binding.txtNameValue.setText(git_user.getName());
            }

            if (git_user.getLocation() != null && git_user.getCompany() != null) {
                binding.txtCompanyValue.setText(git_user.getCompany());
                binding.txtLocationValue.setText(git_user.getLocation());
            } else {
                if (git_user.getCompany() == null) {
                    binding.txtCompanyValue.setText(R.string.string_not_set);
                    binding.txtLocationValue.setText(git_user.getLocation());
                } else if (git_user.getLocation() == null) {
                    binding.txtLocationValue.setText(R.string.string_not_set);
                    binding.txtCompanyValue.setText(git_user.getCompany());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tab_layout_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new FollowersFragment();
                    break;
                case 1:
                    fragment = new FollowingFragment();
                    break;
            }
            assert fragment != null;
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getResources().getString(R.string.tab_followers);
                case 1:
                    return mContext.getResources().getString(R.string.tab_following);
            }
            return null;
        }
    }
}