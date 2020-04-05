package com.example.tripplanner3;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AlertReciever extends BroadcastReceiver {
    AudioManager am;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int i = am.getRingerMode();
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notif);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(100, 100);
        mediaPlayer.start();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Reminder for your trip!!!!");
        builder1.setCancelable(false);
        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "from end button", Toast.LENGTH_SHORT).show();
                        mediaPlayer.stop();
                        Bundle bundle = intent.getBundleExtra("bundle");
                        Trip trip = (Trip) bundle.getSerializable("trip");
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.cancel(trip.getId() + 1000);
                        Log.i("tripalert", trip + "");
//
                        //////////////////
                        dialog.cancel();
                        am.setRingerMode(i);
                    }
                });
        builder1.setNeutralButton("Snooze", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
//                Toast.makeText(context,"from snozee button",Toast.LENGTH_SHORT).show();
                Bundle bundle = intent.getBundleExtra("bundle");
                Log.i("ahmed", intent + "");
                Log.i("ahmed", bundle + "");
                Trip trip = (Trip) bundle.getSerializable("trip");
                Log.i("ahmed", trip + "");
                /////////////
                Intent notifIntent = new Intent(context, AlertReciever.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("trip", trip);
                notifIntent.putExtra("bundle", bundle1);
                PendingIntent pIntent = PendingIntent.getBroadcast(context, trip.getId(), notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                notifIntent.putExtra("trip",trip);
//                notifIntent.putExtra("action","start");
                //
//                Intent notifIntent2 = new Intent(context, NotifBcastReciever.class);
//                PendingIntent pIntent2 = PendingIntent.getBroadcast(context,trip.getId(),notifIntent2,PendingIntent.FLAG_UPDATE_CURRENT);
//                notifIntent2.putExtra("trip",trip);
//                notifIntent2.putExtra("action","Cancel");
                //
                NotificationCompat.Builder mBuilder;
                mBuilder = new NotificationCompat.Builder(context, "default");
                mBuilder.setContentText("TripPlanner")
                        .setContentTitle("You are waiting for trip " + trip.getName())
                        .setSmallIcon(R.drawable.app_icon)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pIntent);
//                        .addAction(R.mipmap.ic_launcher_round,"Start trip",pIntent)
//                        .addAction(R.mipmap.ic_launcher_round,"cancel",pIntent2);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(trip.getId() + 1000, mBuilder.build());
                ///////////////////
                mediaPlayer.stop();
                am.setRingerMode(i);
                //hna el alarm set
                String dateStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                String timeStamp = new SimpleDateFormat("hh:mm").format(Calendar.getInstance().getTime());
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                DateFormat formate2 = new SimpleDateFormat("hh:mm");
                Calendar myCal = Calendar.getInstance();
                try {
                    Date myTime = formate2.parse(timeStamp);
                    Date date2 = format.parse(dateStamp);
                    Log.i("ahmed", String.valueOf(myCal.getTime()));
//                    myCal.setTime(date2);
//                    myCal.set(Calendar.HOUR_OF_DAY,myTime.getHours());
//                    myCal.set(Calendar.MINUTE,myTime.getMinutes());
                    Log.i("snoozetime", myCal.toString());
                    myCal.add(Calendar.MINUTE, 1);
                    Log.i("snoozetime", myCal.toString());
                    HashMap<Integer, Date> myData = new HashMap<Integer, Date>();
                    AlarmManager myAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                    Intent intent2 = new Intent(context, AlertReciever.class);
//                    intent2.putExtra("trip",trip);
//                    PendingIntent pi = PendingIntent.getBroadcast(context, trip.getId(), intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                    myAlarm.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pIntent);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

//                            a5er el alarm
            }
        });

        AlertDialog tripAlert = builder1.create();
        if (Build.VERSION.SDK_INT >= 26) {
            tripAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        } else {
            tripAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        tripAlert.setCanceledOnTouchOutside(false);
       // tripAlert.show();


    }
}
