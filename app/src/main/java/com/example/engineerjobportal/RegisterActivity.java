package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect XML views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Register button
        btnRegister.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Check full name
            if (name.isEmpty()) {
                etName.setError("Enter your full name");
                etName.requestFocus();
                return;
            }

            // Check email
            if (email.isEmpty()) {
                etEmail.setError("Enter your email");
                etEmail.requestFocus();
                return;
            }

            // Check phone number
            if (phone.isEmpty()) {
                etPhone.setError("Enter your phone number");
                etPhone.requestFocus();
                return;
            }

            // Check password
            if (password.isEmpty()) {
                etPassword.setError("Enter your password");
                etPassword.requestFocus();
                return;
            }

            // Check confirm password
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Confirm your password");
                etConfirmPassword.requestFocus();
                return;
            }

            // Check password match
            if (!Objects.equals(password, confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }

            // Create Firebase account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                // Save full name as Firebase display name
                                UserProfileChangeRequest profileUpdates =
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {

                                            Toast.makeText(
                                                    RegisterActivity.this,
                                                    "Account created successfully!",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            // Go to Login screen
                                            Intent intent = new Intent(
                                                    RegisterActivity.this,
                                                    LoginActivity.class
                                            );

                                            startActivity(intent);
                                            finish();
                                        });

                            }

                        } else {

                            String errorMessage = (task.getException() != null)
                                    ? task.getException().getMessage()
                                    : "Registration failed";

                            Toast.makeText(
                                    RegisterActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}