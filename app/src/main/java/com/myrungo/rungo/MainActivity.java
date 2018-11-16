package com.myrungo.rungo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationViewEx.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    replaceFragment(fragment);
                    return true;
                case R.id.custom:
                    fragment = new CustomFragment();
                    replaceFragment(fragment);
                    return true;
                case R.id.challenge:
                    fragment = new ChallengeFragment();
                    replaceFragment(fragment);
                    return true;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    replaceFragment(fragment);
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
        replaceFragment(fragment);
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bnve);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setupBottomNavigationView(bottomNavigationViewEx);
    }

    public void replaceFragment(Fragment someFragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
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
