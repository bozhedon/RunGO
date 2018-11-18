package com.myrungo.rungo.login;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.myrungo.rungo.base.BaseContract;

interface LoginContract extends BaseContract {

    interface View extends BaseContract.View {

        void createSignInIntent();

        void hideRefreshIndicator();

        void hideErrorTextView();

        void showErrorTextView();

        void goToMainScreen();

        void setErrorText(@Nullable final String message);

        void showProgressIndicator();

        void hideProgressIndicator();

    }

    interface Presenter<V extends View> extends BaseContract.Presenter<V> {

        void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data);

    }

}
