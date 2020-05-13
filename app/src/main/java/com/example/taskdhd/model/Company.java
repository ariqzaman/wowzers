package com.example.taskdhd.model;

import java.util.ArrayList;

public class Company {

    private String companySymbol;
    private ArrayList<DailyPrice> companyStockPrices;

    public Company(String companySymbol, ArrayList<DailyPrice> companyStockPrices) {
        this.companySymbol = companySymbol;
        this.companyStockPrices = companyStockPrices;
    }

    public String getCompanySymbol() {
        return companySymbol;
    }

    public void setCompanySymbol(String companySymbol) {
        this.companySymbol = companySymbol;
    }

    public ArrayList<DailyPrice> getCompanyStockPrices() {
        return companyStockPrices;
    }

    public void setCompanyStockPrices(ArrayList<DailyPrice> companyStockPrices) {
        this.companyStockPrices = companyStockPrices;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companySymbol='" + companySymbol + '\'' +
                ", companyStockPrices=" + companyStockPrices +
                '}';
    }
}
