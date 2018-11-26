package com.myrungo.rungo.start;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.myrungo.rungo.BuildConfig;
import com.myrungo.rungo.base.BasePresenter;

import static com.myrungo.rungo.start.StartActivity.REQUEST_APPLICATION_SETTINGS_FOR_ON_MAP_READY;
import static com.myrungo.rungo.start.StartActivity.REQUEST_APPLICATION_SETTINGS_FOR_ON_RESUME;

final class StartPresenter
        extends BasePresenter<StartContract.View>
        implements StartContract.Presenter<StartContract.View> {

    @Override
    public void onViewCreate() {
    }

    @Override
    public void onMapReady() {
        if (isLocationPermissionDeniedForOnMapReady()) return;

        getView().setMyLocationEnabled();
    }

    @Override
    public void onStart() {
        if (isLocationPermissionDeniedForOnResume()) return;

        getView().requestLocationUpdates();
    }

    @Override
    public void onRequestPermissionForOnMapReadyResult(int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (isLocationPermissionGranted(grantResults)) {
            // permission was granted

            onMapReady();
        } else if (shouldShowRequestPermissionRationale()) {
            getView().showRequestPermissionRationaleForOnMapReady();
        } else {
            //user denied permission with tap on "dont ask"
            getView().showGoSettingsForOnMapReadyDialog();
        }
    }

    @Override
    public void onRequestPermissionForOnResumeResult(int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (isLocationPermissionGranted(grantResults)) {
            // permission was granted
            onStart();
        } else if (shouldShowRequestPermissionRationale()) {
            getView().showRequestPermissionRationaleForOnResume();
        } else {
            //user denied permission with tap on "dont ask"
            getView().showGoSettingsForOnResumeDialog();
        }
    }

    @Override
    public void onApplicationSettingsRequestResult() {
        onMapReady();
    }

    @Override
    public void onShowGoSettingsForOnMapReadyDialogPositiveButtonClick() {
        @NonNull final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));

        getActivity().startActivityForResult(intent, REQUEST_APPLICATION_SETTINGS_FOR_ON_MAP_READY);
    }

    @Override
    public void onShowGoSettingsForOnMapReadyDialogNegativeButtonClick() {
        getActivity().finish();
    }

    @Override
    public void onShowGoSettingsForOnResumeDialogPositiveButtonClick() {
        @NonNull final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));

        getActivity().startActivityForResult(intent, REQUEST_APPLICATION_SETTINGS_FOR_ON_RESUME);
    }

    @Override
    public void onShowGoSettingsForResumeDialogNegativeButtonClick() {
        getActivity().finish();
    }

    @Override
    public void onShowRequestPermissionRationaleForOnResumePositiveButtonClick() {
        getView().requestLocationPermissionForOnResume();
    }

    @Override
    public void onShowRequestPermissionRationaleForOnResumeNegativeButtonClick() {
        getActivity().finish();
    }

    @Override
    public void onShowRequestPermissionRationaleForOnMapReadyPositiveButtonClick() {
        getView().requestLocationPermissionForOnMapReady();
    }

    @Override
    public void onShowRequestPermissionRationaleForOnMapReadyNegativeButtonClick() {
        getActivity().finish();
    }

    private boolean isLocationPermissionGranted(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationPermissionDeniedForOnMapReady() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLocationPermissionDenied()) {
                // Permission is not granted
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale()) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    getView().showRequestPermissionRationaleForOnMapReady();
                } else {
                    // No explanation needed; request the permission
                    getView().requestLocationPermissionForOnMapReady();
                }

                return true;
            }
        }

        return false;

    }

    private boolean isLocationPermissionDeniedForOnResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isLocationPermissionDenied()) {
                // Permission is not granted
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale()) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    getView().showRequestPermissionRationaleForOnResume();
                } else {
                    // No explanation needed; request the permission
                    getView().requestLocationPermissionForOnResume();
                }

                return true;
            }
        }

        return false;
    }

    private boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat
                .shouldShowRequestPermissionRationale(
                        getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) && ActivityCompat
                .shouldShowRequestPermissionRationale(
                        getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                );
    }

    private boolean isLocationPermissionDenied() {
        return ContextCompat
                .checkSelfPermission(
                        getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat
                .checkSelfPermission(
                        getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED;
    }

}
