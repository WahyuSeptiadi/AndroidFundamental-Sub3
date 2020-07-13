package com.wahyu.githubapi.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wahyu.githubapi.R;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * Created by wahyu_septiadi on 30, June 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(SearchActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#2196F3"))
                .withHeaderText("")
                .withFooterText(getApplication().getResources().getString(R.string.copyright))
                .withBeforeLogoText("")
                .withAfterLogoText(getApplication().getResources().getString(R.string.app_name))
                .withLogo(R.drawable.github);

        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}

