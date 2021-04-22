package com.kevin.consumer.view.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.kevin.consumer.R;
import com.kevin.consumer.helper.alarm.AlarmReceiver;
import com.kevin.consumer.helper.alarm.TimePickerFragment;
import com.kevin.consumer.view.favorite.FavoriteActivity;

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

    final String TIME_PICKER_REPEAT_TAG = "TIME REPEAT TAG";
    final String TIME_DEFAULT = "09:00";
    final String TIME_RESET = "00:00";
    final String MESSAGE_RESET = "";
    String MESSAGE_DEFAULT = "";
    String repeatTime, repeatMessage;

    SharedPreferences sharedPreferences;

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

        setTime.setOnClickListener(this);
        setON.setOnClickListener(this);
        setOFF.setOnClickListener(this);
        btnDefTime.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        MESSAGE_DEFAULT = getBaseContext().getResources().getString(R.string.msg_default);

        getAlarmPref();
        alarmReceiver = new AlarmReceiver(SetReminderActivity.this);

        //set ANIMATION
        Animation slide_left = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation slide_bottom1 = AnimationUtils.loadAnimation(this, R.anim.up_to_down);
        Animation slide_bottom2 = AnimationUtils.loadAnimation(this, R.anim.up_to_down);

        slide_bottom1.setDuration(1900);
        slide_bottom2.setDuration(2000);
        slide_left.setDuration(2000);

        setTime.setAnimation(slide_left);
        setMessage.setAnimation(slide_left);
        btnDefTime.setAnimation(slide_bottom2);
        detailTime.setAnimation(slide_bottom1);

    }

    @Override
    public void onClick(View view) {
        boolean activeReminder = sharedPreferences.getBoolean("button", false);

        if (view.getId() == R.id.setTimeReminder) {
            if (activeReminder) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getText(R.string.toast_turn_off_reminder))
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
                        .setText(getResources().getText(R.string.input_message))
                        .setDuration(Snacky.LENGTH_LONG)
                        .warning().show();
            } else {
                alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING, repeatTime, repeatMessage);
                setAlarmPref(repeatTime, repeatMessage, true);
            }
        } else if (view.getId() == R.id.setReminderOFF) {
            detailTime.setText(TIME_RESET);
            et_messageTime.setText(MESSAGE_RESET);

            alarmReceiver.cancelAlarm(this);
            setAlarmPref(repeatTime, repeatMessage, false);
        } else if (view.getId() == R.id.btnDefaultTime) {
            if (activeReminder) {
                Snacky.builder()
                        .setView(view)
                        .centerText()
                        .setText(getResources().getText(R.string.toast_turn_off_reminder))
                        .setDuration(Snacky.LENGTH_LONG)
                        .warning().show();
            } else {
                detailTime.setText(TIME_DEFAULT);
                et_messageTime.setText(MESSAGE_DEFAULT);
            }
        } else if (view.getId() == R.id.btnBack) {
            Intent toFav = new Intent(SetReminderActivity.this, FavoriteActivity.class);
            startActivity(toFav);

            finish();
        }
    }

    @Override
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

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