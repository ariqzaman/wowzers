package com.example.taskdhd.model;

public class DailyPrice {

    private String dailyDate;
    private double dailyOpen;
    private double dailyHigh;
    private double dailyLow;
    private double dailyClose;
    private int dailyVolume;

    public DailyPrice(String dailyDate, double dailyOpen, double dailyHigh, double dailyLow, double dailyClose, int dailyVolume) {
        this.dailyDate = dailyDate;
        this.dailyOpen = dailyOpen;
        this.dailyHigh = dailyHigh;
        this.dailyLow = dailyLow;
        this.dailyClose = dailyClose;
        this.dailyVolume = dailyVolume;
    }

    public String getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(String dailyDate) {
        this.dailyDate = dailyDate;
    }

    public double getDailyOpen() {
        return dailyOpen;
    }

    public void setDailyOpen(double dailyOpen) {
        this.dailyOpen = dailyOpen;
    }

    public double getDailyHigh() {
        return dailyHigh;
    }

    public void setDailyHigh(double dailyHigh) {
        this.dailyHigh = dailyHigh;
    }

    public double getDailyLow() {
        return dailyLow;
    }

    public void setDailyLow(double dailyLow) {
        this.dailyLow = dailyLow;
    }

    public double getDailyClose() {
        return dailyClose;
    }

    public void setDailyClose(double dailyClose) {
        this.dailyClose = dailyClose;
    }

    public int getDailyVolume() {
        return dailyVolume;
    }

    public void setDailyVolume(int dailyVolume) {
        this.dailyVolume = dailyVolume;
    }

    @Override
    public String toString() {
        return "DailyPrice{" +
                "dailyDate='" + dailyDate + '\'' +
                ", dailyOpen=" + dailyOpen +
                ", dailyHigh=" + dailyHigh +
                ", dailyLow=" + dailyLow +
                ", dailyClose=" + dailyClose +
                ", dailyVolume=" + dailyVolume +
                '}';
    }
}
