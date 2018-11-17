package com.myrungo.rungo.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.myrungo.rungo.R;
import com.myrungo.rungo.base.BasePresenter;

import java.util.List;
import java.util.Objects;

final class LoginPresenter<V extends LoginContract.View>
        extends BasePresenter<V>
        implements LoginContract.Presenter<V> {

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

        silentlySignIn(email);
    }

    private void silentlySignIn(@NonNull final String email) {
        getFirebaseAuth()
                .fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(((Activity) getView()), new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    final public void onComplete(@NonNull final Task<SignInMethodQueryResult> task) {
                        if (getCurrentUser() == null) {
                            return;
                        }

                        final boolean emailVerified = getCurrentUser().isEmailVerified();

                        @NonNull final Context context = ((Activity) getView()).getApplicationContext();

                        if (task.isSuccessful()) {
                            @Nullable final SignInMethodQueryResult result = task.getResult();

                            if (result == null) {
                                getView().hideProgressDialog();
                                return;
                            }

                            @Nullable final List<String> signInMethods = result.getSignInMethods();

                            if (signInMethods == null) {
                                getView().hideProgressDialog();
                                return;
                            }

                            final boolean userExistsInDB = !signInMethods.isEmpty();

                            if (userExistsInDB) {
                                if (emailVerified) {
                                    getView().goToMain();
                                } else {
                                    emailIsNotVerified(context);
                                }
                            }

                            return;
                        }

                        @NonNull String message;

                        @Nullable final Exception exception = task.getException();

                        if (exception instanceof FirebaseNetworkException) {
                            //todo решить, давать ли пользователю войти в аккаунт
                            //(его может не быть в БД или email не верифицирован)
                            //или показывать сообщение об ошибке
                            @NonNull final String networkError = context.getString(R.string.a_network_error_has_occurred);
                            @NonNull final String signInFailed = context.getString(R.string.sign_in_failed);
                            message = networkError + ". " + signInFailed;
                        } else {
                            message = context.getString(R.string.unknown_error_has_occured);
                        }

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

    @Override
    final public void signInWithPhoneNumber(@NonNull final String phoneNumber) {
        getView().showProgressDialog();

        if (!getView().isPhoneNumberValid()) {
            getView().hideProgressDialog();
            return;
        }

        int c = 1;
    }

    @Override
    final public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password) {
        getView().showProgressDialog();

        if (!getView().isEmailAndPasswordValid()) {
            getView().hideProgressDialog();
            getView().disableSignInButton();
            return;
        }

        //email and password are valid

        getView().disableSignInButton();

        getFirebaseAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getView(), new OnCompleteListener<AuthResult>() {
                    @Override
                    final public void onComplete(@NonNull final Task<AuthResult> task) {
                        @NonNull final Context context = ((Activity) getView()).getApplicationContext();

                        if (!task.isSuccessful()) {
                            @Nullable final Exception taskException = task.getException();

                            @NonNull String message;

                            if (taskException instanceof FirebaseAuthInvalidUserException) {
                                message = context.getString(R.string.there_is_no_such_user);
                            } else if (taskException instanceof FirebaseNetworkException) {
                                message = context.getString(R.string.a_network_error_has_occurred);
                                getView().enableSignInButton();
                            } else if (taskException instanceof FirebaseAuthInvalidCredentialsException) {
                                message = context.getString(R.string.the_email_address_is_badly_formatted);
                                getView().enableSignInButton();
                            } else {
                                message = context.getString(R.string.sign_in_failed);
                                getView().enableSignInButton();
                            }

                            getView().hideProgressDialog();
                            getView().showMessage(message);
                            return;
                        }

                        //task is succesful

                        getView().enableSignInButton();

                        if (getCurrentUser() == null) {
                            getView().hideProgressDialog();
                            return;
                        }

                        //currentUser != null

                        if (getCurrentUser().isEmailVerified()) {
                            //TODO сохранять в БД

                            getView().hideProgressDialog();
                            getView().goToMain();
                            return;
                        }

                        emailIsNotVerified(context);
                    }
                });
    }

    private void emailIsNotVerified(@NonNull final Context context) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            final public void onClick(@NonNull final View v) {
                sendEmailVerification();
            }
        };

        String message = context.getString(R.string.email_is_not_verified) + ". " +
                context.getString(R.string.send_email_verification_again);

        getView().hideProgressDialog();

        getView().showMessage(
                message,
                context.getString(android.R.string.yes),
                clickListener
        );
    }

    @Override
    final public void signUpWithEmail(@NonNull final String email, @NonNull final String password) {
        getView().showProgressDialog();

        if (!getView().isEmailAndPasswordValid()) {
            getView().hideProgressDialog();
            getView().disableSignInButton();
            return;
        }

        //email and password are valid

        getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getView(), new OnCompleteListener<AuthResult>() {
                    @Override
                    final public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendEmailVerification();
                            getView().hideProgressDialog();
                            return;
                        }

                        //task is not successful

                        @Nullable final Exception taskException = task.getException();
                        @NonNull final Context context = ((Activity) getView()).getApplicationContext();
                        @NonNull String message;

                        if (taskException instanceof FirebaseAuthWeakPasswordException) {
                            message = context.getString(R.string.password_must_be_at_least_6_characters);
                        } else if (taskException instanceof FirebaseAuthUserCollisionException) {
                            @NonNull final String tryToSignIn = context.getString(R.string.try_to_sign_in);

                            @NonNull final String suchUserAlreadyExists =
                                    context.getString(R.string.such_user_already_exists);

                            message = suchUserAlreadyExists + ". " + tryToSignIn;
                        } else if (taskException instanceof FirebaseNetworkException) {
                            message = context.getString(R.string.a_network_error_has_occurred);
                        } else if (taskException instanceof FirebaseAuthInvalidCredentialsException) {
                            message = context.getString(R.string.the_email_address_is_badly_formatted);
                            getView().enableSignInButton();
                        } else {
                            message = context.getString(R.string.register_failed);
                        }

                        getView().hideProgressDialog();

                        getView().showMessage(message);
                    }
                });
    }

    @Override
    public void signUpWithPhoneNumber(@NonNull final String phoneNumber) {
        int c = 1;
    }

    private void sendEmailVerification() {
        Objects.requireNonNull(getCurrentUser()).sendEmailVerification()
                .addOnCompleteListener((Activity) getView(), new OnCompleteListener<Void>() {
                    @Override
                    final public void onComplete(@NonNull final Task<Void> task) {
                        @NonNull final Context context = ((Activity) getView()).getApplicationContext();

                        @NonNull String message = "";

                        if (task.isSuccessful()) {
                            if (getCurrentUser() != null) {
                                message = context.getString(R.string.email_verification_has_been_sent_to) + " " +
                                        getCurrentUser().getEmail();
                            }
                        } else {
                            message = context.getString(R.string.verification_failed);
                        }

                        getView().showMessage(message);
                        getView().enableSignInButton();
                    }
                });
    }

}
