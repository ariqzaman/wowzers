package com.example.taskdhd.model;

import java.util.ArrayList;

public class Companies {

    private ArrayList<Company> listedCompanies;

    public Companies(ArrayList<Company> listedCompanies) {
        this.listedCompanies = listedCompanies;
    }

    public ArrayList<Company> getListedCompanies() {
        return listedCompanies;
    }

    public void setListedCompanies(ArrayList<Company> listedCompanies) {
        this.listedCompanies = listedCompanies;
    }

    @Override
    public String toString() {
        return "Companies{" +
                "listedCompanies=" + listedCompanies +
                '}';
    }
}
