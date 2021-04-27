package com.kevin.consumer.view.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kevin.consumer.R;
import com.kevin.consumer.helper.BaseConst;
import com.kevin.consumer.view.favorite.FavoriteActivity;
import com.kevin.consumer.view.follower.FollowersFragment;
import com.kevin.consumer.view.following.FollowingFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    private DetailViewModel detailViewModel;
    private CircleImageView imgAvatarDetail;
    private TextView username, name, repository, follower, following, company, location;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView btnBack = findViewById(R.id.img_btn_back);
        imgAvatarDetail = findViewById(R.id.img_civ_avatar);
        username = findViewById(R.id.txt_username_value);
        name = findViewById(R.id.txt_name_value);
        repository = findViewById(R.id.txt_repository_value);
        follower = findViewById(R.id.txt_follower_value);
        following = findViewById(R.id.txt_following_value);
        company = findViewById(R.id.txt_company_value);
        location = findViewById(R.id.txt_location_value);

        mContext = getBaseContext();

        SectionPagerAdapter mSection = new SectionPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.vp_profile);
        mViewPager.setAdapter(mSection);

        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        String username = getIntent().getStringExtra(BaseConst.DATA_KEY);
        detailViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(DetailViewModel.class);
        detailViewModel.setDetailUser(username);

        getData();

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(DetailActivity.this, FavoriteActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FavoriteActivity.class));
    }

    private void getData() {
        detailViewModel.getDetailUser().observe(this, git_user -> {
            Picasso.get().load(git_user.getAvatar_url()).into(imgAvatarDetail);
            username.setText(git_user.getLogin());
            repository.setText(String.valueOf(git_user.getPublic_repos()));
            follower.setText(String.valueOf(git_user.getFollowers()));
            following.setText(String.valueOf(git_user.getFollowing()));

            if (git_user.getName() == null) {
                name.setText(R.string.string_not_set);
            } else {
                name.setText(git_user.getName());
            }

            if (git_user.getLocation() == null && git_user.getCompany() == null) {
                location.setText(R.string.string_not_set);
                company.setText(R.string.string_not_set);
            } else if (git_user.getCompany() == null || git_user.getLocation() == null) {
                if (git_user.getCompany() == null) {
                    company.setText(R.string.string_not_set);
                    location.setText(git_user.getLocation());
                } else if (git_user.getLocation() == null) {
                    location.setText(R.string.string_not_set);
                    company.setText(git_user.getCompany());
                }
            } else {
                company.setText(git_user.getCompany());
                location.setText(git_user.getLocation());
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