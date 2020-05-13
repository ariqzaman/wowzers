package com.example.taskdhd.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class NotificationView extends BroadcastReceiver {

    private final String sharedPrefKey = "taskDHD";
    private final String sharedPrefNotificationKey = "notificationList";
    private SharedPreferences prefs;


    @Override
    public void onReceive(final Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences prefs = context.getSharedPreferences(sharedPrefKey, MODE_PRIVATE);
        //System.out.println(prefs.getString(sharedPrefNotificationKey, null));
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                //System.out.println(key);
                String intentKey = bundle.get(key).toString();
                System.out.println(intentKey);
                Notification notification = intent.getParcelableExtra(intentKey + "-notification");
                int notificationId = intent.getIntExtra(intentKey, Integer.valueOf(intentKey));
                notificationManager.notify(notificationId, notification);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(key, "Channel human readable title", importance);
                    notificationManager.createNotificationChannel(channel);
                }
                break;
            }
        }
    }
}

//        Notification notification = intent.getParcelableExtra(NOTIFICATION);
//        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
//        notificationManager.notify(notificationId, notification);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "notification-id";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title", importance);
//            notificationManager.createNotificationChannel(channel);
//        }
