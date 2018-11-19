package com.myrungo.rungo.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public abstract class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

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
    final public void onBindView(@NonNull final V view) {
        this.view = view;
    }

    @Override
    final public void onUnbindView() {
        view = null;
    }

    @NonNull
    final protected Context getContext() {
        try {
            @NonNull final Fragment fragment = (Fragment) getView();

            @Nullable final FragmentActivity fragmentActivity = fragment.getActivity();

            if (fragmentActivity == null) {
                throw new NullPointerException("fragmentActivity == null");
            }

            return fragmentActivity;
        } catch (Exception e) {
            return (Activity) getView();
        }
    }

}
