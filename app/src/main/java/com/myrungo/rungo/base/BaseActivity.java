package com.myrungo.rungo.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseActivity
        extends AppCompatActivity
        implements BaseContract.View {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupContentView();
        setupClickListeners();
        setPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        getPresenter().onUnbindView();
        super.onDestroy();
    }

    @Override
    final public void showMessage(@NonNull final String message) {
        Snackbar.make(getProgressBarLayout(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    final public void showMessage(
            @NonNull final String message,
            @NonNull final String buttonString,
            @NonNull final View.OnClickListener clickListener
    ) {
        Snackbar
                .make(getProgressBarLayout(), message, Snackbar.LENGTH_LONG)
                .setAction(buttonString, clickListener)
                .show();
    }

    @Override
    final public void hideProgressDialog() {
        getProgressBarLayout().setVisibility(View.GONE);
    }

    @Override
    final public void showProgressDialog() {
        getProgressBarLayout().setVisibility(View.VISIBLE);
    }

    @NonNull
    protected abstract BaseContract.Presenter getPresenter();

    protected abstract void setupContentView();

    protected abstract void setupClickListeners();

    protected abstract void setPresenter();

    @NonNull
    protected abstract ViewGroup getProgressBarLayout();

    @NonNull
    protected abstract CoordinatorLayout getCoordinatorLayout();

}
