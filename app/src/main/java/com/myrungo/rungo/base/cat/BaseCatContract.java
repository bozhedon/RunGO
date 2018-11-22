package com.myrungo.rungo.base.cat;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.base.BaseFragmentContract;

public interface BaseCatContract extends BaseFragmentContract {

    interface View extends BaseFragmentContract.View {

        void showProgressIndicator();

        void hideProgressIndicator();

    }

    interface Presenter<V extends View> extends BaseFragmentContract.Presenter<V> {

        @NonNull
        Task<String> asyncGetPreferredCostume();

        @NonNull
        String getPrefferedCostume();

    }

}
