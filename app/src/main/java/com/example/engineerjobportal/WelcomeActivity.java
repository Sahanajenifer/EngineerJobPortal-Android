package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button btnLogin, btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // Open Login Screen
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Open Register Screen
        btnCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}