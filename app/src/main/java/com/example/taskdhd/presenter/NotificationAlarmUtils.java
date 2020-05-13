package com.example.taskdhd.presenter;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.taskdhd.R;
import com.example.taskdhd.view.NotificationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


//https://stackoverflow.com/questions/4315611/android-get-all-pendingintents-set-with-alarmmanager/42239371
public class NotificationAlarmUtils {

    private final String sharedPrefKey = "taskDHD";
    private final String sharedPrefNotificationKey = "notificationList";
    private final String GROUP_KEY = "group_notification";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public NotificationAlarmUtils(Context context){
        this.context = context;
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences(sharedPrefKey, MODE_PRIVATE);
        this.editor = context.getSharedPreferences(sharedPrefKey, MODE_PRIVATE).edit();
    }

    public void addAlarmNotification(Notification notification, int notificationID, int minutes, String companyCode) {
        Calendar mcurrentTime = Calendar.getInstance();
        Intent notificationIntent = new Intent(context, NotificationView.class);
        notificationIntent.putExtra(String.valueOf(notificationID), notificationID);
        notificationIntent.putExtra(String.valueOf(notificationID).concat("-notification"), notification);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,mcurrentTime.getTimeInMillis(),60000 * minutes, pendingIntent);

        saveAlarmId(notificationID, minutes, companyCode);
    }


    public Notification addNotification(String companyName, String notificationID) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Aussie Stocks");
        builder.setContentText("Remember to check the stock price of "+companyName);
        builder.setSmallIcon(R.drawable.ic_red_favourite);
        builder.setGroup(GROUP_KEY);
        builder.setGroupSummary(true);
        builder.setAutoCancel(true);
        //builder.setStyle(new Notification.InboxStyle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //String channelId = "notification-id";
            builder.setChannelId(notificationID);
        }

        return builder.build();
    }

    public void cancelAlarmNotification(Notification notification, int notificationID, String companyCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, NotificationView.class);
        notificationIntent.putExtra(String.valueOf(notificationID), notificationID);
        notificationIntent.putExtra(String.valueOf(notificationID).concat("-notification"), notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        cancelAlarm(notificationID, companyCode);
    }

    public void cancelAlarm(int notificationID, String companyCode){
        ArrayList<String> notificationRecord = new ArrayList<>();
        String alarmIDArray = prefs.getString(sharedPrefNotificationKey, null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        ArrayList<ArrayList<String>> alarmIDArrayList = gson.fromJson(alarmIDArray, type);
        notificationRecord.add(companyCode);
        notificationRecord.add(String.valueOf(0));
        //System.out.println(notificationRecord.toString());
        for(int i=0; i<alarmIDArrayList.size(); i++){
            if(alarmIDArrayList.get(i).get(0).equals(companyCode)){
                alarmIDArrayList.set(i,notificationRecord);
            }
        }
        editor.putString(sharedPrefNotificationKey, alarmIDArrayList.toString());
        editor.apply();
        System.out.println(prefs.getString(sharedPrefNotificationKey, null));
    }

    public void deleteAlarmID(Notification notification, int notificationID, String companyCode){
        String alarmIDArray = prefs.getString(sharedPrefNotificationKey, null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        ArrayList<ArrayList<String>> alarmIDArrayList = gson.fromJson(alarmIDArray, type);
        for(int i=0; i<alarmIDArrayList.size(); i++){
            if(alarmIDArrayList.get(i).get(0).equals(companyCode)){
                alarmIDArrayList.remove(i);
                break;
            }
        }
        editor.putString(sharedPrefNotificationKey, alarmIDArrayList.toString());
        editor.apply();
        System.out.println(prefs.getString(sharedPrefNotificationKey, null));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, NotificationView.class);
        notificationIntent.putExtra(String.valueOf(notificationID), notificationID);
        notificationIntent.putExtra(String.valueOf(notificationID).concat("-notification"), notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

    }



    public void saveAlarmId(int id, int minutes, String companyCode) {
        ArrayList<String> notificationRecord = new ArrayList<>();
        boolean detectedID = false;
        //editor.remove(sharedPrefNotificationKey).apply();

        if(prefs.getString(sharedPrefNotificationKey, null) == null){
            ArrayList<ArrayList<String>> newAlarmIDArray = new ArrayList<>();
            notificationRecord.add(companyCode);
            notificationRecord.add(String.valueOf(minutes));
            newAlarmIDArray.add(notificationRecord);
            editor.putString(sharedPrefNotificationKey, newAlarmIDArray.toString());
        }else{
            String alarmIDArray = prefs.getString(sharedPrefNotificationKey, null);
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
            ArrayList<ArrayList<String>> alarmIDArrayList = gson.fromJson(alarmIDArray, type);
            notificationRecord.add(companyCode);
            notificationRecord.add(String.valueOf(minutes));

            System.out.println(id);
            // TODO Favourite list and notification list need to be sync
            // TODO method to be used: https://www.tutorialspoint.com/java/util/arraylist_add_index.htm

            for(int i=0; i<alarmIDArrayList.size(); i++){
                if(alarmIDArrayList.get(i).get(0).equals(notificationRecord.get(0))){
                    System.out.println("SET");
                    detectedID = true;
                    alarmIDArrayList.set(i,notificationRecord);
                }
            }
            if(!detectedID){
                System.out.println("ADD");
                alarmIDArrayList.add(notificationRecord);
            }
            editor.putString(sharedPrefNotificationKey, alarmIDArrayList.toString());
        }
        editor.apply();
        System.out.println(prefs.getString(sharedPrefNotificationKey, null));
    }

    public ArrayList<ArrayList<String>> getAllNotification(){
        SharedPreferences prefs = context.getSharedPreferences(sharedPrefKey, MODE_PRIVATE);
        String alarmIDArray = prefs.getString(sharedPrefNotificationKey, null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        ArrayList<ArrayList<String>> alarmIDArrayList = gson.fromJson(alarmIDArray, type);
        return alarmIDArrayList;
    }

}
