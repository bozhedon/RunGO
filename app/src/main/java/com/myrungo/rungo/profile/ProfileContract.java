package com.myrungo.rungo.profile;

import android.support.annotation.NonNull;

import com.myrungo.rungo.base.cat.BaseCatContract;
import com.myrungo.rungo.models.UserTimeIntervalsInfo;

public interface ProfileContract extends BaseCatContract {

    interface View extends BaseCatContract.View {

        void dressUp();

        void setUpName(@NonNull final String name);

        void setUpTotalDistance(@NonNull final String totalDistance);

        @NonNull
        UserTimeIntervalsInfo getWeekInfo();

        @NonNull
        UserTimeIntervalsInfo getMonthInfo();

        @NonNull
        UserTimeIntervalsInfo getYearInfo();

    }

    interface Presenter<V extends View> extends BaseCatContract.Presenter<V> {

        @NonNull
        UserTimeIntervalsInfo getWeekInfo();

        @NonNull
        UserTimeIntervalsInfo getMonthInfo();

        @NonNull
        UserTimeIntervalsInfo getYearInfo();

        void onDressUpComplete();

    }

}
