package com.myrungo.rungo.custom;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.base.cat.BaseCatContract;

import java.util.List;

public interface CustomContract extends BaseCatContract {

    interface View extends BaseCatContract.View {

        void showAvailableCostumes(@NonNull final Task<List<String>> listTask);

    }

    interface Presenter<V extends View> extends BaseCatContract.Presenter<V> {

        void asyncSaveNewCostume(@NonNull final String newCostume);

        @NonNull
        Task<List<String>> asyncGetUserRewards();

        void asyncUpdateUserRewards(@NonNull final String preferredCostume);

    }

}
