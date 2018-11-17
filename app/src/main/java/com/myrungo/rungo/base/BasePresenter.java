package com.myrungo.rungo.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class BasePresenter<V extends BaseContract.View>
        implements BaseContract.Presenter<V> {

    @Nullable
    private V view = null;

    @NonNull
    final protected V getView() {
        if (view == null) {
            throw new RuntimeException("view == null");
        }

        return view;
    }

    @Override
    public void onBindView(@NonNull V view) {
        this.view = view;
    }

    @Override
    public void onUnbindView() {
        this.view = null;
    }

}
