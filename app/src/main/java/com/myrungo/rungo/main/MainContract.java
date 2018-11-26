package com.myrungo.rungo.main;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.myrungo.rungo.base.BaseContract;
import com.myrungo.rungo.models.Challenge;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.models.Training;

import java.util.List;

public interface MainContract extends BaseContract {

    @SuppressWarnings("unused")
    interface View extends BaseContract.View {

        @NonNull
        FirebaseAnalytics getFirebaseAnalytics();

        void showProgressIndicator();

        void hideProgressIndicator();

        /**
         * Fragments can call this method for their work
         * <p>
         * Example:
         * <p>
         * <pre>
         * {@code @Nullable
         * final FragmentActivity activity = getActivity();
         *
         * if (activity != null) {
         *     try {
         *        @code @NonNull final MainContract.View mainView = (MainContract.View) activity;
         *
         *        @code @NonNull final List<Challenge> allChallenges = mainView.getAllChallenges();
         *     } catch (Exception e) {
         *         e.printStackTrace();
         *     }
         * }
         * }
         * </pre>
         */
        @NonNull
        List<Challenge> getAllChallenges() throws Exception;

        /**
         * Fragments can call this method for their work
         * <p>
         * Example available in getAllChallenges()
         */
        @NonNull
        List<DBUser> getUsers() throws Exception;

        @NonNull
        DBUser getCurrentUserInfo() throws Exception;

        @NonNull
        Task<DBUser> asyncGetCurrentUserInfo();

        void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception;

        @NonNull
        Task<Void> asyncUpdateUserInfo(@NonNull final DBUser newUserInfo) throws Exception;

        void createNewUser(@NonNull final DBUser newUser) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception;

        @NonNull
        CollectionReference getUsersCollection();

        void goToLoginScreen();

        @NonNull
        List<Training> getCurrentUserTrainings() throws Exception;

    }

    interface Presenter<V extends View> extends BaseContract.Presenter<V> {

        void onViewStart();

        @NonNull
        CollectionReference getUsersCollection();

        @NonNull
        List<Challenge> getAllChallenges() throws Exception;

        @NonNull
        List<DBUser> getUsers() throws Exception;

        @NonNull
        DBUser getCurrentUserInfo() throws Exception;

        @NonNull
        Task<DBUser> asyncGetCurrentUserInfo();

        void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception;

        @NonNull
        Task<Void> asyncUpdateUserInfo(@NonNull final DBUser newUserInfo) throws Exception;

        void createNewUser(@NonNull final DBUser newUser) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception;

        @NonNull
        List<Training> getCurrentUserTrainings() throws Exception;

    }

}
