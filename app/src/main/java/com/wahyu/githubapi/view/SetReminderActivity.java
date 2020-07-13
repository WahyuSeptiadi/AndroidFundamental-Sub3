package com.wahyu.githubapi.view;

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

import com.wahyu.githubapi.R;
import com.wahyu.githubapi.alarm.AlarmReceiver;
import com.wahyu.githubapi.alarm.fragment.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SetReminderActivity extends AppCompatActivity implements View.OnClickListener, TimePickerFragment.DialogTimeListener{

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
        RelativeLayout setMessage =  findViewById(R.id.setMessage);

        //set OnCLICK
        setTime.setOnClickListener(this);
        setON.setOnClickListener(this);
        setOFF.setOnClickListener(this);
        btnDefTime.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        MESSAGE_DEFAULT = getBaseContext().getResources().getString(R.string.msg_default);

        getAlarmPref();
        alarmReceiver = new AlarmReceiver();

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
        boolean aktifReminder = sharedPreferences.getBoolean("button", false);

        switch (view.getId()){
            case R.id.setTimeReminder :
                if (aktifReminder){
                    Toasty.warning(this, getResources().getText(R.string.toast_turn_off_reminder), Toasty.LENGTH_SHORT, true).show();
                }else{
                    TimePickerFragment timePickerFragmentRepeat = new TimePickerFragment();
                    timePickerFragmentRepeat.show(getSupportFragmentManager(), TIME_PICKER_REPEAT_TAG);
                    et_messageTime.setText(MESSAGE_DEFAULT);
                }
                break;
            case R.id.setReminderON :
                repeatTime = detailTime.getText().toString();
                repeatMessage = et_messageTime.getText().toString();
                if (TextUtils.isEmpty(repeatMessage)){
                    Toasty.error(this, getResources().getText(R.string.input_message), Toasty.LENGTH_SHORT, true).show();
                }else{
                    alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING, repeatTime, repeatMessage);
                    setAlarmPref(repeatTime, repeatMessage, true);
                }
                break;
            case R.id.setReminderOFF :
                detailTime.setText(TIME_RESET);
                et_messageTime.setText(MESSAGE_RESET);

                alarmReceiver.cancelAlarm(this);
                setAlarmPref(repeatTime, repeatMessage, false);
                break;
            case R.id.btnDefaultTime :
                if (aktifReminder){
                    Toasty.warning(this, getResources().getText(R.string.toast_turn_off_reminder), Toasty.LENGTH_SHORT, true).show();
                }else{
                    detailTime.setText(TIME_DEFAULT);
                    et_messageTime.setText(MESSAGE_DEFAULT);
                }
                break;
            case R.id.btnBack :
                Intent toFav = new Intent(SetReminderActivity.this, SearchActivity.class);
                startActivity(toFav);

                finish();
                break;
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

    public void setAlarmPref(String time, String message, boolean repeat){
        SharedPreferences.Editor editor = getApplication().getSharedPreferences("alarm", MODE_PRIVATE).edit();
        editor.putString("time", time);
        editor.putString("message", message);
        editor.putBoolean("button", repeat);
        editor.apply();

        if (repeat){
            setON.setVisibility(View.INVISIBLE);
            setOFF.setVisibility(View.VISIBLE);
            et_messageTime.setEnabled(false);
        }else{
            setON.setVisibility(View.VISIBLE);
            setOFF.setVisibility(View.INVISIBLE);
            et_messageTime.setEnabled(true);
        }
    }

    public void getAlarmPref(){
        sharedPreferences = getApplication().getSharedPreferences("alarm", MODE_PRIVATE);
        detailTime.setText(sharedPreferences.getString("time",TIME_RESET));
        et_messageTime.setText(sharedPreferences.getString("message", MESSAGE_RESET));

        boolean aktifReminder = sharedPreferences.getBoolean("button", false);
        if (aktifReminder){
            setON.setVisibility(View.INVISIBLE);
            setOFF.setVisibility(View.VISIBLE);
            et_messageTime.setEnabled(false);
        }else{
            setON.setVisibility(View.VISIBLE);
            setOFF.setVisibility(View.INVISIBLE);
            et_messageTime.setEnabled(true);
        }
    }
}