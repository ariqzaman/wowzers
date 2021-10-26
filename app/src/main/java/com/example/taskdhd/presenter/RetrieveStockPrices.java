package com.example.taskdhd.presenter;

import android.content.Context;

import com.example.taskdhd.model.Companies;
import com.example.taskdhd.model.Company;
import com.example.taskdhd.model.DailyPrice;
import com.example.taskdhd.model.SaveStockPrices;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class RetrieveStockPrices {

    private Companies companies;
    private ArrayList<Company> companyArrayList = new ArrayList<>();
    private SaveStockPrices internalStorage;


    public RetrieveStockPrices(Context context){
        internalStorage = new SaveStockPrices(context);
    }


    public Companies requestIndexesPriceOnline(String[] indexesArray){
        try {
            for(int i=0; i<indexesArray.length; i++){
                getStockPricesOnline(indexesArray[i]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return companies;
    }


    public Companies requestCompanyPriceOnline(String companyCode){

        try{
            getStockPricesOnline(companyCode);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return companies;
    }


    private void getStockPricesOnline(final String companyCode){
        final String REQUEST_METHOD = "GET";
        final int READ_TIMEOUT = 15000;
        final int CONNECTION_TIMEOUT = 15000;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String inputLine;
                String result;

                Company company;
                boolean successful = false;
                while(!successful){
                    try{
//                        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="
//                                +companyCode+
//                                "&apikey=UXFCZEY0WC38WLF2&outputsize=compact";

                        //String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&apikey=DEMO&outputsize=compact";
                        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=AAPL&interval=5min&apikey=UXFCZEY0WC38WLF2&outputsize=compact";
                        System.out.println(url);
                        URL api = new URL(url);
                        HttpURLConnection connection =(HttpURLConnection) api.openConnection();

                        //connection.setRequestMethod(REQUEST_METHOD);
                        //connection.setReadTimeout(READ_TIMEOUT);
                        //connection.setConnectTimeout(CONNECTION_TIMEOUT);

                        connection.connect();

                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();

                        while((inputLine = reader.readLine()) != null){
                            stringBuilder.append(inputLine);
                        }

                        reader.close();
                        streamReader.close();

                        result = stringBuilder.toString();

                        JSONObject jsonObject = new JSONObject(result);

//                        if(jsonObject.has("Time Series (Daily)")) {
//                            internalStorage.saveStockPrices(companyCode, result);
//                            companyArrayList.add(convertToCompany(jsonObject));
//                            companies = new Companies(companyArrayList);
//                            successful = true;
                        if(jsonObject.has("Time Series (5min)")) {
                            internalStorage.saveStockPrices(companyCode, result);
                            companyArrayList.add(convertToCompany(jsonObject));
                            companies = new Companies(companyArrayList);
                            successful = true;
                        }else if (jsonObject.has("Note")){
                            //System.out.println("HAHA");
                            company = new Company(
                                    "API Limit",
                                    null);
                            companyArrayList = new ArrayList<>();
                            companyArrayList.add(company);
                            companies = new Companies(companyArrayList);
                            successful = true;
                        }else if(jsonObject.has("Error Message")){
                            company = new Company(
                                    "Company undefined",
                                    null);
                            companyArrayList = new ArrayList<>();
                            companyArrayList.add(company);
                            companies = new Companies(companyArrayList);
                            successful = true;
                        }

                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

            }
        });
        thread.start();
        try{
            thread.join();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Companies getIndexesPriceInternal(String[] indexesArray){
        ArrayList<Company> arrayCompany = new ArrayList<>();
        try {
            for(int i=0; i<indexesArray.length; i++){
                JSONObject jsonObject = internalStorage.getStockPrice(indexesArray[i]);
                Company index = convertToCompany(jsonObject);
                arrayCompany.add(index);
            }
            Companies indexes = new Companies(arrayCompany);
            return indexes;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return companies;
    }




    private Company convertToCompany(JSONObject jsonFile){
        Company company;
        ArrayList<DailyPrice> dailyPrices = new ArrayList<>();
        try {
            //JSONObject jsonPrices = jsonFile.getJSONObject("Time Series (Daily)");
            JSONObject jsonPrices = jsonFile.getJSONObject("Time Series (5min)");
            Iterator<String> dates = jsonPrices.keys();

            while (dates.hasNext()) {
                String date = dates.next();
                JSONObject jsonPrice = jsonPrices.getJSONObject(date);
                DailyPrice dailyPrice = new DailyPrice(
                        date,
                        jsonPrice.getDouble("1. open"),
                        jsonPrice.getDouble("2. high"),
                        jsonPrice.getDouble("3. low"),
                        jsonPrice.getDouble("4. close"),
                        jsonPrice.getInt("5. volume"));
                dailyPrices.add(dailyPrice);
            }

            String symbol = jsonFile
                    .getJSONObject("Meta Data")
                    .getString("2. Symbol");

            company = new Company(
                    symbol,
                    dailyPrices);

            return company;

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

    }
}
