package com.kevin.consumer.view.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.consumer.R;
import com.kevin.consumer.databinding.ActivitySettingsBinding;
import com.kevin.consumer.helper.alarm.AlarmReceiver;
import com.kevin.consumer.view.favorite.FavoriteActivity;

import de.mateware.snacky.Snacky;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySettingsBinding binding;

    private AlarmReceiver alarmReceiver;
    private final String MESSAGE_RESET = "";
    private String repeatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgSetReminderOn.setOnClickListener(this);
        binding.imgSetReminderOff.setOnClickListener(this);
        binding.imgBtnBack.setOnClickListener(this);
        binding.txtSettingLanguage.setOnClickListener(this);

        getAlarmPref();
        alarmReceiver = new AlarmReceiver();

        Animation slide_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_left.setDuration(2000);
        binding.relativeSetMessage.setAnimation(slide_left);
    }

    @Override
    public void onClick(View view) {
        String DEFAULT_ALARM_TIME = "09:00";
        if (view.getId() == R.id.txt_setting_language) {
            startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
        } else if (view.getId() == R.id.img_set_reminder_on) {
            repeatMessage = binding.etMessageTime.getText().toString();

            if (TextUtils.isEmpty(repeatMessage)) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getString(R.string.input_message))
                        .setDuration(Snacky.LENGTH_LONG)
                        .error().show();
            } else {
                alarmReceiver.setRepeatingAlarm(this, DEFAULT_ALARM_TIME, repeatMessage);
                setAlarmPref(DEFAULT_ALARM_TIME, repeatMessage, true);

                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getText(R.string.toast_repeat))
                        .setDuration(Snacky.LENGTH_LONG)
                        .success().show();
            }
        } else if (view.getId() == R.id.img_set_reminder_off) {
            binding.etMessageTime.setText(MESSAGE_RESET);

            alarmReceiver.cancelAlarm(this);
            setAlarmPref(DEFAULT_ALARM_TIME, repeatMessage, false);

            Snacky.builder()
                    .setView(view)
                    .centerText()
                    .setText(getResources().getText(R.string.toast_cancel))
                    .setDuration(Snacky.LENGTH_LONG)
                    .info().show();
        } else if (view.getId() == R.id.img_btn_back) {
            startActivity(new Intent(SettingsActivity.this, FavoriteActivity.class));
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
            binding.imgSetReminderOn.setVisibility(View.INVISIBLE);
            binding.imgSetReminderOff.setVisibility(View.VISIBLE);
            binding.etMessageTime.setEnabled(false);
        } else {
            binding.imgSetReminderOn.setVisibility(View.VISIBLE);
            binding.imgSetReminderOff.setVisibility(View.INVISIBLE);
            binding.etMessageTime.setEnabled(true);
        }
    }

    public void getAlarmPref() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("alarm", MODE_PRIVATE);
        binding.etMessageTime.setText(sharedPreferences.getString("message", MESSAGE_RESET));

        boolean activeReminder = sharedPreferences.getBoolean("button", false);
        if (activeReminder) {
            binding.imgSetReminderOn.setVisibility(View.INVISIBLE);
            binding.imgSetReminderOff.setVisibility(View.VISIBLE);
            binding.etMessageTime.setEnabled(false);
        } else {
            binding.imgSetReminderOn.setVisibility(View.VISIBLE);
            binding.imgSetReminderOff.setVisibility(View.INVISIBLE);
            binding.etMessageTime.setEnabled(true);
        }
    }
}