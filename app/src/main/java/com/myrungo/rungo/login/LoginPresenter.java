package com.myrungo.rungo.login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BasePresenter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT;
import static com.firebase.ui.auth.ErrorCodes.DEVELOPER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.EMAIL_MISMATCH_ERROR;
import static com.firebase.ui.auth.ErrorCodes.NO_NETWORK;
import static com.firebase.ui.auth.ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED;
import static com.firebase.ui.auth.ErrorCodes.PROVIDER_ERROR;
import static com.firebase.ui.auth.ErrorCodes.UNKNOWN_ERROR;
import static com.myrungo.rungo.login.LoginActivity.RC_SIGN_IN;
import static com.myrungo.rungo.utils.DBConstants.emailKey;
import static com.myrungo.rungo.utils.DBConstants.phoneNumberKey;
import static com.myrungo.rungo.utils.DBConstants.usersCollection;

public final class LoginPresenter
        extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter<LoginContract.View> {

    private final String TAG = this.getClass().getName();

    @Nullable
    private FirebaseFirestore firestoreDB;

    @NonNull
    private FirebaseFirestore getFirebaseFirestore() {
        if (firestoreDB == null) {
            firestoreDB = FirebaseFirestore.getInstance();
        }

        return firestoreDB;
    }

    @Override
    final public void onViewCreate() {
        getView().showProgressIndicator();
        getView().createSignInIntent();
    }

    @Override
    final public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getView().hideRefreshIndicator();

        getView().showProgressIndicator();

        if (requestCode == RC_SIGN_IN) {
            @Nullable final IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                onOkResult();
            } else {
                onNotOkResult(response);
            }
        } else {
            getView().hideProgressIndicator();
        }
    }

    private void onOkResult() {
        getView().hideErrorTextView();

        @Nullable final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            getView().hideProgressIndicator();
        } else {
            saveToDB(user);
        }
    }

    private void saveToDB(@NonNull final FirebaseUser user) {
        getView().showProgressIndicator();

        @Nullable final FirebaseUserMetadata metadata = user.getMetadata();

        final long creationTimestamp = metadata == null ? System.currentTimeMillis() : metadata.getCreationTimestamp();

        @NonNull final Map<String, Object> newUserInfo = new HashMap<>();

        @NonNull final String email = user.getEmail() != null ? user.getEmail() : "";
        @NonNull final String displayName = user.getDisplayName() != null ? user.getDisplayName() : "";
        @NonNull final String phoneNumber = user.getPhoneNumber() != null ? user.getPhoneNumber() : "";
        @NonNull final Uri photoUri = user.getPhotoUrl() != null ? user.getPhotoUrl() : Uri.EMPTY;
        @NonNull final String uid = user.getUid();
        final boolean isAnonymous = user.isAnonymous();

        @NonNull final List<String> providers = user.getProviders() != null
                ? user.getProviders() : Collections.<String>emptyList();

        @NonNull final String provider = providers.isEmpty() ? "" : providers.get(0);

        @NonNull final String photoUrl = photoUri.toString();

        newUserInfo.put("reg_date", creationTimestamp);
        newUserInfo.put("name", displayName);
        newUserInfo.put(emailKey, email);
        newUserInfo.put(phoneNumberKey, phoneNumber);
        newUserInfo.put("photoUri", photoUrl);
        newUserInfo.put("uid", uid);
        newUserInfo.put("isAnonymous", isAnonymous);
        newUserInfo.put("provider", provider);

        getFirebaseFirestore()
                .collection(usersCollection)
                .document(uid)
                .set(newUserInfo)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@Nullable final Void v) {
                        //user saved in DB
                        getView().goToMainScreen();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        reportError(exception);

                        getView().hideProgressIndicator();

                        getView().showMessage(exception.getMessage());
                    }
                });
    }

    private void onNotOkResult(@Nullable final IdpResponse response) {
        if (response == null) {
            //the user canceled the sign-in flow using the back button
            getView().hideProgressIndicator();
            return;
        }

        @Nullable final FirebaseUiException error = response.getError();

        if (error == null) {
            getView().hideProgressIndicator();
            return;
        }

        Log.d(TAG, error.getMessage(), error);

        final int errorCode = error.getErrorCode();

        @NonNull final String part1;

        if (errorCode == NO_NETWORK) {

            part1 = getContext().getString(R.string.no_internet_connection);

        } else if (errorCode == PLAY_SERVICES_UPDATE_CANCELLED) {

            part1 = getContext().getString(R.string.play_services_update_cancelled);

        } else if (errorCode == DEVELOPER_ERROR) {

            reportError(error);

            part1 = getContext().getString(R.string.developer_error);

        } else if (errorCode == PROVIDER_ERROR) {

            reportError(error);

            part1 = getContext().getString(R.string.provider_error);

        } else if (errorCode == ANONYMOUS_UPGRADE_MERGE_CONFLICT) {

            reportError(error);

            part1 = getContext().getString(R.string.user_account_merge_conflict);

        } else if (errorCode == EMAIL_MISMATCH_ERROR) {

            reportError(error);

            part1 = getContext().getString(R.string.you_are_attempting_to_sign_in_a_different_email_than_previously_provided);

        } else if (errorCode == UNKNOWN_ERROR) {

            reportError(error);

            part1 = getContext().getString(R.string.unknown_error_has_occured);

        } else {

            reportError(error);

            part1 = getContext().getString(R.string.unknown_error_has_occured);

        }

        @NonNull final String part2 = getContext().getString(R.string.swipe_from_up_to_down);

        getView().setErrorText(part1 + ". " + part2);

        getView().showErrorTextView();

        getView().showMessage(part1);

        getView().hideProgressIndicator();
    }

}
