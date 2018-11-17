package com.myrungo.rungo.login;

import android.support.annotation.NonNull;

import com.myrungo.rungo.base.BaseContract;

public interface LoginContract extends BaseContract {

    interface View extends BaseContract.View {

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean isEmailAndPasswordValid();

        boolean isPhoneNumberValid();

        void goToMain();

        void disableSignInButton();

        void enableSignInButton();

    }

    interface Presenter<V extends View> extends BaseContract.Presenter<V> {

        void signUpWithEmail(final @NonNull String email, final @NonNull String password);

        void signUpWithPhoneNumber(final @NonNull String phoneNumber);

        void signInWithEmailAndPassword(final @NonNull String email, final @NonNull String password);

        void signInWithPhoneNumber(final @NonNull String phoneNumber);

    }

}
