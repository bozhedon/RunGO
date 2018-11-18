package com.myrungo.rungo.models;

import android.support.annotation.NonNull;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
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

    @NonNull
    private String regDate = "";

    /**
     * Primary key
     */
    @NonNull
    private String uid;

    private int age;

    @NonNull
    private String costume = "";

    private int height;

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
    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(@NonNull final String regDate) {
        this.regDate = regDate;
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
