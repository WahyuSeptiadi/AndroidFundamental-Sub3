//package com.kevin.provider.helper.alarm;
//
//import android.app.Activity;
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import com.kevin.provider.R;
//import com.kevin.provider.view.search.SearchActivity;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//import de.mateware.snacky.Snacky;
//
//import static android.content.Context.MODE_PRIVATE;
//
//public class AlarmReceiver extends BroadcastReceiver {
//    public static final String TYPE_REPEATING = "Github API";
//    public static final String EXTRA_MESSAGE = "message";
//    public static final String EXTRA_TYPE = "type";
//    private final int ID_REPEATING = 1;
//
//    private final Activity activity;
//
//    public AlarmReceiver(Activity activity) {
//        this.activity = activity;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String message = intent.getStringExtra(EXTRA_MESSAGE);
//        String title = TYPE_REPEATING;
//
//        Snacky.builder()
//                .setActivity(activity)
//                .centerText()
//                .setText(title + " : " + message)
//                .setDuration(Snacky.LENGTH_LONG)
//                .info().show();
//
//        showAlarmNotification(context, title, message);
//    }
//
//    public boolean isDateInvalid(String date, String format) {
//        try {
//            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
//            df.setLenient(false);
//            df.parse(date);
//            return false;
//        } catch (ParseException e) {
//            return true;
//        }
//    }
//
//    private void showAlarmNotification(Context context, String title, String message) {
//        String CHANNEL_ID = "Channel_1";
//        String CHANNEL_NAME = "AlarmManager channel";
//
//        Intent intent = new Intent(context, SearchActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.drawable.ic_time_reminder)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
//                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                .setSound(alarmSound);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
//            builder.setChannelId(CHANNEL_ID);
//            if (notificationManagerCompat != null) {
//                notificationManagerCompat.createNotificationChannel(channel);
//            }
//        }
//
//        Notification notification = builder.build();
//        if (notificationManagerCompat != null) {
//            notificationManagerCompat.notify(1, notification);
//        }
//    }
//
//    public void setRepeatingAlarm(Context context, String type, String time, String message) {
//        String TIME_FORMAT = "HH:mm";
//        if (isDateInvalid(time, TIME_FORMAT)) return;
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra(EXTRA_MESSAGE, message);
//        intent.putExtra(EXTRA_TYPE, type);
//
//        String[] timeArray = time.split(":");
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
//        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
//        calendar.set(Calendar.SECOND, 0);
//
//        SharedPreferences.Editor editor = context.getSharedPreferences("repeat", MODE_PRIVATE).edit();
//        editor.putBoolean("type" + type, true);
//        editor.putBoolean("time" + time, true);
//        editor.putBoolean("message" + message, true);
//        editor.apply();
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0);
//        if (alarmManager != null) {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//
//            SharedPreferences sharedPreferences = context.getSharedPreferences("repeat", Context.MODE_PRIVATE);
//            sharedPreferences.getBoolean("type" + type, false);
//            sharedPreferences.getBoolean("time" + time, false);
//            sharedPreferences.getBoolean("message" + message, false);
//        }
//
//        Snacky.builder()
//                .setActivity(activity)
//                .centerText()
//                .setText(context.getResources().getText(R.string.toast_repeat))
//                .setDuration(Snacky.LENGTH_LONG)
//                .success().show();
//    }
//
//    public void cancelAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0);
//        pendingIntent.cancel();
//
//        if (alarmManager != null) {
//            alarmManager.cancel(pendingIntent);
//        }
//
//        Snacky.builder()
//                .setActivity(activity)
//                .centerText()
//                .setText(context.getResources().getText(R.string.toast_cancel))
//                .setDuration(Snacky.LENGTH_LONG)
//                .info().show();
//    }
//}
