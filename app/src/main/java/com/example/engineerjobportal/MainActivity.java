package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {

            FirebaseUser currentUser = auth.getCurrentUser();

            Intent intent;

            if (currentUser != null) {

                // User is already logged in
                intent = new Intent(
                        MainActivity.this,
                        HomeActivity.class
                );

            } else {

                // User is not logged in
                intent = new Intent(
                        MainActivity.this,
                        WelcomeActivity.class
                );
            }

            startActivity(intent);
            finish();

        }, SPLASH_TIME);
    }
}