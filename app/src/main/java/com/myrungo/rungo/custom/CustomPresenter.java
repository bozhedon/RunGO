package com.myrungo.rungo.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myrungo.rungo.base.cat.BaseCatPresenter;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.utils.DBConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.myrungo.rungo.utils.DBConstants.userChallengeIsCompleteField;
import static com.myrungo.rungo.utils.DBConstants.userChallengeRewardField;
import static com.myrungo.rungo.utils.DBConstants.userChallengesCollection;

public final class CustomPresenter
        extends BaseCatPresenter<CustomContract.View>
        implements CustomContract.Presenter<CustomContract.View> {

    @Override
    public final void onViewCreate() {
        getView().showProgressIndicator();
        getView().showAvailableCostumes(asyncGetUserRewards());
    }

    @Override
    public void asyncUpdateUserRewards(@NonNull final String preferredCostume) {
        @NonNull final FragmentActivity activity = getActivity();
        @NonNull final MainContract.View view = (MainContract.View) activity;

        @NonNull final Task<DBUser> getCurrentUserInfoTask = view.asyncGetCurrentUserInfo();

        getCurrentUserInfoTask
                .addOnSuccessListener(activity, new OnSuccessListener<DBUser>() {
                    @Override
                    public void onSuccess(@Nullable final DBUser dbUser) {
                        if (dbUser == null) {
                            return;
                        }

                        @NonNull final CollectionReference userChallengesCollection =
                                getUserChallengesCollection(dbUser);

                        @NonNull final List<DocumentSnapshot> userChallenges =
                                getUserChallenges(userChallengesCollection);

                        updateApropriateChallenge(userChallenges, preferredCostume, userChallengesCollection);
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception exception) {
                        reportError(exception);
                    }
                });
    }

    @NonNull
    private List<DocumentSnapshot> getUserChallenges(@NonNull final CollectionReference userChallengesCollection) {
        @NonNull final Task<QuerySnapshot> getUserChallengesTask =
                userChallengesCollection.get();

        waitForAnyResult(getUserChallengesTask);

        @Nullable final Exception getUserChallengesTaskException = getUserChallengesTask.getException();

        if (getUserChallengesTaskException != null) {
            reportError(getUserChallengesTaskException);
            return Collections.emptyList();
        }

        @Nullable final QuerySnapshot getUserChallengesTaskResult = getUserChallengesTask.getResult();

        if (getUserChallengesTaskResult == null) {
            return Collections.emptyList();
        }

        return getUserChallengesTaskResult.getDocuments();
    }

    @NonNull
    private CollectionReference getUserChallengesCollection(@NonNull DBUser dbUser) {
        @NonNull final MainContract.View view = (MainContract.View) getActivity();

        @NonNull final DocumentReference userDocument =
                view.getUsersCollection().document(dbUser.getUid());

        return userDocument.collection(DBConstants.userChallengesCollection);
    }

    private void updateApropriateChallenge(
            @NonNull final List<DocumentSnapshot> snapshots,
            @NonNull final String preferredCostume,
            @NonNull final CollectionReference userChallengesCollection) {
        @NonNull final FragmentActivity activity = getActivity();

        for (@Nullable final DocumentSnapshot snapshot : snapshots) {
            if (snapshot == null) {
                continue;
            }

            @Nullable final Map<String, Object> data = snapshot.getData();

            if (data == null) {
                continue;
            }

            if (data.containsKey("reward")) {
                @Nullable final Object rewardObj = data.get("reward");

                @Nullable final String reward = (String) rewardObj;

                if (reward != null && reward.equals(preferredCostume)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            data.put("isComplete", true);

                            @NonNull final DocumentReference thisChallengeDocument =
                                    userChallengesCollection.document(snapshot.getId());

                            thisChallengeDocument.set(data)
                                    .addOnFailureListener(activity, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull final Exception exception) {
                                            reportError(exception);
                                        }
                                    });
                        }
                    }).start();

                    break;
                }
            }
        }
    }

    @Override
    @NonNull
    public final Task<List<String>> asyncGetUserRewards() {
        @NonNull final MainContract.View view = (MainContract.View) getActivity();

        @NonNull final Task<DBUser> getCurrentUserInfoTask = view.asyncGetCurrentUserInfo();

        return getCurrentUserInfoTask.continueWith(new Continuation<DBUser, List<String>>() {
            @Override
            public List<String> then(@NonNull Task<DBUser> task) throws Exception {
                waitForAnyResult(task);

                @Nullable final Exception exception = task.getException();

                if (exception != null) {
                    reportError(exception);

                    throw exception;
                }

                @Nullable final DBUser dbUser = task.getResult();

                if (dbUser == null) {
                    return Collections.emptyList();
                }

                @NonNull final DocumentReference userDocument = view.getUsersCollection().document(dbUser.getUid());
                @NonNull final Task<QuerySnapshot> getUserChallengesTask =
                        userDocument.collection(userChallengesCollection).get();

                waitForAnyResult(getUserChallengesTask);

                @Nullable final Exception getUserChallengesTaskException = getUserChallengesTask.getException();

                if (getUserChallengesTaskException != null) {
                    reportError(getUserChallengesTaskException);

                    throw getUserChallengesTaskException;
                }

                @Nullable final QuerySnapshot getUserChallengesTaskResult = getUserChallengesTask.getResult();

                if (getUserChallengesTaskResult == null) {
                    return Collections.emptyList();
                }

                return getUserRewards(getUserChallengesTaskResult);
            }
        });
    }

    @NonNull
    private List<String> getUserRewards(QuerySnapshot getUserChallengesTaskResult) {
        @NonNull final List<DocumentSnapshot> documents = getUserChallengesTaskResult.getDocuments();

        @NonNull final List<String> result = new ArrayList<>();

        for (@Nullable final DocumentSnapshot document : documents) {
            if (document == null) {
                continue;
            }

            @Nullable final Map<String, Object> data = document.getData();

            if (data == null) {
                continue;
            }

            @Nullable final Object isCompleteObj = data.get(userChallengeIsCompleteField);

            boolean isComplete = (boolean) isCompleteObj;

            if (!isComplete) {
                continue;
            }

            @Nullable final Object rewardObj = data.get(userChallengeRewardField);

            @Nullable final String reward = (String) rewardObj;

            if (reward == null) {
                continue;
            }

            result.add(reward);
        }

        return result;
    }

    @Override
    public final void asyncSaveNewCostume(@NonNull final String newCostume) {
        //costume MUST be saved to shared preferences
        //because getting user's costume from DB - is long-running task and not preferable
        asyncSaveNewCostumeToSharedPreferences(newCostume);

        asyncSaveNewCostumeToDB(newCostume);
    }

    private void asyncSaveNewCostumeToSharedPreferences(@NonNull final String newCostume) {
        @NonNull final SharedPreferences prefs = Objects.requireNonNull(getContext())
                .getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);

        prefs.edit().putString("SKIN", newCostume).apply();
    }

    @SuppressWarnings("unused")
    @NonNull
    private String getPreferredSkin() {
        @NonNull final String preferredSkinFromSharedPreferences = getPreferredSkinFromSharedPreferences();

        @NonNull final String preferredSkinFromDB = getPreferredSkinFromDB();

        @NonNull final String preferredSkin = preferredSkinFromSharedPreferences;

        if (!preferredSkinFromDB.equals(preferredSkinFromSharedPreferences)) {
            asyncSaveNewCostumeToDB(preferredSkinFromSharedPreferences);
        }

        return preferredSkin;
    }

    @NonNull
    private String getPreferredSkinFromDB() {
        @NonNull final MainContract.View mainView = (MainContract.View) getActivity();

        try {
            @NonNull final DBUser currentUserInfo = mainView.getCurrentUserInfo();

            return currentUserInfo.getCostume();
        } catch (@NonNull final Exception exception) {
            reportError(exception);

            exception.printStackTrace();
            return "";
        }
    }

}
