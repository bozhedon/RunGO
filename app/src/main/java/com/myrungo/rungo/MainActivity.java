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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.myrungo.rungo.base.BaseActivity;
import com.myrungo.rungo.main.MainContract;
import com.myrungo.rungo.main.MainPresenter;
import com.myrungo.rungo.models.Challenge;
import com.myrungo.rungo.models.DBUser;
import com.myrungo.rungo.models.Training;

import java.util.List;

public final class MainActivity
        extends BaseActivity<MainContract.View, MainContract.Presenter<MainContract.View>>
        implements MainContract.View {

    @Nullable
    private CatView.Skins skin;

    @Nullable
    private CatView.Heads head;

    private boolean first = true;
    private int position = 1;
    private User user;
    public static final String USER_TAG = "USER_TAG";
    @NonNull
    private BottomNavigationViewEx.OnNavigationItemSelectedListener onNavigationItemSelectedListener
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

    @NonNull
    public FloatingActionButton getFab() {
        if (fab == null) {
            throw new NullPointerException("fab == null");
        }

        return fab;
    }

    public void setFab(@Nullable final View fab) {
        if (fab == null) {
            throw new NullPointerException("fab == null");
        }

        this.fab = (FloatingActionButton) fab;
    }

    @Nullable
    private MainContract.Presenter<MainContract.View> presenter;

    @Nullable
    final public CatView.Skins getSkin() {
        return skin;
    }

    final public void setSkin(@NonNull final CatView.Skins skin) {
        this.skin = skin;
    }

    @Nullable
    final public CatView.Heads getHead() {
        return head;
    }

    final public void setHead(@NonNull final CatView.Heads head) {
        this.head = head;
    }

    @Override
    final protected MainContract.Presenter<MainContract.View> getPresenter() {
        if (presenter == null) {
            throw new RuntimeException("presenter == null");
        }

        return presenter;
    }

    @Override
    final protected void setupPresenter() {
        presenter = new MainPresenter();
    }

    @Override
    final protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showProgressIndicator();

        user = new User(CatView.Skins.BUSINESS, CatView.Heads.ANGRY);

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
                //intent.putExtra(DBUser.class.getSimpleName(), user);
                startActivity(intent);
            }
        });
    }

    final public void replaceFragment(@NonNull final Fragment someFragment) {
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
            throw new RuntimeException("layoutWithProgressBar == null");
        }

        return layoutWithProgressBar;
    }

    @Override
    final public void hideProgressIndicator() {
        getProgressBarLayout().setVisibility(View.GONE);
    }

    @Override
    final public void showProgressIndicator() {
        getProgressBarLayout().setVisibility(View.VISIBLE);
    }

    @Override
    @NonNull
    final public List<Challenge> getAllChallenges() throws Exception {
        return getPresenter().getAllChallenges();
    }

    @NonNull
    @Override
    final public List<DBUser> getUsers() throws Exception {
        return getPresenter().getUsers();
    }

    @NonNull
    @Override
    final public Task<DBUser> asyncGetCurrentUserInfo() {
        return getPresenter().asyncGetCurrentUserInfo();
    }

    @NonNull
    @Override
    final public DBUser getCurrentUserInfo() throws Exception {
        return getPresenter().getCurrentUserInfo();
    }

    @Override
    final public void updateUserInfo(@NonNull final DBUser newUserInfo) throws Exception {
        getPresenter().updateUserInfo(newUserInfo);
    }

    @Override
    @NonNull
    public Task<Void> asyncUpdateUserInfo(@NonNull final DBUser newUserInfo) throws Exception {
        return getPresenter().asyncUpdateUserInfo(newUserInfo);
    }

    @Override
    final public void createNewUser(@NonNull final DBUser newUser) throws Exception {
        getPresenter().createNewUser(newUser);
    }

    @NonNull
    @Override
    final public List<Training> getUserTrainingsByUid(@NonNull final String uid) throws Exception {
        return getPresenter().getUserTrainingsByUid(uid);
    }

    @NonNull
    @Override
    final public List<Training> getUserTrainingsByDocumentId(@NonNull final String documentId) throws Exception {
        return getPresenter().getUserTrainingsByDocumentId(documentId);
    }

}
