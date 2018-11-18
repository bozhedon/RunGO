package com.myrungo.rungo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

abstract public class BaseActivity<V extends BaseContract.View, P extends BaseContract.Presenter<V>>
        extends AppCompatActivity
        implements BaseContract.View {

    protected abstract P getPresenter();

    protected abstract void setupPresenter();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPresenter();
    }

    @Override
    protected void onDestroy() {
        getPresenter().onUnbindView();
        super.onDestroy();
    }

    @Override
    public void showMessage(@Nullable final String message) {
        if (message != null) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

}
