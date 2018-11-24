package com.myrungo.rungo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.myrungo.rungo.base.BaseActivity;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.main.MainPresenter;
import com.myrungo.rungo.models.Challenge;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.models.Training;
import com.myrungo.rungo.profile.ProfileFragment;

import java.util.List;

public final class MainActivity
        extends BaseActivity<MainContract.View, MainContract.Presenter<MainContract.View>>
        implements MainContract.View {

    public static final String USER_TAG = "USER_TAG";

    private boolean first = true;

    private int position = 1;

    private ChallengeItem current_challenge;

    @Nullable
    private User user;

    @NonNull
    private final BottomNavigationViewEx.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        final public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            @NonNull Fragment fragment;

            switch (item.getItemId()) {
                case R.id.home:
                    if (position != 1) {
                        fragment = new HomeFragment();
                        replaceFragment(fragment);
                        position = 1;
                    }
                    return true;
                case R.id.custom:
                    if (position != 2) {
                        fragment = new CustomFragment();
                        replaceFragment(fragment);
                        position = 2;
                    }
                    return true;
                case R.id.challenge:
                    if (position != 4) {
                        fragment = new ChallengeFragment();
                        replaceFragment(fragment);
                        position = 4;
                    }
                    return true;
                case R.id.profile:
                    if (position != 5) {
                        fragment = new ProfileFragment();
                        replaceFragment(fragment);
                        position = 5;
                    }
                    return true;
            }

            return false;
        }
    };

    @Nullable
    private FloatingActionButton fab;

    @Nullable
    private MainContract.Presenter<MainContract.View> presenter;

    @Nullable
    private FirebaseAnalytics firebaseAnalytics;

    @NonNull
    private FloatingActionButton getFab() {
        if (fab == null) {
            throw new NullPointerException("fab == null");
        }

        return fab;
    }

    private void setFab(@Nullable final View fab) {
        if (fab == null) {
            throw new NullPointerException("fab == null");
        }

        this.fab = (FloatingActionButton) fab;
    }

    @Override
    protected final MainContract.Presenter<MainContract.View> getPresenter() {
        if (presenter == null) {
            @NonNull final RuntimeException exception = new RuntimeException("presenter == null");
            reportError(exception);

            throw exception;
        }

        return presenter;
    }

    @Override
    protected final void setupPresenter() {
        presenter = new MainPresenter();
    }

    @NonNull
    @Override
    public final FirebaseAnalytics getFirebaseAnalytics() {
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }

        return firebaseAnalytics;
    }

    @Override
    protected final void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showProgressIndicator();

        setFab(findViewById(R.id.fab_start));

        setFabOnClickListener();

        goToHomeFragment();

        setupBottomNavigationView();

        hideProgressIndicator();

        getPresenter().onViewCreate();
    }

    private void goToHomeFragment() {
        @NonNull final Fragment fragment = new HomeFragment();
        @NonNull final FragmentManager manager = getSupportFragmentManager();

        //Bundle user_bundle = new Bundle();
        //user_bundle.putSerializable(USER_TAG, user);
        //fragment.setArguments(user_bundle);
        @NonNull final FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setFabOnClickListener() {
        getFab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.putExtra(DBUser.class.getSimpleName(), user);
                startActivity(intent);
            }
        });
    }

    private void replaceFragment(@NonNull final Fragment someFragment) {
        //Bundle user_bundle = new Bundle();
        //user_bundle.putSerializable(USER_TAG, user);
        //someFragment.setArguments(user_bundle);
        @NonNull final FragmentManager fragmentManager = getSupportFragmentManager();
        @NonNull final FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, someFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (first) {
            transaction.addToBackStack(null);
            first = false;
        }
        transaction.commit();
    }

    //bottom navigation view customization
    private void setupBottomNavigationView() {
        @NonNull final BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bnve);

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    @NonNull
    private ViewGroup getProgressBarLayout() {
        @Nullable final ViewGroup layoutWithProgressBar = findViewById(R.id.layoutWithProgressBar);

        if (layoutWithProgressBar == null) {
            @NonNull final RuntimeException exception = new RuntimeException("layoutWithProgressBar == null");
            reportError(exception);

            throw exception;
        }

        return layoutWithProgressBar;
    }

    @Override
    public final void hideProgressIndicator() {
        getProgressBarLayout().setVisibility(View.GONE);
    }

    @Override
    public final void showProgressIndicator() {
        getProgressBarLayout().setVisibility(View.VISIBLE);
    }

    @Override
    @NonNull
    public final List<Challenge> getAllChallenges() throws Exception {
        return getPresenter().getAllChallenges();
    }

    @NonNull
    @Override
    public final List<DBUser> getUsers() throws Exception {
        return getPresenter().getUsers();
    }

    @NonNull
    @Override
    public final Task<DBUser> asyncGetCurrentUserInfo() {
        return getPresenter().asyncGetCurrentUserInfo();
    }

    @NonNull
    @Override
    public final DBUser getCurrentUserInfo() throws Exception {
        return getPresenter().getCurrentUserInfo();
    }

    @Override
    public final void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception {
        getPresenter().updateUserInfo(newUserInfo);
    }

    @Override
    @NonNull
    public final Task<Void> asyncUpdateUserInfo(@NonNull final DBUser newUserInfo) throws Exception {
        return getPresenter().asyncUpdateUserInfo(newUserInfo);
    }

    @Override
    public final void createNewUser(@NonNull final DBUser newUser) throws Exception {
        getPresenter().createNewUser(newUser);
    }

    @NonNull
    @Override
    public final List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception {
        return getPresenter().getUserTrainingsByUid(uid);
    }

    @NonNull
    @Override
    public final List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception {
        return getPresenter().getUserTrainingsByDocumentId(documentId);
    }

    @NonNull
    @Override
    public final CollectionReference getUsersCollection() {
        return getPresenter().getUsersCollection();
    }

}
