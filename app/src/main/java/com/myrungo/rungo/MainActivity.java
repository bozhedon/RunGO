package com.myrungo.rungo;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button_home = findViewById(R.id.home);
        Button button_custom = findViewById(R.id.custom);
        Button button_start = findViewById(R.id.start);
        Button button_challenge = findViewById(R.id.challenge);
        Button button_profile = findViewById(R.id.profile);


        button_home.setOnClickListener(this);
        button_custom.setOnClickListener(this);
        button_start.setOnClickListener(this);
        button_challenge.setOnClickListener(this);
        button_profile.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.home:
                for (Fragment fragments:getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragments).commit();
            }
                break;
            case R.id.custom:
                fragment = new Customization();
                replaceFragment(fragment);
                break;
            case R.id.start:
                Intent myIntent = new Intent(MainActivity.this, StartActivity.class);
                MainActivity.this.startActivity(myIntent);
                break;
            case R.id.challenge:
                fragment = new Challenge();
                replaceFragment(fragment);
                break;
            case R.id.profile:
                fragment = new Profile();
                replaceFragment(fragment);
                break;
        }
    }
    public void replaceFragment(Fragment someFragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.commit();
    }
}
