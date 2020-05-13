package com.example.taskdhd.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.AnyChartView;
import com.example.taskdhd.R;
import com.example.taskdhd.model.Companies;
import com.example.taskdhd.model.Company;
import com.example.taskdhd.presenter.CompaniesListAdapter;
import com.example.taskdhd.presenter.FavouriteListManager;
import com.example.taskdhd.presenter.InternetConnectivity;
import com.example.taskdhd.presenter.LoadChartClass;
import com.example.taskdhd.presenter.RetrieveCompaniesList;
import com.example.taskdhd.presenter.RetrieveStockPrices;

import java.util.ArrayList;

public class CompanyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent intent;
    RetrieveStockPrices retrieveStockPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        retrieveStockPrice = new RetrieveStockPrices(getApplicationContext());

        initiateUI();
        new asyncLoad().execute();
    }

    private class asyncLoad extends AsyncTask<String, Integer, Company> {

        InternetConnectivity internetConnectivity = new InternetConnectivity();
        AlertDialog.Builder alertDialog;

        @Override
        protected void onPreExecute() {
            initiateUI();
            alertDialog = new AlertDialog.Builder(CompanyActivity.this);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Company company) {
//            System.out.println(company.toString());
            if(company.getCompanySymbol().equals("Data not saved")){

                alertDialog.setTitle("API Limit reach without new data loaded in internal storage");
                alertDialog.setPositiveButton("Back To Search",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompanyActivity.super.onBackPressed();
                            }
                        });
                alertDialog.show();

            }else if(company.getCompanySymbol().equals("Company undefined")){
                alertDialog.setTitle("Company Not Found in AlphaVantage");
                alertDialog.setPositiveButton("Back To Search",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompanyActivity.super.onBackPressed();
                            }
                        });
                alertDialog.show();
            }else{
                ProgressBar progressBar = findViewById(R.id.companyProgressBar);
                progressBar.setVisibility(View.INVISIBLE);
                generateMainUI(company);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Company doInBackground(String... strings) {
            Company company;
            if(internetConnectivity.isNetworkAvailable(getApplicationContext())) {

                company = retrieveStockPrice
                        .requestCompanyPriceOnline(
                                "ASX:"+intent.getStringExtra("companyCode"))
                        .getListedCompanies().get(0);

                if(company.getCompanySymbol().equals("API Limit")){
                    company = retrieveStockPrice
                            .getStockPricesInternal(
                                    "ASX:"+intent.getStringExtra("companyCode"));
                }
            }else{
                company = retrieveStockPrice
                        .getStockPricesInternal(
                                "ASX:"+intent.getStringExtra("companyCode"));

            }
            return company;
        }
    }

    private void generateMainUI(Company company){
        TextView companyName = findViewById(R.id.nameValue);
        companyName.setText(intent.getStringExtra("companyName"));

        TextView companyCode = findViewById(R.id.codeValue);
        companyCode.setText(intent.getStringExtra("companyCode"));

        TextView companyIndustry = findViewById(R.id.industryValue);
        companyIndustry.setText(intent.getStringExtra("companyIndustry"));

        TextView openPrice = findViewById(R.id.openValue);
        openPrice.setText(String.valueOf(company.getCompanyStockPrices().get(0).getDailyOpen()));

        TextView highPrice = findViewById(R.id.highValue);
        highPrice.setText(String.valueOf(company.getCompanyStockPrices().get(0).getDailyHigh()));

        TextView lowPrice = findViewById(R.id.lowValue);
        lowPrice.setText(String.valueOf(company.getCompanyStockPrices().get(0).getDailyLow()));

        TextView closePrice = findViewById(R.id.closeValue);
        closePrice.setText(String.valueOf(company.getCompanyStockPrices().get(0).getDailyClose()));

        TextView volume = findViewById(R.id.volumeValue);
        volume.setText(String.valueOf(company.getCompanyStockPrices().get(0).getDailyVolume()));

        AnyChartView companyChart = findViewById(R.id.stockPriceChart);
        LoadChartClass loadChartClass = new LoadChartClass();
        companyChart.setChart(loadChartClass.loadAnyChart(company, intent.getStringExtra("companyName")));



        FavouriteListManager favouriteList = new FavouriteListManager(getApplicationContext(),
                getWindow().getDecorView().getRootView());
        ImageView favouriteButton = findViewById(R.id.favouriteButton);

        if(favouriteList.detectFavouriteCompany(
                intent.getStringExtra("companyName"),
                intent.getStringExtra("companyCode"),
                intent.getStringExtra("companyIndustry"))){

            Drawable drawable = getResources().getDrawable(R.drawable.ic_red_favourite);
            drawable.setBounds(0, 0, favouriteButton.getWidth(), favouriteButton.getHeight());
            favouriteButton.setBackground(drawable);
        }else{
            Drawable drawable = getResources().getDrawable(R.drawable.ic_favorite_24px);
            drawable.setBounds(0, 0, favouriteButton.getWidth(), favouriteButton.getHeight());
            favouriteButton.setBackground(drawable);
        }

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                favouriteList.saveToFavouriteList(
                        intent.getStringExtra("companyName"),
                        "ASX:"+intent.getStringExtra("companyCode"),
                        intent.getStringExtra("companyIndustry"));
                CompaniesListAdapter companiesListAdapter = new CompaniesListAdapter(
                        CompanyActivity.this,
                        getApplicationContext(),
                        favouriteList.getFavouriteList()
                );
                companiesListAdapter.notifyDataSetChanged();
                for(int i=0; i<favouriteList.getFavouriteList().size(); i++){
                    companiesListAdapter.notifyItemInserted(i);
                    companiesListAdapter.notifyItemChanged(i);
                }
            }
        });
    }

    private void initiateUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        intent = getIntent();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favourite) {
            Intent intent = new Intent(this,FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(this,NotificationActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
