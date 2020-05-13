package com.example.taskdhd.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.example.taskdhd.view.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SaveStockPrices {
    private Context context;

    public SaveStockPrices(Context context){
        this.context = context;
        setupStorage();
    }

    private void setupStorage(){
        File file = new File(context.getFilesDir(), "data");
        if(!file.exists()) {
            boolean fileCreated = file.mkdirs();
            if (!fileCreated) {
                System.out.println("Directory create was not successful.");
            }else{
                System.out.println("Directory create was successful.");
            }
        }
//        else{
//            boolean fileDeleted = file.delete();
//            if (fileDeleted) {
//                System.out.println("Directory deleted for testing purpose.");
//            }else{
//                System.out.println("Directory deleted not succesful.");
//            }
//        }
    }

    public void saveStockPrices(String companyCode, String json){

        File path = context.getFilesDir();
        File file = new File(path, "/data/"+companyCode+".json");

        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
            printWriter.write(json);
            printWriter.flush();
            printWriter.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
//        File check = new File(path, "/data/");
//        for(String fileName:check.list()){
//            System.out.println(fileName);
//            File currentFile = new File(check.getPath(),fileName);
//            boolean fileDeleted = currentFile.delete();
//            if (fileDeleted) {
//                System.out.println("Directory deleted for testing purpose.");
//            }
//        }
    }

    public JSONObject getStockPrice(String companyCode){
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(readStockPrice(companyCode));
            return jsonObject;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

    }

    public String readStockPrice(String companyCode){
        String result="";
        File file = new File(context.getFilesDir(),"/data/"+companyCode+".json");
        if(file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                fis.close();
                isr.close();
                bufferedReader.close();

                result = stringBuilder.toString();

            }catch (Exception ex){
                ex.printStackTrace();
            }

        }else{
            result = "{\"warning\":\"data not exist in internal storage\"}";
        }


        return result;
    }

}
