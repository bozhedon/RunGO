package com.myrungo.rungo.models;

import android.support.annotation.NonNull;

/**
 * In firestore fields will be named like parameter's fields
 * For example: if in DB field == "reg_date", but model's field == "regDate"
 * it will be renamed to "regDate"
 */
@SuppressWarnings("unused")
public final class DBUser {

    @NonNull
    private String email = "";

    private boolean isAnonymous;

    @NonNull
    private String name = "";

    @NonNull
    private String phoneNumber = "";

    @NonNull
    private String photoUri = "";

    @NonNull
    private String provider = "";

    //must be EXACTLY with underscore, because of firebase authentication
    private long reg_date = 0L;

    /**
     * Primary key
     */
    @NonNull
    private String uid;

    private int age;

    @NonNull
    private String costume = "";

    private int height;

    private double totalDistance = 0.0;

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public DBUser() {
        this.uid = "";
    }

    public DBUser(@NonNull final String uid) {
        this.uid = uid.trim();
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull final String email) {
        this.email = email;
    }

    public boolean getIsAnonymous() {
        return isAnonymous;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull final String name) {
        this.name = name;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(@NonNull final String photoUri) {
        this.photoUri = photoUri;
    }

    @NonNull
    public String getProvider() {
        return provider;
    }

    public void setProvider(@NonNull final String provider) {
        this.provider = provider;
    }

    @NonNull
    public Long getReg_date() {
        return reg_date;
    }

    public void setReg_date(@NonNull final Long reg_date) {
        this.reg_date = reg_date;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    @NonNull
    public String getCostume() {
        return costume;
    }

    public void setCostume(@NonNull final String costume) {
        this.costume = costume;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setAnonymous(final boolean anonymous) {
        isAnonymous = anonymous;
    }

}
