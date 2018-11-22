package com.myrungo.rungo.profile;

import com.myrungo.rungo.base.cat.BaseCatContract;

public interface ProfileContract extends BaseCatContract {

    interface View extends BaseCatContract.View {

        void dressUp();

    }

    interface Presenter<V extends View> extends BaseCatContract.Presenter<V> {


    }

}
