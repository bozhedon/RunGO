package com.myrungo.rungo.models;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class UserTimeIntervalsInfo implements Parcelable {

    @Nullable
    public final UserTimeIntervalInfo currentTime;

    @Nullable
    public final UserTimeIntervalInfo previousTime;

    public UserTimeIntervalsInfo(
            @NonNull final UserTimeIntervalInfo currentTime,
            @NonNull final UserTimeIntervalInfo previousTime) {
        this.currentTime = currentTime;
        this.previousTime = previousTime;
    }

    private UserTimeIntervalsInfo(Parcel in) {
        currentTime = in.readParcelable(UserTimeIntervalInfo.class.getClassLoader());
        previousTime = in.readParcelable(UserTimeIntervalInfo.class.getClassLoader());
    }

    @Override
    public final void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeParcelable(currentTime, flags);
        dest.writeParcelable(previousTime, flags);
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    public static final Creator<UserTimeIntervalsInfo> CREATOR = new Creator<UserTimeIntervalsInfo>() {
        @Override
        public final UserTimeIntervalsInfo createFromParcel(@NonNull final Parcel in) {
            return new UserTimeIntervalsInfo(in);
        }

        @Override
        public final UserTimeIntervalsInfo[] newArray(final int size) {
            return new UserTimeIntervalsInfo[size];
        }
    };

}
