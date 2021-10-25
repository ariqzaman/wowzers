package com.example.taskdhd.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.example.taskdhd.R;
import com.example.taskdhd.model.Company;
import com.example.taskdhd.presenter.InternetConnectivity;
import com.example.taskdhd.presenter.RetrieveStockPrices;
import com.example.taskdhd.presenter.StockIndexAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RetrieveStockPrices retrieveStockPrice;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        retrieveStockPrice = new RetrieveStockPrices(getApplicationContext());
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        new asyncLoad().execute();
    }

    private class asyncLoad extends AsyncTask<String[], Integer, ArrayList<Company>> {

        String[] arrayIndex = {"^AXJO","^AFLI"};
        InternetConnectivity internetConnectivity = new InternetConnectivity();

        @Override
        protected void onPreExecute() {
            initiateUI();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Company> arrayIndexes) {
            System.out.println(arrayIndex.toString());
            generateMainUI(arrayIndexes);

            ProgressBar progressBar = findViewById(R.id.stockIndexProgress);
            progressBar.setVisibility(View.INVISIBLE);
            //System.out.println(arrayIndexes.toString());
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(arrayIndexes);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<Company> doInBackground(String[]...indexes) {
            ArrayList<Company> arrayIndexes;
            if(internetConnectivity.isNetworkAvailable(getApplicationContext())) {
                arrayIndexes = retrieveStockPrice
                        .requestIndexesPriceOnline(arrayIndex)
                        .getListedCompanies();
                for(int i=0; i<arrayIndexes.size(); i++){
                    if(arrayIndexes.get(i).getCompanySymbol().equals("API Limit")){
                        arrayIndexes = retrieveStockPrice
                                .getIndexesPriceInternal(arrayIndex)
                                .getListedCompanies();
                        break;
                    }
                }
            }else{
                arrayIndexes = retrieveStockPrice
                        .getIndexesPriceInternal(arrayIndex)
                        .getListedCompanies();
            }

            //System.out.println(arrayIndexes.get(0).getCompanySymbol());
            return arrayIndexes;
        }
    }


    private void generateMainUI(ArrayList<Company> arrayIndexes){

        RecyclerView recyclerView = findViewById(R.id.stockIndexes_recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.Adapter adapter = new StockIndexAdapter(arrayIndexes);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new asyncLoad().execute();
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

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            // Same page
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
