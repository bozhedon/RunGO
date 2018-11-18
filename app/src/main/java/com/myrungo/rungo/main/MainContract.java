package com.myrungo.rungo.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.myrungo.rungo.base.BaseContract;
import com.myrungo.rungo.models.Challenge;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.models.Training;

import java.util.List;

public interface MainContract extends BaseContract {

    @SuppressWarnings("unused")
    interface View extends BaseContract.View {

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

        @Nullable
        DBUser getCurrentUserInfo() throws Exception;

        void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception;

        void createNewUser(@NonNull final DBUser newUser) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception;


    }

    interface Presenter<V extends View> extends BaseContract.Presenter<V> {

        @NonNull
        List<Challenge> getAllChallenges() throws Exception;

        @NonNull
        List<DBUser> getUsers() throws Exception;

        @Nullable
        DBUser getCurrentUserInfo() throws Exception;

        void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception;

        void createNewUser(@NonNull final DBUser newUser) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception;

        @NonNull
        List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception;

    }

}
