package com.example.taskdhd.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.taskdhd.R;
import com.example.taskdhd.presenter.CompaniesListAdapter;
import com.example.taskdhd.presenter.RetrieveCompaniesList;
import com.example.taskdhd.presenter.StockIndexAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RetrieveCompaniesList retrieveCompaniesList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        new asyncLoad().execute();
    }

    private class asyncLoad extends AsyncTask<String, Integer, ArrayList<ArrayList<String>>> {

        @Override
        protected void onPreExecute() {
            initiateUI();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> companiesList) {
            generateMainUI(companiesList);
            ProgressBar progressBar = findViewById(R.id.stockListProgress);
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(companiesList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String... strings) {
            retrieveCompaniesList = new RetrieveCompaniesList();
            return retrieveCompaniesList.retrieveCompanies();
        }
    }

    private void generateMainUI(ArrayList<ArrayList<String>> companiesList){
        EditText searchCompany = findViewById(R.id.searchCompanyView);
        searchCompany.setVisibility(View.VISIBLE);
        searchCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((CompaniesListAdapter) adapter).getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView = findViewById(R.id.stockRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        System.out.println(companiesList.size());
        adapter = new CompaniesListAdapter(this,getApplicationContext(), companiesList);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            // Same page
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
