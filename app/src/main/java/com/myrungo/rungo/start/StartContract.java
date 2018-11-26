package com.myrungo.rungo.start;

import com.myrungo.rungo.base.BaseContract;

interface StartContract extends BaseContract {

    interface View extends BaseContract.View {

        void requestLocationUpdates();

        void setMyLocationEnabled();

        void showRequestPermissionRationaleForOnResume();

        void requestLocationPermissionForOnMapReady();

        void showRequestPermissionRationaleForOnMapReady();

        void requestLocationPermissionForOnResume();

        void showGoSettingsForOnMapReadyDialog();

        void showGoSettingsForOnResumeDialog();

    }

    interface Presenter<V extends View> extends BaseContract.Presenter<V> {

        void onStart();

        void onMapReady();

        void onRequestPermissionForOnMapReadyResult(int[] grantResults);

        void onRequestPermissionForOnResumeResult(int[] grantResults);

        void onApplicationSettingsRequestResult();

        void onShowGoSettingsForOnMapReadyDialogPositiveButtonClick();

        void onShowGoSettingsForOnMapReadyDialogNegativeButtonClick();

        void onShowRequestPermissionRationaleForOnResumePositiveButtonClick();

        void onShowRequestPermissionRationaleForOnResumeNegativeButtonClick();

        void onShowRequestPermissionRationaleForOnMapReadyPositiveButtonClick();

        void onShowRequestPermissionRationaleForOnMapReadyNegativeButtonClick();

        void onShowGoSettingsForOnResumeDialogPositiveButtonClick();

        void onShowGoSettingsForResumeDialogNegativeButtonClick();

    }

}
