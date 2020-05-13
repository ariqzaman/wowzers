package com.example.taskdhd.presenter;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taskdhd.R;
import com.google.gson.Gson;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;

public class FavouriteListManager {

    private final String sharedPrefKey = "taskDHD";
    private final String sharedPrefFavouriteKey = "favouriteList";
    private Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private NotificationAlarmUtils alarmUtils;
    Gson gson;
    ImageView favouriteButton;
    Toast toastMessage;

    public FavouriteListManager(Context context, View view) {

        this.context = context;
        gson = new Gson();
        editor = context.getSharedPreferences(sharedPrefKey, MODE_PRIVATE).edit();
        prefs = context.getSharedPreferences(sharedPrefKey, MODE_PRIVATE);
        favouriteButton = view.findViewById(R.id.favouriteButton);
        alarmUtils = new NotificationAlarmUtils(context);

    }

    public boolean detectFavouriteCompany(String companyName, String companyCode, String companyIndustry){

        String favouriteListJson = prefs.getString(sharedPrefFavouriteKey, null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        ArrayList<ArrayList<String>> favouriteList = gson.fromJson(favouriteListJson, type);

        ArrayList<String> favouriteCompany = new ArrayList<>();
        favouriteCompany.add(companyName);
        favouriteCompany.add(companyCode.replace("ASX:", ""));
        favouriteCompany.add(companyIndustry);
//        System.out.println("DETECT"+favouriteCompany.toString());
//        System.out.println("DETECT"+favouriteList.contains(favouriteCompany));
        return favouriteList.contains(favouriteCompany);
    }


    public void deleteFromFavourite(String companyName, String companyCode, String companyIndustry){
        String favouriteListJson = prefs.getString(sharedPrefFavouriteKey, null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        ArrayList<ArrayList<String>> favouriteList = gson.fromJson(favouriteListJson, type);

        ArrayList<String> favouriteCompany = new ArrayList<>();
        favouriteCompany.add(companyName);
        favouriteCompany.add(companyCode.replace("ASX:", ""));
        favouriteCompany.add(companyIndustry);

        favouriteList.remove(favouriteCompany);
        favouriteListJson = gson.toJson(favouriteList);
        editor.putString(sharedPrefFavouriteKey, favouriteListJson);
        editor.apply();
    }

    public void saveToFavouriteList(String companyName, String companyCode, String companyIndustry){

        ArrayList<String> favouriteCompany = new ArrayList<>();
        String favouriteListJson;

        if(toastMessage != null){
            toastMessage.cancel();
        }

        if(prefs.getString(sharedPrefFavouriteKey, null) == null){
            ArrayList<ArrayList<String>> favouriteList = new ArrayList<>();
            favouriteCompany.add(companyName);
            favouriteCompany.add(companyCode.replace("ASX:", ""));
            favouriteCompany.add(companyIndustry);

            favouriteList.add(favouriteCompany);
            favouriteListJson = gson.toJson(favouriteList);

            Toast.makeText(context, companyName+" succesfully save in favourite list",
                    Toast.LENGTH_SHORT).show();
        }else{
            favouriteListJson = prefs.getString(sharedPrefFavouriteKey, null);
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
            ArrayList<ArrayList<String>> favouriteList = gson.fromJson(favouriteListJson, type);

            favouriteCompany.add(companyName);
            favouriteCompany.add(companyCode.replace("ASX:", ""));
            favouriteCompany.add(companyIndustry);

            if(favouriteList.contains(favouriteCompany)){
                favouriteButton.setBackgroundResource(R.drawable.ic_favorite_24px);
                removeAlarmCompany(favouriteList, favouriteCompany);
                favouriteList.remove(favouriteCompany);
                toastMessage = Toast.makeText(context, companyName+" remove from favourite list",
                        Toast.LENGTH_SHORT);
                toastMessage.show();
                Collections.sort(favouriteList, new Comparator<ArrayList<String>>() {
                    @Override
                    public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                        return o1.get(1).compareTo(o2.get(1));
                    }
                });
            }else{
                favouriteButton.setBackgroundResource(R.drawable.ic_red_favourite);
                favouriteList.add(favouriteCompany);
                toastMessage = Toast.makeText(context, companyName+" succesfully save in favourite list",
                        Toast.LENGTH_SHORT);
                toastMessage.show();
                Collections.sort(favouriteList, new Comparator<ArrayList<String>>() {
                    @Override
                    public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                        return o1.get(1).compareTo(o2.get(1));
                    }
                });
                saveAlarmCompany(favouriteList, favouriteCompany);
            }

            favouriteListJson = gson.toJson(favouriteList);
            System.out.println(favouriteList.toString());
        }

        editor.putString(sharedPrefFavouriteKey, favouriteListJson);
        editor.apply();
    }


    public ArrayList<ArrayList<String>> getFavouriteList(){
        String favouriteListJson = prefs.getString(sharedPrefFavouriteKey, null);
        Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        ArrayList<ArrayList<String>> favouriteList = gson.fromJson(favouriteListJson, type);
        return favouriteList;
    }

    private void saveAlarmCompany(ArrayList<ArrayList<String>> favouriteCompanies, ArrayList<String> company){
        for(int i=0; i<favouriteCompanies.size(); i++){
            if(favouriteCompanies.get(i).equals(company)){
                alarmUtils.saveAlarmId(i, 0, company.get(1));
                break;
            }
        }
    }

    private void removeAlarmCompany(ArrayList<ArrayList<String>> favouriteCompanies, ArrayList<String> company){
        for(int i=0; i<favouriteCompanies.size(); i++){
            if(favouriteCompanies.get(i).equals(company)){
                Notification notification = alarmUtils.addNotification(
                        favouriteCompanies.get(i).get(0), String.valueOf(i));
                alarmUtils.deleteAlarmID(notification, i, company.get(1));
                break;
            }
        }
    }

}
