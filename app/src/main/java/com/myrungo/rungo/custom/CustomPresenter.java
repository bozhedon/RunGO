package com.myrungo.rungo.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myrungo.rungo.CatView;
import com.myrungo.rungo.base.BaseFragmentPresenter;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.models.DBUser;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.myrungo.rungo.utils.DBConstants.userChallengeIsCompleteField;
import static com.myrungo.rungo.utils.DBConstants.userChallengeRewardField;
import static com.myrungo.rungo.utils.DBConstants.userChallengesCollection;

@SuppressWarnings("unused")
public final class CustomPresenter
        extends BaseFragmentPresenter<CustomContract.View>
        implements CustomContract.Presenter<CustomContract.View> {

    private final String TAG = this.getClass().getName();

    @Override
    public final void onViewCreate() {
        getView().showAvailableCostumes(asyncGetUserRewards());

        getView().dressUp();
    }

    @Override
    @NonNull
    public final Task<List<String>> asyncGetUserRewards() {
        @NonNull final String preferredSkinFromSharedPreferences =
                getPreferredSkinFromSharedPreferences();

        @NonNull final Task<String> asyncGetPreferredSkinFromDBTask =
                asyncGetPreferredSkinFromDB(preferredSkinFromSharedPreferences);

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
    public final void saveNewCostume(@NonNull final String newCostume) {
        //costume MUST be saved to shared preferences
        //because getting user's costume from DB - is long-running task and not preferable
        saveNewCostumeToSharedPreferences(newCostume);

        asyncSaveNewCostumeToDB(newCostume);
    }

    @Override
    @NonNull
    public final Task<String> asyncGetPreferredCostume() {
        @NonNull final String preferredSkinFromSharedPreferences =
                getPreferredSkinFromSharedPreferences();

        @NonNull final Task<String> asyncGetPreferredSkinFromDBTask =
                asyncGetPreferredSkinFromDB(preferredSkinFromSharedPreferences);

        return asyncGetPreferredSkinFromDBTask
                .continueWith(new Continuation<String, String>() {
                    @Override
                    public String then(@NonNull Task<String> task) {
                        @NonNull final String preferredSkin = preferredSkinFromSharedPreferences;

                        waitForAnyResult(task);

                        @Nullable final String preferredSkinFromDB = task.getResult();

                        if (preferredSkinFromDB == null) {
                            return preferredSkin;
                        }

                        if (!preferredSkinFromDB.equals(preferredSkinFromSharedPreferences)) {
                            asyncSaveNewCostumeToDB(preferredSkinFromSharedPreferences);
                        }

                        return preferredSkin;
                    }
                });
    }

    private void saveNewCostumeToSharedPreferences(@NonNull final String newCostume) {
        @NonNull final SharedPreferences prefs = Objects.requireNonNull(getContext())
                .getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);

        prefs.edit().putString("SKIN", newCostume).apply();
    }

    private void asyncSaveNewCostumeToDB(@NonNull final String newCostume) {
        @NonNull final MainContract.View mainView = (MainContract.View) getActivity();

        mainView.asyncGetCurrentUserInfo()
                .addOnSuccessListener(new OnSuccessListener<DBUser>() {
                    @Override
                    public void onSuccess(@Nullable final DBUser currentUserInfo) {
                        if (currentUserInfo == null) {
                            Log.d(TAG, "currentUserInfo == null");
                            return;
                        }

                        currentUserInfo.setCostume(newCostume);

                        try {
                            mainView.asyncUpdateUserInfo(currentUserInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User info in DB updated successfully");
                                            } else if (task.isCanceled()) {
                                                Log.d(TAG, "User info in DB update canceled");
                                            } else if (!task.isSuccessful()) {
                                                @Nullable final Exception exception = task.getException();

                                                if (exception != null) {
                                                    reportError(exception);

                                                    Log.d(TAG, "User info in DB update failed", exception);
                                                }
                                            }
                                        }
                                    });
                        } catch (@NonNull final Exception exception) {
                            reportError(exception);

                            exception.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception exception) {
                        reportError(exception);

                        Log.d(TAG, "Getting current user info from DB failed", exception);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "Getting current user info from DB canceled");
                    }
                });
    }

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

    @NonNull
    private String getPreferredSkinFromSharedPreferences() {
        @NonNull final String defaultCostume = CatView.Skins.COMMON.toString().toLowerCase();

        @NonNull final SharedPreferences prefs = getContext()
                .getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);

        @Nullable String preferredSkinFromSharedPreferences = prefs.getString("SKIN", defaultCostume);

        if (preferredSkinFromSharedPreferences == null) {
            preferredSkinFromSharedPreferences = defaultCostume;
        }

        return preferredSkinFromSharedPreferences;
    }

    @NonNull
    private Task<String> asyncGetPreferredSkinFromDB(@NonNull final String preferredSkinFromSharedPreferences) {
        @NonNull final MainContract.View mainView = (MainContract.View) getActivity();

        @NonNull final Task<DBUser> getCurrentUserInfoTask = mainView.asyncGetCurrentUserInfo();

        return getCurrentUserInfoTask.continueWith(new Continuation<DBUser, String>() {
            @Override
            public String then(@NonNull final Task<DBUser> task) throws Exception {
                waitForAnyResult(task);

                @Nullable final Exception exception = task.getException();

                if (exception != null) {
                    reportError(exception);

                    throw exception;
                }

                @Nullable final DBUser dbUser = task.getResult();

                if (dbUser == null) {
                    return "";
                }

                @NonNull final String preferredSkinFromDB = dbUser.getCostume();

                if (!preferredSkinFromDB.equals(preferredSkinFromSharedPreferences)) {
                    asyncSaveNewCostumeToDB(preferredSkinFromSharedPreferences);
                }

                return preferredSkinFromDB;
            }
        });
    }

    private void reportError(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        YandexMetrica.reportUnhandledException(throwable);
    }

    private void waitForAnyResult(@NonNull final Task<?> task) {
        while (true) {
            final boolean complete = task.isComplete();

            final boolean canceled = task.isCanceled();

            final boolean successful = task.isSuccessful();

            //for sync realization
            @NonNull final String msg = "task.isComplete() == " +
                    complete +
                    "; task.isCanceled() == " +
                    canceled +
                    "; task.isSuccessful() == " +
                    successful;

            Log.d(TAG, msg);

            if (successful || canceled || complete) {
                break;
            }
        }
    }

}
