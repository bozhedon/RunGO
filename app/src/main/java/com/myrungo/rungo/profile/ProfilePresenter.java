package com.myrungo.rungo.profile;

import com.myrungo.rungo.base.cat.BaseCatPresenter;

final class ProfilePresenter
        extends BaseCatPresenter<ProfileContract.View>
        implements ProfileContract.Presenter<ProfileContract.View> {

    @Override
    public void onViewCreate() {
        getView().showProgressIndicator();
        getView().dressUp();
    }

}
