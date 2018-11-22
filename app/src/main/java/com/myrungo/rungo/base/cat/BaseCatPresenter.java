package com.myrungo.rungo.base.cat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.CatView;
import com.myrungo.rungo.base.BaseFragmentPresenter;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.models.DBUser;

public abstract class BaseCatPresenter<V extends BaseCatContract.View>
        extends BaseFragmentPresenter<V>
        implements BaseCatContract.Presenter<V> {

    private final String TAG = this.getClass().getName();

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

                        @Nullable final Exception exception = task.getException();

                        if (exception != null) {
                            //TODO
                        }

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

    @Override
    @NonNull
    public final String getPrefferedCostume() {
        @NonNull final String preferredSkinFromSharedPreferences =
                getPreferredSkinFromSharedPreferences();

        @NonNull final Task<String> asyncGetPreferredSkinFromDBTask =
                asyncGetPreferredSkinFromDB(preferredSkinFromSharedPreferences);

        asyncGetPreferredSkinFromDBTask
                .continueWith(new Continuation<String, String>() {
                    @Override
                    public String then(@NonNull Task<String> task) {
                        @NonNull final String preferredSkin = preferredSkinFromSharedPreferences;

                        waitForAnyResult(task);

                        @Nullable final Exception exception = task.getException();

                        if (exception != null) {
                            int c = 1;
                            //TODO
                        }

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

        return preferredSkinFromSharedPreferences;
    }


    @NonNull
    protected String getPreferredSkinFromSharedPreferences() {
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
    protected Task<String> asyncGetPreferredSkinFromDB(@NonNull final String preferredSkinFromSharedPreferences) {
        @NonNull final MainContract.View mainView = (MainContract.View) getActivity();

        @NonNull final Task<DBUser> getCurrentUserInfoTask = mainView.asyncGetCurrentUserInfo();

        return getCurrentUserInfoTask.continueWith(new Continuation<DBUser, String>() {
            @Override
            public String then(@NonNull final Task<DBUser> task) throws Exception {
                waitForAnyResult(task);

                @Nullable final Exception exception = task.getException();

                if (exception != null) {
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

    protected void asyncSaveNewCostumeToDB(@NonNull final String newCostume) {
        @NonNull final FragmentActivity activity = getActivity();
        @NonNull final MainContract.View mainView = (MainContract.View) activity;

        mainView.asyncGetCurrentUserInfo()
                .addOnSuccessListener(activity, new OnSuccessListener<DBUser>() {
                    @Override
                    public void onSuccess(@Nullable final DBUser currentUserInfo) {
                        if (currentUserInfo == null) {
                            Log.d(TAG, "currentUserInfo == null");
                            return;
                        }

                        currentUserInfo.setCostume(newCostume);

                        try {
                            mainView.asyncUpdateUserInfo(currentUserInfo)
                                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User info in DB updated successfully");
                                            } else if (task.isCanceled()) {
                                                Log.d(TAG, "User info in DB update canceled");
                                            } else if (!task.isSuccessful()) {
                                                @Nullable final Exception exception = task.getException();

                                                if (exception != null) {
                                                    Log.d(TAG, "User info in DB update failed", exception);
                                                }
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Log.d(TAG, "Getting current user info from DB failed", e);
                    }
                })
                .addOnCanceledListener(activity, new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "Getting current user info from DB canceled");
                    }
                });

    }

    protected void waitForAnyResult(@NonNull final Task<?> task) {
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
