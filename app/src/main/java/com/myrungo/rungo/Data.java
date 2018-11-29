package com.myrungo.rungo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private boolean isRunning;
    private long time;
    private long timeStopped;
    private boolean isFirstTime;
    private boolean signal;

    private double distanceM;
    private double curSpeed;
    private double maxSpeed;

    private onGpsServiceUpdate onGpsServiceUpdate;

    private List<LatLng> positions = new ArrayList<>();

    public interface onGpsServiceUpdate {
        void update();
    }

    public void setOnGpsServiceUpdate(onGpsServiceUpdate onGpsServiceUpdate) {
        this.onGpsServiceUpdate = onGpsServiceUpdate;
    }

    public void update() {
        onGpsServiceUpdate.update();
    }

    public Data() {
        isRunning = false;
        distanceM = 0;
        curSpeed = 0;
        maxSpeed = 0;
        timeStopped = 0;
        positions.clear();
    }

    public Data(onGpsServiceUpdate onGpsServiceUpdate) {
        this();
        setOnGpsServiceUpdate(onGpsServiceUpdate);
    }

    public void addDistance(double distance) {
        distanceM = distanceM + distance;
    }

    public double getDistance() {
        return distanceM;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAverageSpeed() {
        double average;
        String units;
        if (time <= 0) {
            average = 0.0;
        } else {
            average = (distanceM / (time / 1000.0)) * 3.6;
        }
        return average;
    }

    public double getAverageSpeedMotion() {
        long motionTime = time - timeStopped;
        double average;
        if (motionTime <= 0) {
            average = 0.0;
        } else {
            average = (distanceM / (motionTime / 1000.0)) * 3.6;
        }
        return average;
    }

    public void setCurSpeed(double curSpeed) {
        this.curSpeed = curSpeed;
        if (curSpeed > maxSpeed) {
            maxSpeed = curSpeed;
        }
    }

    public void addPosition(LatLng position) {
        positions.add(position);
    }

    public LatLng getLastPosition() {
        return positions.get(positions.size() - 1);
    }

    public List<LatLng> getPositions() {
        return positions;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setTimeStopped(long timeStopped) {
        this.timeStopped += timeStopped;
    }

    public double getCurSpeed() {
        return curSpeed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSignal() {
        return signal;
    }

    public void setSignal(boolean x) {
        signal = x;
    }

}
