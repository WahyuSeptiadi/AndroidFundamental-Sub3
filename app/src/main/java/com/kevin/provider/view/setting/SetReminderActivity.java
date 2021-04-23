package com.kevin.provider.view.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.provider.R;
import com.kevin.provider.helper.alarm.AlarmReceiver;
import com.kevin.provider.helper.alarm.TimePickerFragment;
import com.kevin.provider.view.search.SearchActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.mateware.snacky.Snacky;

public class SetReminderActivity extends AppCompatActivity implements View.OnClickListener, TimePickerFragment.DialogTimeListener {

    private TextView detailTime;
    private EditText et_messageTime;
    private ImageView setON;
    private ImageView setOFF;
    private AlarmReceiver alarmReceiver;

    private final String TIME_PICKER_REPEAT_TAG = "TIME REPEAT TAG";
    private final String TIME_RESET = "00:00";
    private final String MESSAGE_RESET = "";
    private String MESSAGE_DEFAULT = "";
    private String repeatTime, repeatMessage;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        ImageView setTime = findViewById(R.id.setTimeReminder);
        setON = findViewById(R.id.setReminderON);
        setOFF = findViewById(R.id.setReminderOFF);
        Button btnDefTime = findViewById(R.id.btnDefaultTime);
        et_messageTime = findViewById(R.id.et_messageTime);
        detailTime = findViewById(R.id.tv_time);
        ImageView btnBack = findViewById(R.id.btnBack);
        RelativeLayout setMessage = findViewById(R.id.setMessage);
        TextView setLanguage = findViewById(R.id.txt_setting_language);

        setTime.setOnClickListener(this);
        setON.setOnClickListener(this);
        setOFF.setOnClickListener(this);
        btnDefTime.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        setLanguage.setOnClickListener(this);

        MESSAGE_DEFAULT = getBaseContext().getResources().getString(R.string.msg_default);

        getAlarmPref();
        alarmReceiver = new AlarmReceiver();

        Animation slide_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        slide_left.setDuration(2000);

        setTime.setAnimation(slide_left);
        setMessage.setAnimation(slide_left);
    }

    @Override
    public void onClick(View view) {
        boolean activeReminder = sharedPreferences.getBoolean("button", false);

        if (view.getId() == R.id.txt_setting_language) {
            startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
        } else if (view.getId() == R.id.setTimeReminder) {
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
                et_messageTime.setText(MESSAGE_DEFAULT);
            }
        } else if (view.getId() == R.id.setReminderON) {
            repeatTime = detailTime.getText().toString();
            repeatMessage = et_messageTime.getText().toString();
            if (TextUtils.isEmpty(repeatMessage)) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getString(R.string.input_message))
                        .setDuration(Snacky.LENGTH_LONG)
                        .error().show();
            } else {
                alarmReceiver.setRepeatingAlarm(this, repeatTime, repeatMessage);
                setAlarmPref(repeatTime, repeatMessage, true);

                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getText(R.string.toast_repeat))
                        .setDuration(Snacky.LENGTH_LONG)
                        .success().show();
            }
        } else if (view.getId() == R.id.setReminderOFF) {
            detailTime.setText(TIME_RESET);
            et_messageTime.setText(MESSAGE_RESET);

            alarmReceiver.cancelAlarm(this);
            setAlarmPref(repeatTime, repeatMessage, false);

            Snacky.builder()
                    .setView(view)
                    .centerText()
                    .setText(getResources().getText(R.string.toast_cancel))
                    .setDuration(Snacky.LENGTH_LONG)
                    .info().show();
        } else if (view.getId() == R.id.btnDefaultTime) {
            if (activeReminder) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getString(R.string.toast_turn_off_reminder))
                        .setDuration(Snacky.LENGTH_LONG)
                        .warning().show();
            } else {
                // DEFAULT ALARM
                String DEFAULT_TIME = "09:00";
                detailTime.setText(DEFAULT_TIME);
                et_messageTime.setText(MESSAGE_DEFAULT);
            }
        } else if (view.getId() == R.id.btnBack) {
            startActivity(new Intent(SetReminderActivity.this, SearchActivity.class));
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
            detailTime.setText(dateFormat.format(calendar.getTime()));
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
            et_messageTime.setEnabled(false);
        } else {
            setON.setVisibility(View.VISIBLE);
            setOFF.setVisibility(View.INVISIBLE);
            et_messageTime.setEnabled(true);
        }
    }

    public void getAlarmPref() {
        sharedPreferences = getApplication().getSharedPreferences("alarm", MODE_PRIVATE);
        detailTime.setText(sharedPreferences.getString("time", TIME_RESET));
        et_messageTime.setText(sharedPreferences.getString("message", MESSAGE_RESET));

        boolean activeReminder = sharedPreferences.getBoolean("button", false);
        if (activeReminder) {
            setON.setVisibility(View.INVISIBLE);
            setOFF.setVisibility(View.VISIBLE);
            et_messageTime.setEnabled(false);
        } else {
            setON.setVisibility(View.VISIBLE);
            setOFF.setVisibility(View.INVISIBLE);
            et_messageTime.setEnabled(true);
        }
    }
}