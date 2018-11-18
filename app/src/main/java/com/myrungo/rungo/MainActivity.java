package com.myrungo.rungo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity{

    private boolean first = true;
    private int position = 1;
    private User user;
    public static final String USER_TAG = "USER_TAG";
    private BottomNavigationViewEx.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    if(position !=1)
                    {
                        fragment = new HomeFragment();
                        replaceFragment(fragment);
                        position=1;
                    }
                    return true;
                case R.id.custom:
                    if (position!=2)
                    {
                        fragment = new CustomFragment();
                        replaceFragment(fragment);
                        position=2;
                    }
                    return true;
                case R.id.challenge:
                    if (position!=4)
                    {
                        fragment = new ChallengeFragment();
                        replaceFragment(fragment);
                        position=4;
                    }
                    return true;
                case R.id.profile:
                    if (position!=5)
                    {
                        fragment = new ProfileFragment();
                        replaceFragment(fragment);
                        position=5;
                    }
                    return true;
            }
            return false;
        }
    };

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab_start);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

        Fragment fragment = new HomeFragment();
        user = new User(CatView.Skins.BUSINESS, CatView.Heads.ANGRY);
        Bundle user_bundle = new Bundle();
        user_bundle.putSerializable(USER_TAG,user);
        fragment.setArguments(user_bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container,fragment);
        transaction.commit();
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bnve);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setupBottomNavigationView(bottomNavigationViewEx);



    }

    public void replaceFragment(Fragment someFragment) {
        Bundle user_bundle = new Bundle();
        user_bundle.putSerializable(USER_TAG,user);
        someFragment.setArguments(user_bundle);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if(first)
        {
            transaction.addToBackStack(null);
            first=false;
        }
        transaction.commit();
    }
    //bottom navigation view customization
    private void setupBottomNavigationView(BottomNavigationViewEx bnve){
        bnve.enableItemShiftingMode(false);
        bnve.enableShiftingMode(false);
        bnve.enableAnimation(false);
        bnve.setTextVisibility(false);
    }

}
