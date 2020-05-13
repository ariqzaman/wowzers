package com.example.taskdhd.presenter;

import com.example.taskdhd.model.Companies;
import com.example.taskdhd.model.Company;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class RetrieveCompaniesList {

    public RetrieveCompaniesList(){}

    public ArrayList<ArrayList<String>> retrieveCompanies(){
        ArrayList<ArrayList<String>> companiesList = new ArrayList<ArrayList<String>>();

        final int READ_TIMEOUT = 15000;
        final int CONNECTION_TIMEOUT = 15000;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String inputLine;
                String result;

                try{
                    String url = "https://www.asx.com.au/asx/research/ASXListedCompanies.csv";

                    URL api = new URL(url);
                    URLConnection connection = api.openConnection();

                    connection.setReadTimeout(READ_TIMEOUT);
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);

                    connection.connect();

                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    reader.readLine();
                    reader.readLine();
                    reader.readLine();
                    while((inputLine = reader.readLine()) != null){
                        String[] values = inputLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        ArrayList<String> company = new ArrayList<>();
                        company.add(values[0].substring(1, values[0].length()-1));
                        company.add(values[1].substring(1, values[1].length()-1));
                        company.add(values[2].substring(1, values[2].length()-1));
                        companiesList.add(company);
                    }

                    reader.close();
                    streamReader.close();
                } catch (UnknownHostException noConnection){
                    System.out.println("No Connection Warn User");
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
        try{
            thread.join();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return companiesList;
    }

}
