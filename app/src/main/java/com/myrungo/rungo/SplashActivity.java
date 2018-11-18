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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.myrungo.rungo.login.LoginActivity;

import java.util.List;
import java.util.Map;

import static com.myrungo.rungo.utils.DBConstants.emailKey;
import static com.myrungo.rungo.utils.DBConstants.phoneNumberKey;
import static com.myrungo.rungo.utils.DBConstants.usersCollection;

public final class SplashActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    @NonNull
    private final String TAG = getClass().getName();

    @Override
    final protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeToAppropriateScreen();
    }

    @Nullable
    private FirebaseFirestore firestoreDB;

    @NonNull
    private FirebaseFirestore getFirebaseFirestore() {
        if (firestoreDB == null) {
            firestoreDB = FirebaseFirestore.getInstance();
        }

        return firestoreDB;
    }

    @Nullable
    private FirebaseAuth firebaseAuth;

    @NonNull
    private FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.useAppLanguage();
        }

        return firebaseAuth;
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    private void routeToAppropriateScreen() {
        if (getCurrentUser() != null) {
            @Nullable final String phoneNumber = getCurrentUser().getPhoneNumber();

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                checkWithPhoneNumber(phoneNumber);
                return;
            }

            @Nullable final String email = getCurrentUser().getEmail();

            if (email != null && !email.isEmpty()) {
                checkWithEmail(email);
                return;
            }
        }

        goToLoginScreen();
    }

    private void checkWithEmail(@NonNull final String email) {
        getFirebaseFirestore()
                .collection(usersCollection)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    final public void onSuccess(@NonNull final QuerySnapshot querySnapshot) {
                        @NonNull final List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        checkDocumentsForEmail(documents, email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        goToLoginScreen();
                    }
                });
    }

    private void checkDocumentsForEmail(
            @Nullable final List<DocumentSnapshot> documents,
            @NonNull final String email
    ) {
        if (documents == null) {
            goToLoginScreen();
            return;
        }

        for (@NonNull final DocumentSnapshot document : documents) {
            @Nullable final Map<String, Object> data = document.getData();

            if (data != null) {
                @Nullable final Object emailFromDB = data.get(emailKey);

                if (emailFromDB != null) {
                    final boolean canGoToMainScreen = emailFromDB.equals(email);

                    if (canGoToMainScreen) {
                        goToMainScreen();
                        return;
                    }
                }
            }
        }

        //DB has no such user's email
        goToLoginScreen();
    }

    private void checkWithPhoneNumber(@NonNull final String phoneNumber) {
        getFirebaseFirestore()
                .collection(usersCollection)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    final public void onSuccess(@NonNull final QuerySnapshot querySnapshot) {
                        @NonNull final List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        checkDocumentsForPhoneNumber(documents, phoneNumber);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    final public void onFailure(@NonNull final Exception exception) {
                        goToLoginScreen();
                    }
                });
    }

    private void checkDocumentsForPhoneNumber(
            @Nullable final List<DocumentSnapshot> documents,
            @NonNull final String phoneNumber
    ) {
        if (documents == null) {
            goToLoginScreen();
            return;
        }

        for (@NonNull final DocumentSnapshot document : documents) {
            @Nullable final Map<String, Object> data = document.getData();

            if (data != null) {
                @Nullable final Object phoneNumberFromDB = data.get(phoneNumberKey);

                if (phoneNumberFromDB != null) {
                    final boolean canGoToMainScreen = phoneNumberFromDB.equals(phoneNumber);

                    if (canGoToMainScreen) {
                        goToMainScreen();
                        return;
                    }
                }
            }
        }

        //DB has no such user's phone number
        goToLoginScreen();
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
