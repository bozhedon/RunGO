package com.myrungo.rungo.models;

/**
 * In firestore fields will be named like parameter's fields
 * For example: if in DB field == "start_time", but model's field == "startTime"
 * it will be renamed to "startTime"
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class Training {

    private double averageSpeed = 0;

    private double distance = 0;

    private long startTime = 0;

    private long endTime = 0;

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

}
