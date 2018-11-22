package com.myrungo.rungo.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.myrungo.rungo.MainActivity;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BaseActivity;

import java.util.Arrays;
import java.util.List;

public final class LoginActivity
        extends BaseActivity<LoginContract.View, LoginContract.Presenter<LoginContract.View>>
        implements SwipeRefreshLayout.OnRefreshListener, LoginContract.View {

    static final int RC_SIGN_IN = 123;

    @NonNull
    private final static List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );

    @Nullable
    private LoginContract.Presenter<LoginContract.View> presenter;

    @Override
    final protected void setupPresenter() {
        presenter = new LoginPresenter();
    }

    @NonNull
    @Override
    final protected LoginContract.Presenter<LoginContract.View> getPresenter() {
        if (presenter == null) {
            @NonNull final NullPointerException exception = new NullPointerException("presenter == null");
            reportError(exception);

            throw exception;
        }

        return presenter;
    }

    @Override
    final protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSwipeRefreshLayout().setOnRefreshListener(this);

        getPresenter().onViewCreate();
    }

    //must be called ONLY from onCreate
    @Override
    public void createSignInIntent() {
        @NonNull final Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false, true)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.icon_head_400x400)
                .setTheme(R.style.AppTheme)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    final protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            @Nullable final Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void hideRefreshIndicator() {
        getSwipeRefreshLayout().setRefreshing(false);
    }

    @Override
    public void goToMainScreen() {
        @NonNull final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void setErrorText(@Nullable final String message) {
        getErrorTextView().setText(message);
    }

    @Override
    public void hideProgressIndicator() {
        getProgressBarLayout().setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        getProgressBarLayout().setVisibility(View.VISIBLE);
    }

    @Override
    public void showMessage(@Nullable final String message) {
        if (message != null) {
            Snackbar.make(getCoordinatorLayout(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        createSignInIntent();
    }

    @Override
    public void showErrorTextView() {
        getErrorTextView().setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorTextView() {
        getErrorTextView().setVisibility(View.GONE);
    }

    @NonNull
    private ViewGroup getProgressBarLayout() {
        @Nullable final ViewGroup layoutWithProgressBar = findViewById(R.id.layoutWithProgressBar);

        if (layoutWithProgressBar == null) {
            @NonNull final NullPointerException exception = new NullPointerException("layoutWithProgressBar == null");
            reportError(exception);

            throw exception;
        }

        return layoutWithProgressBar;
    }

    @NonNull
    private CoordinatorLayout getCoordinatorLayout() {
        @Nullable final CoordinatorLayout activityFirebaseUICL = findViewById(R.id.activityFirebaseUICL);

        if (activityFirebaseUICL == null) {
            @NonNull final RuntimeException exception = new NullPointerException("activityFirebaseUICL == null");
            reportError(exception);

            throw exception;
        }

        return activityFirebaseUICL;
    }

    @NonNull
    private SwipeRefreshLayout getSwipeRefreshLayout() {
        @Nullable final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        if (swipeRefreshLayout == null) {
            @NonNull final NullPointerException exception = new NullPointerException("swipeRefreshLayout == null");
            reportError(exception);

            throw exception;
        }

        return swipeRefreshLayout;
    }

    @NonNull
    private TextView getErrorTextView() {
        @Nullable final TextView errorTextView = findViewById(R.id.errorText);

        if (errorTextView == null) {
            @NonNull final NullPointerException exception = new NullPointerException("errorTextView == null");
            reportError(exception);

            throw exception;
        }

        return errorTextView;
    }

}
