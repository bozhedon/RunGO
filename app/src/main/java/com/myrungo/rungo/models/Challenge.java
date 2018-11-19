package com.myrungo.rungo.models;

import android.support.annotation.NonNull;

/**
 * In firestore fields will be named like parameter's fields
 * For example: if in DB field == "img_URL", but model's field == "imgURL"
 * it will be renamed to "imgURL"
 */
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
