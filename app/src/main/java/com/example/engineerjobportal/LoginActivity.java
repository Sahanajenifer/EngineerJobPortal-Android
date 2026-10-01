package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Connect XML views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Login button
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Check empty fields
            if (email.isEmpty()) {
                etEmail.setError("Enter your email");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter your password");
                etPassword.requestFocus();
                return;
            }

            // Firebase login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login successful!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    LoginActivity.this,
                                    HomeActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            String failureMessage = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error";

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login failed: " + failureMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}