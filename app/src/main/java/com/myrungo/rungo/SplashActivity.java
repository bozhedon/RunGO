package com.myrungo.rungo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.myrungo.rungo.login.LoginActivity;

import java.util.List;

public final class SplashActivity extends AppCompatActivity {

    @Override
    final protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeToAppropriateScreen();
    }

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

    private void routeToAppropriateScreen() {
        if (getCurrentUser() == null) {
            goToLoginScreen();
            return;
        }

        @Nullable final String email = getCurrentUser().getEmail();

        if (email == null) {
            goToLoginScreen();
            return;
        }

        if (email.isEmpty()) {
            //TODO
            //if sign up with phone number, sign in with appropriate info
            goToLoginScreen();
            return;
        }

        getFirebaseAuth()
                .fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    final public void onSuccess(@NonNull final SignInMethodQueryResult result) {
                        if (getCurrentUser() == null) {
                            goToLoginScreen();
                            return;
                        }

                        @Nullable final List<String> signInMethods = result.getSignInMethods();

                        if (signInMethods == null) {
                            goToLoginScreen();
                            return;
                        }

                        final boolean userExistsInDB = !signInMethods.isEmpty();

                        if (!userExistsInDB) {
                            goToLoginScreen();
                            return;
                        }

                        if (!getCurrentUser().isEmailVerified()) {
                            goToLoginScreen();
                        } else {
                            goToMainScreen();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        goToLoginScreen();
                    }
                });
    }

    private void goToLoginScreen() {
        @NonNull final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainScreen() {
        @NonNull final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
