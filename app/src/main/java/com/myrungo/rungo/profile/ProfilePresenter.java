package com.myrungo.rungo.profile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.myrungo.rungo.base.cat.BaseCatPresenter;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.models.Training;
import com.myrungo.rungo.models.UserTimeIntervalInfo;
import com.myrungo.rungo.models.UserTimeIntervalsInfo;
import com.myrungo.rungo.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class ProfilePresenter
        extends BaseCatPresenter<ProfileContract.View>
        implements ProfileContract.Presenter<ProfileContract.View> {

    @Nullable
    private List<Training> currentUserTrainings;

    @Override
    public void onViewCreate() {
        getView().showProgressIndicator();
        getView().dressUp();
    }

    @NonNull
    private List<Training> getCurrentUserTrainings() {
        if (currentUserTrainings == null) {
            currentUserTrainings = getUserTrainings();
        }

        return currentUserTrainings;
    }

    @NonNull
    public UserTimeIntervalsInfo getWeekInfo() {
        return calculateTrainingsInfoFor(TimeInterval.WEEK);
    }

    @NonNull
    public UserTimeIntervalsInfo getMonthInfo() {
        return calculateTrainingsInfoFor(TimeInterval.MONTH);
    }

    @NonNull
    public UserTimeIntervalsInfo getYearInfo() {
        return calculateTrainingsInfoFor(TimeInterval.YEAR);
    }

    @Override
    public void onDressUpComplete() {
        try {
            @NonNull final DBUser currentUserDBInfo = getDBCurrentUserInfo();

            getView().setUpName(currentUserDBInfo.getName());

            @NonNull final List<Training> currentUserTrainings = getUserTrainings();

            getView().setUpTotalDistance(getTotalDistance(currentUserTrainings));
        } catch (@NonNull final Exception e) {
            e.printStackTrace();
            reportError(e);
        }

        getView().hideProgressIndicator();
    }

    @NonNull
    private UserTimeIntervalsInfo calculateTrainingsInfoFor(@NonNull final TimeInterval timeField) {
        final int calendarTimeInterval;

        if (timeField == TimeInterval.WEEK) {
            calendarTimeInterval = Calendar.WEEK_OF_YEAR;
        } else if (timeField == TimeInterval.MONTH) {
            calendarTimeInterval = Calendar.MONTH;
        } else if (timeField == TimeInterval.YEAR) {
            calendarTimeInterval = Calendar.YEAR;
        } else {
            @NonNull final NullPointerException exception = new NullPointerException("Wrong time interval");
            reportError(exception);

            throw exception;
        }

        @NonNull final Calendar currentCalendar = Calendar.getInstance();
        final int currentTimeInterval = currentCalendar.get(calendarTimeInterval);
        currentCalendar.add(calendarTimeInterval, -1);
        final int previousTimeInterval = currentCalendar.get(calendarTimeInterval);

        double currentTimeIntervalTotalDistance = 0;
        double previousTimeIntervalTotalDistance = 0;

        long currentTimeIntervalTotalNumberTrainings = 0;
        long previousTimeIntervalTotalNumberTrainings = 0;

        double currentTimeIntervalAverageSpeed = 0;
        double previousTimeIntervalAverageSpeed = 0;

        for (@Nullable final Training training : getCurrentUserTrainings()) {
            if (training == null) {
                continue;
            }

            final boolean trainingWasOnCurrentTimeInterval =
                    wasTrainingOnThis(
                            currentTimeInterval,
                            training.getStartTime(),
                            training.getEndTime(),
                            calendarTimeInterval
                    );

            final boolean trainingWasOnPreviousTimeInterval =
                    wasTrainingOnThis(
                            previousTimeInterval,
                            training.getStartTime(),
                            training.getEndTime(),
                            calendarTimeInterval
                    );

            if (trainingWasOnCurrentTimeInterval) {
                currentTimeIntervalTotalNumberTrainings++;
                currentTimeIntervalTotalDistance += training.getDistance();
                currentTimeIntervalAverageSpeed += training.getAverageSpeed();
            } else if (trainingWasOnPreviousTimeInterval) {
                previousTimeIntervalTotalNumberTrainings++;
                previousTimeIntervalTotalDistance += training.getDistance();
                previousTimeIntervalAverageSpeed += training.getAverageSpeed();
            }
        }

        @NonNull final UserTimeIntervalInfo currentTimeIntervalInfo = new UserTimeIntervalInfo(
                currentTimeIntervalTotalDistance,
                currentTimeIntervalTotalNumberTrainings,
                currentTimeIntervalAverageSpeed);

        @NonNull final UserTimeIntervalInfo previousTimeIntervalInfo = new UserTimeIntervalInfo(
                previousTimeIntervalTotalDistance,
                previousTimeIntervalTotalNumberTrainings,
                previousTimeIntervalAverageSpeed);

        return new UserTimeIntervalsInfo(currentTimeIntervalInfo, previousTimeIntervalInfo);
    }

    private boolean wasTrainingOnThis(
            final int timeInterval,
            final long startTime,
            final long endTime,
            final int calendarTimeInterval) {
        final int startWeek = getTimeIntervalSerialNumber(startTime, calendarTimeInterval);

        final int endWeek = getTimeIntervalSerialNumber(endTime, calendarTimeInterval);

        return startWeek == timeInterval && endWeek == timeInterval;
    }

    private int getTimeIntervalSerialNumber(final long time, final int calendarTimeInterval) {
        @NonNull final Date date = new Date(time);
        @NonNull final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(calendarTimeInterval);
    }

    @NonNull
    private String getTotalDistance(@NonNull final List<Training> currentUserTrainings) {
        double totalDistance = 0;

        for (@Nullable final Training training : currentUserTrainings) {
            if (training == null) {
                continue;
            }

            totalDistance += training.getDistance();
        }

        return Utils.roundIfNeeded(totalDistance) + " км";
    }

    @NonNull
    private List<Training> getUserTrainings() {
        @NonNull final List<Training> currentUserTrainings = new ArrayList<>();

        try {
            currentUserTrainings.addAll(getMainView().getCurrentUserTrainings());
        } catch (@NonNull final Exception e) {
            e.printStackTrace();
        }

        return currentUserTrainings;
    }

    @NonNull
    private DBUser getDBCurrentUserInfo() throws Exception {
        return getMainView().getCurrentUserInfo();
    }

    @NonNull
    private MainContract.View getMainView() {
        return (MainContract.View) getActivity();
    }

    enum TimeInterval {
        WEEK,
        MONTH,
        YEAR
    }

}
