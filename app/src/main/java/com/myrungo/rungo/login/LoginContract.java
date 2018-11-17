package com.myrungo.rungo.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

interface LoginContract {

    interface View {

        void showProgressIndicator();

        void createSignInIntent();

        void hideRefreshIndicator();

        void hideProgressDialog();

        void hideErrorTextView();

        void showErrorTextView();

        void goToMainScreen();

        void showMessage(@Nullable final String message);

        void setErrorText(@Nullable final String message);

    }

    interface Presenter<V extends View> {

        void onBindView(@NonNull final V view);

        void onUnbindView();

        void onViewCreate();

        void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data);

    }

}
