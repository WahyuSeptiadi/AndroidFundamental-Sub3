package com.kevin.consumer.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.consumer.R;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(FavoriteActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#373D45"))
                .withHeaderText("")
                .withFooterText(getApplication().getResources().getString(R.string.copyright))
                .withBeforeLogoText("")
                .withAfterLogoText(getApplication().getResources().getString(R.string.app_name))
                .withLogo(R.drawable.gitlab);

        config.getHeaderTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}

