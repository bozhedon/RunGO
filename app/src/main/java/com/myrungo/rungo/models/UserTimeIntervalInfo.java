package com.myrungo.rungo.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public final class UserTimeIntervalInfo implements Parcelable {

    public static final Creator<UserTimeIntervalInfo> CREATOR = new Creator<UserTimeIntervalInfo>() {
        @Override
        public final UserTimeIntervalInfo createFromParcel(@NonNull final Parcel in) {
            return new UserTimeIntervalInfo(in);
        }

        @Override
        public final UserTimeIntervalInfo[] newArray(final int size) {
            return new UserTimeIntervalInfo[size];
        }
    };

    public final double totalDistance;
    public final long totalNumberTrainings;
    public final double totalAverageSpeed;

    public UserTimeIntervalInfo(double totalDistance, long totalNumberTrainings, double totalAverageSpeed) {
        this.totalDistance = totalDistance;
        this.totalNumberTrainings = totalNumberTrainings;
        this.totalAverageSpeed = totalAverageSpeed;
    }

    private UserTimeIntervalInfo(@NonNull final Parcel in) {
        totalDistance = in.readDouble();
        totalNumberTrainings = in.readLong();
        totalAverageSpeed = in.readDouble();
    }

    @Override
    public final void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeDouble(totalDistance);
        dest.writeLong(totalNumberTrainings);
        dest.writeDouble(totalAverageSpeed);
    }

    @Override
    public final int describeContents() {
        return 0;
    }

}
