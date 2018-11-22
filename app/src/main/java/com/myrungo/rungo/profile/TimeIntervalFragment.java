package com.myrungo.rungo.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myrungo.rungo.R;

import static com.myrungo.rungo.profile.Constants.POSITION_KEY;

public final class TimeIntervalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        @NonNull final View view =
                inflater.inflate(R.layout.fragment_user_time_interval_achievments, container, false);

        if (getArguments() != null) {
            int position = getArguments().getInt(POSITION_KEY);

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
        }

        return view;
    }

}
