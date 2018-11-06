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

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        final Button button_home = findViewById(R.id.home);
        final Button button_custom = findViewById(R.id.custom);
        final Button button_start = findViewById(R.id.start);
        final Button button_challenge = findViewById(R.id.challenge);
        final Button button_profile = findViewById(R.id.profile);

        button_home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        button_custom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment=null;
                fragment= new Customization();
                fragmentTransaction.replace(R.id.fragment,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, StartActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
        button_challenge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment=null;
                fragment= new Challenge();
                fragmentTransaction.replace(R.id.fragment,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        button_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment=null;
                fragment= new Profile();
                fragmentTransaction.replace(R.id.fragment,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
