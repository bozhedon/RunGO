package com.myrungo.rungo.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.myrungo.rungo.R;
import com.myrungo.rungo.models.UserTimeIntervalInfo;
import com.myrungo.rungo.models.UserTimeIntervalsInfo;
import com.myrungo.rungo.utils.Utils;
import com.yandex.metrica.YandexMetrica;

import static com.myrungo.rungo.profile.Constants.POSITION_KEY;

public final class TimeIntervalFragment extends Fragment {

    @NonNull
    private final String weekInfoTag = "weekInfo";

    @NonNull
    private final String monthInfoTag = "monthInfo";

    @NonNull
    private final String yearInfoTag = "yearInfo";

    @Nullable
    private ProfileContract.View profileFragment;

    @Nullable
    private UserTimeIntervalsInfo weekInfo;

    @Nullable
    private UserTimeIntervalsInfo monthInfo;

    @Nullable
    private UserTimeIntervalsInfo yearInfo;

    @Nullable
    @Override
    public final View onCreateView(
            @NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        @NonNull final View view =
                inflater.inflate(R.layout.fragment_user_time_interval_achievments, container, false);

        if (getArguments() != null) {
            final int position = getArguments().getInt(POSITION_KEY);

            @NonNull final TextView thisTimeInterval = view.findViewById(R.id.thisTimeInterval);
            @NonNull final TextView previousTimeInterval = view.findViewById(R.id.previousTimeInterval);

            @NonNull String thisTimeIntervalString = "";
            @NonNull String previousTimeIntervalString = "";

            if (position == 0) {
                thisTimeIntervalString = "Эта неделя";
                previousTimeIntervalString = "Прошлая неделя";
            } else if (position == 1) {
                thisTimeIntervalString = "Этот месяц";
                previousTimeIntervalString = "Прошлый месяц";
            } else if (position == 2) {
                thisTimeIntervalString = "Этот год";
                previousTimeIntervalString = "Прошлый год";
            }

            thisTimeInterval.setText(thisTimeIntervalString);
            previousTimeInterval.setText(previousTimeIntervalString);

            if (savedInstanceState != null) {
                if (position == 0) {
                    weekInfo = savedInstanceState.getParcelable(weekInfoTag);
                } else if (position == 1) {
                    monthInfo = savedInstanceState.getParcelable(monthInfoTag);
                } else if (position == 2) {
                    yearInfo = savedInstanceState.getParcelable(yearInfoTag);
                }
            }

            startWork(position, view);
        }

        return view;
    }

    @NonNull
    private ProfileContract.View getProfileFragment() {
        if (profileFragment == null) {
            @Nullable final FragmentActivity activity = getActivity();

            if (activity == null) {
                @NonNull final NullPointerException exception = new NullPointerException("activity == null");

                reportError(exception);

                throw exception;
            }

            @NonNull final FragmentManager fragmentManager = activity.getSupportFragmentManager();

            for (@NonNull final Fragment fragment : fragmentManager.getFragments()) {
                if (fragment instanceof ProfileFragment) {
                    profileFragment = (ProfileContract.View) fragment;
                    return profileFragment;
                }
            }

            @NonNull final NullPointerException exception = new NullPointerException("ProfileFragment does not exist");

            reportError(exception);

            throw exception;
        }

        return profileFragment;
    }

    @NonNull
    private UserTimeIntervalsInfo getWeekInfo() {
        if (weekInfo == null) {
            weekInfo = getProfileFragment().getWeekInfo();
        }

        return weekInfo;
    }

    @NonNull
    private UserTimeIntervalsInfo getMonthInfo() {
        if (monthInfo == null) {
            monthInfo = getProfileFragment().getMonthInfo();
        }

        return monthInfo;
    }

    @NonNull
    private UserTimeIntervalsInfo getYearInfo() {
        if (yearInfo == null) {
            yearInfo = getProfileFragment().getYearInfo();
        }

        return yearInfo;
    }

    private void startWork(final int position, @NonNull final View view) {
        if (position == 0) {
            fillFields(getWeekInfo(), view);
        } else if (position == 1) {
            fillFields(getMonthInfo(), view);
        } else if (position == 2) {
            fillFields(getYearInfo(), view);
        }
    }

    @SuppressLint("SetTextI18n")
    private void fillFields(@NonNull final UserTimeIntervalsInfo timeIntervalTrainingsInfo, @NonNull final View view) {
        @Nullable final UserTimeIntervalInfo currentTimeIntervalInfo = timeIntervalTrainingsInfo.currentTime;

        @Nullable final UserTimeIntervalInfo previousTimeIntervalInfo = timeIntervalTrainingsInfo.previousTime;

        setTotalDistance((TextView) view.findViewById(R.id.distanceThisValue), currentTimeIntervalInfo);

        setTotalNumberTrainings((TextView) view.findViewById(R.id.trainingsThisValue), currentTimeIntervalInfo);

        setTotalAverageSpeed((TextView) view.findViewById(R.id.averageSpeedThisValue), currentTimeIntervalInfo);


        setTotalDistance((TextView) view.findViewById(R.id.distancePreviousValue), previousTimeIntervalInfo);

        setTotalNumberTrainings((TextView) view.findViewById(R.id.trainingsPreviousValue), previousTimeIntervalInfo);

        setTotalAverageSpeed((TextView) view.findViewById(R.id.averageSpeedPreviousValue), previousTimeIntervalInfo);
    }

    private void setTotalAverageSpeed(@NonNull final TextView view, @Nullable final UserTimeIntervalInfo timeIntervalInfo) {
        @NonNull final String totalAverageSpeed;
        if (timeIntervalInfo != null) {
            totalAverageSpeed = Utils.roundIfNeeded(timeIntervalInfo.totalAverageSpeed);
        } else {
            totalAverageSpeed = "";
        }

        view.setText(totalAverageSpeed);
    }

    private void setTotalNumberTrainings(@NonNull final TextView view, @Nullable final UserTimeIntervalInfo timeIntervalInfo) {
        @NonNull final String totalNumberTrainings;

        if (timeIntervalInfo != null) {
            totalNumberTrainings = String.valueOf(timeIntervalInfo.totalNumberTrainings);
        } else {
            totalNumberTrainings = "";
        }

        view.setText(totalNumberTrainings);
    }

    private void setTotalDistance(@NonNull final TextView view, @Nullable final UserTimeIntervalInfo timeIntervalInfo) {
        @NonNull final String currentTotalDistance;

        if (timeIntervalInfo != null) {
            currentTotalDistance = Utils.roundIfNeeded(timeIntervalInfo.totalDistance);
        } else {
            currentTotalDistance = "";
        }

        view.setText(currentTotalDistance);
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(weekInfoTag, getWeekInfo());
        outState.putParcelable(monthInfoTag, getMonthInfo());
        outState.putParcelable(yearInfoTag, getYearInfo());
    }

    private void reportError(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        YandexMetrica.reportUnhandledException(throwable);
    }

}
