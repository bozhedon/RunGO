package com.myrungo.rungo.base;

import android.support.annotation.NonNull;
import android.view.View.OnClickListener;

public interface BaseContract {

    interface View {

        void showProgressDialog();

        void hideProgressDialog();

        void showMessage(@NonNull final String message);

        void showMessage(
                @NonNull final String message,
                @NonNull final String buttonString,
                @NonNull final OnClickListener clickListener
        );

    }

    interface Presenter<V extends View> {

        void onBindView(final @NonNull V view);

        void onUnbindView();

        void onStart();

    }

}
