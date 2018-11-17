package com.myrungo.rungo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.myrungo.rungo.login.LoginActivity;

public final class SplashActivity extends AppCompatActivity {

    @Override
    final protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @NonNull final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
