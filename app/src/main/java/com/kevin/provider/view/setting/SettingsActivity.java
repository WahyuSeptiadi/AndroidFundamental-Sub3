package com.kevin.provider.view.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.provider.R;
import com.kevin.provider.helper.alarm.AlarmReceiver;
import com.kevin.provider.view.search.SearchActivity;

import de.mateware.snacky.Snacky;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etMessageTime;
    private ImageView imgSetON, imgSetOFF;
    private AlarmReceiver alarmReceiver;
    private TextView txtTimeAlarm;

    private final String DEFAULT_ALARM_TIME = "09:00";
    private final String MESSAGE_RESET = "";
    private String repeatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgSetON = findViewById(R.id.img_set_reminder_on);
        imgSetOFF = findViewById(R.id.img_set_reminder_off);
        etMessageTime = findViewById(R.id.et_message_time);
        ImageView btnBack = findViewById(R.id.img_btn_back);
        RelativeLayout setMessage = findViewById(R.id.relative_set_message);
        TextView txtSetLanguage = findViewById(R.id.txt_setting_language);
        txtTimeAlarm = findViewById(R.id.txt_default_time);

        imgSetON.setOnClickListener(this);
        imgSetOFF.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        txtSetLanguage.setOnClickListener(this);

        getAlarmPref();
        alarmReceiver = new AlarmReceiver();

        Animation slide_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_left.setDuration(2000);
        setMessage.setAnimation(slide_left);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_setting_language) {
            startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
        } else if (view.getId() == R.id.img_set_reminder_on) {
            repeatMessage = etMessageTime.getText().toString();

            if (TextUtils.isEmpty(repeatMessage)) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getString(R.string.input_message))
                        .setDuration(Snacky.LENGTH_LONG)
                        .error().show();
            } else {
                if (txtTimeAlarm.getText().equals(DEFAULT_ALARM_TIME)) {
                    alarmReceiver.setRepeatingAlarm(this, DEFAULT_ALARM_TIME, repeatMessage);
                    setAlarmPref(DEFAULT_ALARM_TIME, repeatMessage, true);
                }

                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getText(R.string.toast_repeat))
                        .setDuration(Snacky.LENGTH_LONG)
                        .success().show();
            }
        } else if (view.getId() == R.id.img_set_reminder_off) {
            etMessageTime.setText(MESSAGE_RESET);

            alarmReceiver.cancelAlarm(this);
            setAlarmPref(DEFAULT_ALARM_TIME, repeatMessage, false);
            txtTimeAlarm.setText(DEFAULT_ALARM_TIME);

            Snacky.builder()
                    .setView(view)
                    .centerText()
                    .setText(getResources().getText(R.string.toast_cancel))
                    .setDuration(Snacky.LENGTH_LONG)
                    .info().show();
        } else if (view.getId() == R.id.img_btn_back) {
            startActivity(new Intent(SettingsActivity.this, SearchActivity.class));
            finish();
        }
    }

    public void setAlarmPref(String time, String message, boolean repeat) {
        SharedPreferences.Editor editor = getApplication().getSharedPreferences("alarm", MODE_PRIVATE).edit();
        editor.putString("time", time);
        editor.putString("message", message);
        editor.putBoolean("button", repeat);
        editor.apply();

        if (repeat) {
            imgSetON.setVisibility(View.INVISIBLE);
            imgSetOFF.setVisibility(View.VISIBLE);
            etMessageTime.setEnabled(false);
        } else {
            imgSetON.setVisibility(View.VISIBLE);
            imgSetOFF.setVisibility(View.INVISIBLE);
            etMessageTime.setEnabled(true);
        }
    }

    public void getAlarmPref() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("alarm", MODE_PRIVATE);
        txtTimeAlarm.setText(sharedPreferences.getString("time", DEFAULT_ALARM_TIME));
        etMessageTime.setText(sharedPreferences.getString("message", MESSAGE_RESET));

        boolean activeReminder = sharedPreferences.getBoolean("button", false);
        if (activeReminder) {
            imgSetON.setVisibility(View.INVISIBLE);
            imgSetOFF.setVisibility(View.VISIBLE);
            etMessageTime.setEnabled(false);
        } else {
            imgSetON.setVisibility(View.VISIBLE);
            imgSetOFF.setVisibility(View.INVISIBLE);
            etMessageTime.setEnabled(true);
        }
    }
}