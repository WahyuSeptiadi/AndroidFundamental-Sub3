package com.kevin.consumer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.squareup.picasso.Picasso;
import com.kevin.consumer.R;
import com.kevin.consumer.view.fragmentTabLayout.FollowersFragment;
import com.kevin.consumer.view.fragmentTabLayout.FollowingFragment;
import com.kevin.consumer.viewModel.DetailViewModel;
import com.kevin.consumer.constant.Base;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;

public class DetailActivity extends AppCompatActivity {

    private DetailViewModel detailViewModel;
    private CircleImageView imgAvatarDetail;
    private TextView username, name, repository, follower, following, company, location;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static boolean count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //setComponent
        ImageView btnBack = findViewById(R.id.btnBack);
        imgAvatarDetail = findViewById(R.id.imgAvatar);
        username = findViewById(R.id.usernameValue);
        name = findViewById(R.id.nameValue);
        repository = findViewById(R.id.repositoryValue);
        follower = findViewById(R.id.followerValue);
        following = findViewById(R.id.followingValue);
        company = findViewById(R.id.companyValue);
        location = findViewById(R.id.locationValue);

        // for tabLayout
        mContext = getBaseContext();

        SectionPagerAdapter mSection = new SectionPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.vp_profile);
        mViewPager.setAdapter(mSection);

        TabLayout mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        String username = getIntent().getStringExtra(Base.DATA_KEY);
        detailViewModel = new ViewModelProvider(this,
                          new ViewModelProvider.NewInstanceFactory()).get(DetailViewModel.class);
        detailViewModel.setDetailUser(username);


        if (checkInternet()){
            if (count){
                Snacky.builder()
                        .setView(mViewPager)
                        .setIcon(R.drawable.ic_signal_on)
                        .centerText()
                        .setText(getResources().getString(R.string.msg_internet_on))
                        .setDuration(Snacky.LENGTH_LONG)
                        .success().show();
                getData();
                count = false;
            }else{
                getData();
            }
        }else{
            Snacky.builder()
                    .setView(mViewPager)
                    .setIcon(R.drawable.ic_signal_off)
                    .centerText()
                    .setText(getResources().getString(R.string.msg_internet_off))
                    .setDuration(Snacky.LENGTH_LONG)
                    .error().show();
            count = true;
        }

        btnBack.setOnClickListener(v -> {
            Intent toFav = new Intent(DetailActivity.this, FavoriteActivity.class);
            startActivity(toFav);

            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent toFav = new Intent(this, FavoriteActivity.class);
        startActivity(toFav);
    }

    private void getData() {
        detailViewModel.getDetailUser().observe(this, git_user -> {
            Picasso.get().load(git_user.getAvatar_url()).into(imgAvatarDetail);
            username.setText(git_user.getLogin());
            repository.setText(String.valueOf(git_user.getPublic_repos()));
            follower.setText(String.valueOf(git_user.getFollowers()));
            following.setText(String.valueOf(git_user.getFollowing()));

            if(git_user.getName() == null){
                name.setText(R.string.str_null);
            }else{
                name.setText(git_user.getName());
            }

            if(git_user.getLocation() == null && git_user.getCompany() == null){
                location.setText(R.string.str_null);
                company.setText(R.string.str_null);
            }else if (git_user.getCompany() == null || git_user.getLocation() == null){
                if (git_user.getCompany() == null){
                    company.setText(R.string.str_null);
                    location.setText(git_user.getLocation());
                }else if (git_user.getLocation() == null){
                    location.setText(R.string.str_null);
                    company.setText(git_user.getCompany());
                }
            }else{
                company.setText(git_user.getCompany());
                location.setText(git_user.getLocation());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.tablayout_detail, menu);
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
            switch (position){
                case 0 :
                    fragment = new FollowersFragment();
                    break;
                case 1 :
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
            switch (position){
                case 0 :
                    return mContext.getResources().getString(R.string.tab_followers);
                case 1 :
                    return mContext.getResources().getString(R.string.tab_following);
            }
            return null;
        }
    }

    public boolean checkInternet(){
        boolean connectStatus;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();

        return connectStatus;
    }
}