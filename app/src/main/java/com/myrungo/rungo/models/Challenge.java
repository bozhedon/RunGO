package com.myrungo.rungo.models;

import android.support.annotation.NonNull;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class Challenge {

    private double distance = 0.0;

    private int hour = 0;

    private int minutes = 0;

    @NonNull
    private String reward = "";

    @NonNull
    private String imgURL = "";

    public double getDistance() {
        return distance;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    @NonNull
    public String getReward() {
        return reward;
    }

    @NonNull
    public String getImgURL() {
        return imgURL;
    }

}
