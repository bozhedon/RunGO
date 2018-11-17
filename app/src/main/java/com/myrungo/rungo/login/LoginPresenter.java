package com.myrungo.rungo.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BasePresenter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class LoginPresenter<V extends LoginContract.View>
        extends BasePresenter<V>
        implements LoginContract.Presenter<V> {

    private final String TAG = this.getClass().getName();

    @Nullable
    private String verificationId;

    @Nullable
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Nullable
    private FirebaseAuth firebaseAuth;

    @NonNull
    private FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        firebaseAuth.useAppLanguage();

        return firebaseAuth;
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    @Override
    final public void onStart() {
        getView().showProgressDialog();

        if (getCurrentUser() == null) {
            getView().hideProgressDialog();
            return;
        }

        @Nullable final String email = getCurrentUser().getEmail();

        if (email == null) {
            getView().hideProgressDialog();
            return;
        }

        if (email.isEmpty()) {
            //TODO
            //if sign up with phone number, sign in with appropriate info
            return;
        }

        silentlySignIn(email);
    }

    private void silentlySignIn(@NonNull final String email) {
        getFirebaseAuth()
                .fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    final public void onSuccess(@NonNull final SignInMethodQueryResult result) {
                        if (getCurrentUser() == null) {
                            return;
                        }

                        @Nullable final List<String> signInMethods = result.getSignInMethods();

                        if (signInMethods == null) {
                            getView().hideProgressDialog();
                            return;
                        }

                        final boolean userExistsInDB = !signInMethods.isEmpty();

                        if (userExistsInDB) {
                            if (!getCurrentUser().isEmailVerified()) {
                                emailIsNotVerified();
                            } else {
                                getView().goToMain();
                            }
                        } else {
                            getView().hideProgressDialog();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        @NonNull String message;

                        if (exception instanceof FirebaseNetworkException) {
                            //todo user may be is not verified
                            //then go to main screen or show error?
                            @NonNull final String networkError = getContext().getString(R.string.a_network_error_has_occurred);
                            @NonNull final String signInFailed = getContext().getString(R.string.sign_in_failed);
                            message = networkError + ". " + signInFailed;
                        } else {
                            message = getContext().getString(R.string.unknown_error_has_occured);
                        }

                        Log.d(TAG, exception.getMessage(), exception);

                        getView().hideProgressDialog();

                        getView().showMessage(message);
                    }
                });
    }

    @Override
    final public void onBindView(@NonNull final V view) {
        super.onBindView(view);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void signInWithPhoneAuthCredential(@NonNull final PhoneAuthCredential credential) {
        getView().showProgressDialog();

        getFirebaseAuth().signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    final public void onSuccess(@NonNull final AuthResult result) {
                        if (getCurrentUser() == null) {
                            getView().hideProgressDialog();
                            return;
                        }

                        getView().goToMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            getView().showMessage(getContext().getString(R.string.invalid_code));
                        } else {
                            //..
                        }

                        Log.d(TAG, exception.getMessage(), exception);

                        getView().hideProgressDialog();
                    }
                });
    }

    private void verifyPhoneNumberWithCode(@NonNull final String verificationId, @NonNull final String code) {
        @NonNull final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    @Override
    final public void signInWithPhoneNumber(@NonNull final String phoneNumber) {
        getView().showProgressDialog();

        if (!getView().isPhoneNumberValid()) {
            getView().hideProgressDialog();
            getView().disableSignInWithPhoneNumberButton();
            return;
        }

        //..
    }

    @Override
    final public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password) {
        getView().showProgressDialog();

        if (!getView().isEmailAndPasswordValid()) {
            getView().hideProgressDialog();
            getView().disableSignInWithEmailButton();
            return;
        }

        //email and password are valid

        getView().disableSignInWithEmailButton();

        getFirebaseAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(@NonNull final AuthResult authResult) {
                        getView().enableSignInWithEmailButton();

                        if (getCurrentUser() == null) {
                            getView().hideProgressDialog();
                            return;
                        }

                        //currentUser != null

                        if (getCurrentUser().isEmailVerified()) {
                            //TODO save to DB
                            getView().goToMain();
                        } else {
                            emailIsNotVerified();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        @NonNull String message;

                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            message = getContext().getString(R.string.there_is_no_such_user);
                        } else if (exception instanceof FirebaseNetworkException) {
                            message = getContext().getString(R.string.a_network_error_has_occurred);
                            getView().enableSignInWithEmailButton();
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            message = getContext().getString(R.string.the_email_address_is_badly_formatted);
                            getView().enableSignInWithEmailButton();
                        } else {
                            message = getContext().getString(R.string.sign_in_failed);
                            getView().enableSignInWithEmailButton();
                        }

                        Log.d(TAG, exception.getMessage(), exception);

                        getView().hideProgressDialog();
                        getView().showMessage(message);
                    }
                });
    }

    private void emailIsNotVerified() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            final public void onClick(@NonNull final View v) {
                sendEmailVerification();
            }
        };

        String message = getContext().getString(R.string.email_is_not_verified) + ". " +
                getContext().getString(R.string.send_email_verification_again);

        getView().hideProgressDialog();

        getView().showMessage(
                message,
                getContext().getString(android.R.string.yes),
                clickListener
        );
    }

    @Override
    final public void signUpWithEmail(@NonNull final String email, @NonNull final String password) {
        getView().showProgressDialog();

        if (!getView().isEmailAndPasswordValid()) {
            getView().hideProgressDialog();
            getView().disableSignInWithEmailButton();
            return;
        }

        //email and password are valid

        getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    final public void onSuccess(@NonNull final AuthResult result) {
                        sendEmailVerification();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        @NonNull String message;

                        if (exception instanceof FirebaseAuthWeakPasswordException) {
                            message = getContext().getString(R.string.password_must_be_at_least_6_characters);
                        } else if (exception instanceof FirebaseAuthUserCollisionException) {
                            @NonNull final String tryToSignIn = getContext().getString(R.string.try_to_sign_in);

                            @NonNull final String suchUserAlreadyExists =
                                    getContext().getString(R.string.such_user_already_exists);

                            message = suchUserAlreadyExists + ". " + tryToSignIn;
                        } else if (exception instanceof FirebaseNetworkException) {
                            message = getContext().getString(R.string.a_network_error_has_occurred);
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            message = getContext().getString(R.string.the_email_address_is_badly_formatted);
                            getView().enableSignInWithEmailButton();
                        } else {
                            message = getContext().getString(R.string.register_failed);
                        }

                        Log.d(TAG, exception.getMessage(), exception);

                        getView().hideProgressDialog();

                        getView().showMessage(message);

                    }
                });
    }

    @Override
    public void signUpWithPhoneNumber(@NonNull final String phoneNumber) {
        getView().showProgressDialog();

        if (!getView().isPhoneNumberValid()) {
            getView().hideProgressDialog();
            getView().disableSignInWithPhoneNumberButton();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,    // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Activity) getView(),   // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull final PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.

                        getView().hideProgressDialog();

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull final FirebaseException exception) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.

                        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            getView().showPhoneNumberError(getContext().getString(R.string.invalid_phone_number));
                        } else if (exception instanceof FirebaseNetworkException) {
                            getView().showMessage(getContext().getString(R.string.a_network_error_has_occurred));
                        } else if (exception instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            getView().showMessage("The SMS quota for the project has been exceeded");
                        } else {
                            getView().showMessage(getContext().getString(R.string.send_sms_failed));
                        }

                        Log.d(this.getClass().getName(), exception.getMessage(), exception);

                        getView().hideProgressDialog();
                    }

                    @Override
                    public void onCodeSent(
                            @NonNull final String verificationId,
                            @NonNull final PhoneAuthProvider.ForceResendingToken token
                    ) {
                        //TODO save this parameters to DB or Shared Preferences
                        LoginPresenter.this.verificationId = verificationId;
                        resendToken = token;

                        getView().hideProgressDialog();
                    }
                });
    }

    private void sendEmailVerification() {
        Objects.requireNonNull(getCurrentUser()).sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    final public void onSuccess(@Nullable final Void aVoid) {
                        if (getCurrentUser() != null) {
                            String hasBeenSentTo =
                                    getContext().getString(R.string.email_verification_has_been_sent_to);

                            @NonNull final String message = hasBeenSentTo + " " + getCurrentUser().getEmail();

                            getView().hideProgressDialog();
                            getView().showMessage(message);
                            getView().enableSignInWithEmailButton();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        final @NonNull String message = getContext().getString(R.string.send_verification_email_failed);

                        Log.d(TAG, exception.getMessage(), exception);

                        getView().hideProgressDialog();
                        getView().showMessage(message);
                        getView().enableSignInWithEmailButton();
                    }
                });
    }

    @NonNull
    private Context getContext() {
        return ((Activity) getView()).getApplicationContext();
    }

}
