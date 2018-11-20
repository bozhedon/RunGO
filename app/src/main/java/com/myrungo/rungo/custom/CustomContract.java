package com.myrungo.rungo.custom;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.base.BaseFragmentContract;

import java.util.List;

public interface CustomContract extends BaseFragmentContract {

    interface View extends BaseFragmentContract.View {

        void dressUp();

        void showAvailableCostumes(@NonNull final Task<List<String>> listTask);

    }

    interface Presenter<V extends View> extends BaseFragmentContract.Presenter<V> {

        void saveNewCostume(@NonNull final String newCostume);

        @NonNull
        Task<String> asyncGetPreferredCostume();

        @NonNull
        Task<List<String>> asyncGetUserRewards();

    }

}
