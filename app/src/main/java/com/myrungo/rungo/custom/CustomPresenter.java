package com.myrungo.rungo.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.myrungo.rungo.CatView;
import com.myrungo.rungo.base.BaseFragmentPresenter;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.models.DBUser;

import java.util.Objects;

@SuppressWarnings("unused")
public final class CustomPresenter
        extends BaseFragmentPresenter<CustomContract.View>
        implements CustomContract.Presenter<CustomContract.View> {

    private final String TAG = this.getClass().getName();

    @Override
    final public void onViewCreate() {
        getView().dressUp();
    }

    @Override
    final public void saveNewCostume(@NonNull final String newCostume) {
        //costume MUST be saved to shared preferences
        //because getting user's costume from DB - is long-running task and not preferable
        saveNewCostumeToSharedPreferences(newCostume);

        asyncSaveNewCostumeToDB(newCostume);
    }

    @Override
    @NonNull
    final public Task<String> asyncGetPreferredSkin() {
        @NonNull final String preferredSkinFromSharedPreferences =
                getPreferredSkinFromSharedPreferences();

        @NonNull final Task<String> asyncGetPreferredSkinFromDBTask =
                asyncGetPreferredSkinFromDB(preferredSkinFromSharedPreferences);

        return asyncGetPreferredSkinFromDBTask
                .continueWith(new Continuation<String, String>() {
                    @Override
                    public String then(@NonNull Task<String> task) {
                        @NonNull final String preferredSkin = preferredSkinFromSharedPreferences;

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
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(@Nullable final Void aVoid) {
                                            Log.d(TAG, "User info in DB updated successfully");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull final Exception e) {
                                            Log.d(TAG, "User info in DB update failed", e);
                                        }
                                    })
                                    .addOnCanceledListener(new OnCanceledListener() {
                                        @Override
                                        public void onCanceled() {
                                            Log.d(TAG, "User info in DB update canceled");
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Log.d(TAG, "Getting current user info from DB failed");
                        e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NonNull
    private String getPreferredSkinFromSharedPreferences() {
        @NonNull final String defaultCostume = CatView.Skins.COMMON.toString().toLowerCase();

        @NonNull final SharedPreferences prefs = Objects.requireNonNull(getContext())
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

}
