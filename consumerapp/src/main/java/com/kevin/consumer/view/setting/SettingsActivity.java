package com.kevin.consumer.view.setting;

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

import com.kevin.consumer.R;
import com.kevin.consumer.helper.alarm.AlarmReceiver;
import com.kevin.consumer.helper.alarm.TimePickerFragment;
import com.kevin.consumer.view.favorite.FavoriteActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.mateware.snacky.Snacky;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, TimePickerFragment.DialogTimeListener {

    private EditText etMessageTime;
    private ImageView setON, setOFF;
    private AlarmReceiver alarmReceiver;
    private TextView txtTimeAlarm;

    private final String TIME_PICKER_REPEAT_TAG = "TIME REPEAT TAG";
    private final String DEFAULT_ALARM_TIME = "09:00";
    private final String MESSAGE_RESET = "";
    private String repeatMessage;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setON = findViewById(R.id.img_set_reminder_on);
        setOFF = findViewById(R.id.img_set_reminder_off);
        etMessageTime = findViewById(R.id.et_message_time);
        ImageView btnBack = findViewById(R.id.img_btn_back);
        RelativeLayout setMessage = findViewById(R.id.relative_set_message);
        TextView setLanguage = findViewById(R.id.txt_setting_language);
        TextView setManualTime = findViewById(R.id.txt_set_manual_time);
        txtTimeAlarm = findViewById(R.id.txt_default_time);

        setON.setOnClickListener(this);
        setOFF.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        setLanguage.setOnClickListener(this);
        setManualTime.setOnClickListener(this);

        getAlarmPref();
        alarmReceiver = new AlarmReceiver();

        Animation slide_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slide_left.setDuration(2000);
        setMessage.setAnimation(slide_left);
    }

    @Override
    public void onClick(View view) {
        boolean activeReminder = sharedPreferences.getBoolean("button", false);

        if (view.getId() == R.id.txt_setting_language) {
            startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
        } else if (view.getId() == R.id.txt_set_manual_time) {
            if (activeReminder) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getString(R.string.toast_turn_off_reminder))
                        .setDuration(Snacky.LENGTH_LONG)
                        .warning().show();
            } else {
                TimePickerFragment timePickerFragmentRepeat = new TimePickerFragment();
                timePickerFragmentRepeat.show(getSupportFragmentManager(), TIME_PICKER_REPEAT_TAG);
                etMessageTime.setText(getBaseContext().getResources().getString(R.string.msg_default));
            }
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
                } else {
                    String getTime = txtTimeAlarm.getText().toString();
                    alarmReceiver.setRepeatingAlarm(this, getTime, repeatMessage);
                    setAlarmPref(getTime, repeatMessage, true);
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
            startActivity(new Intent(SettingsActivity.this, FavoriteActivity.class));
            finish();
        }
    }

    @Override
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (TIME_PICKER_REPEAT_TAG.equals(tag)) {
            txtTimeAlarm.setText(dateFormat.format(calendar.getTime()));
        }
    }

    public void setAlarmPref(String time, String message, boolean repeat) {
        SharedPreferences.Editor editor = getApplication().getSharedPreferences("alarm", MODE_PRIVATE).edit();
        editor.putString("time", time);
        editor.putString("message", message);
        editor.putBoolean("button", repeat);
        editor.apply();

        if (repeat) {
            setON.setVisibility(View.INVISIBLE);
            setOFF.setVisibility(View.VISIBLE);
            etMessageTime.setEnabled(false);
        } else {
            setON.setVisibility(View.VISIBLE);
            setOFF.setVisibility(View.INVISIBLE);
            etMessageTime.setEnabled(true);
        }
    }

    public void getAlarmPref() {
        sharedPreferences = getApplication().getSharedPreferences("alarm", MODE_PRIVATE);
        txtTimeAlarm.setText(sharedPreferences.getString("time", DEFAULT_ALARM_TIME));
        etMessageTime.setText(sharedPreferences.getString("message", MESSAGE_RESET));

        boolean activeReminder = sharedPreferences.getBoolean("button", false);
        if (activeReminder) {
            setON.setVisibility(View.INVISIBLE);
            setOFF.setVisibility(View.VISIBLE);
            etMessageTime.setEnabled(false);
        } else {
            setON.setVisibility(View.VISIBLE);
            setOFF.setVisibility(View.INVISIBLE);
            etMessageTime.setEnabled(true);
        }
    }
}