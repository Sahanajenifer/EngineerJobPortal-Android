package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText etProfileName;
    private EditText etProfileEmail;
    private EditText etProfilePhone;
    private EditText etProfileQualification;
    private EditText etProfileExperience;
    private EditText etProfileSkills;
    private EditText etProfileCertifications;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Connect XML fields
        etProfileName = findViewById(R.id.etProfileName);
        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfilePhone = findViewById(R.id.etProfilePhone);
        etProfileQualification = findViewById(R.id.etProfileQualification);
        etProfileExperience = findViewById(R.id.etProfileExperience);
        etProfileSkills = findViewById(R.id.etProfileSkills);
        etProfileCertifications = findViewById(R.id.etProfileCertifications);

        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Load existing profile
        loadProfile();

        // Save profile
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // Logout
        btnLogout.setOnClickListener(v -> {

            auth.signOut();

            Toast.makeText(
                    ProfileActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    WelcomeActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            return;
        }

        String userId = user.getUid();

        if (user.getEmail() != null) {
            etProfileEmail.setText(user.getEmail());
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name =
                                documentSnapshot.getString("fullName");

                        String email =
                                documentSnapshot.getString("email");

                        String phone =
                                documentSnapshot.getString("phone");

                        String qualification =
                                documentSnapshot.getString("qualification");

                        String experience =
                                documentSnapshot.getString("experience");

                        String skills =
                                documentSnapshot.getString("skills");

                        String certifications =
                                documentSnapshot.getString("certifications");

                        if (name != null) {
                            etProfileName.setText(name);
                        }

                        if (email != null) {
                            etProfileEmail.setText(email);
                        }

                        if (phone != null) {
                            etProfilePhone.setText(phone);
                        }

                        if (qualification != null) {
                            etProfileQualification.setText(
                                    qualification
                            );
                        }

                        if (experience != null) {
                            etProfileExperience.setText(
                                    experience
                            );
                        }

                        if (skills != null) {
                            etProfileSkills.setText(skills);
                        }

                        if (certifications != null) {
                            etProfileCertifications.setText(
                                    certifications
                            );
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        ProfileActivity.this,
                        "Failed to load profile",
                        Toast.LENGTH_SHORT
                ).show());
    }

    private void saveProfile() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    ProfileActivity.this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String fullName =
                etProfileName.getText()
                        .toString()
                        .trim();

        String email =
                etProfileEmail.getText()
                        .toString()
                        .trim();

        String phone =
                etProfilePhone.getText()
                        .toString()
                        .trim();

        String qualification =
                etProfileQualification.getText()
                        .toString()
                        .trim();

        String experience =
                etProfileExperience.getText()
                        .toString()
                        .trim();

        String skills =
                etProfileSkills.getText()
                        .toString()
                        .trim();

        String certifications =
                etProfileCertifications.getText()
                        .toString()
                        .trim();

        // Basic validation
        if (fullName.isEmpty()) {
            etProfileName.setError("Enter your name");
            etProfileName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etProfileEmail.setError("Enter your email");
            etProfileEmail.requestFocus();
            return;
        }

        if (qualification.isEmpty()) {
            etProfileQualification.setError(
                    "Enter your qualification"
            );
            etProfileQualification.requestFocus();
            return;
        }

        if (skills.isEmpty()) {
            etProfileSkills.setError(
                    "Enter your skills"
            );
            etProfileSkills.requestFocus();
            return;
        }

        if (certifications.isEmpty()) {
            etProfileCertifications.setError(
                    "Enter your certifications"
            );
            etProfileCertifications.requestFocus();
            return;
        }

        // Profile data
        Map<String, Object> profile =
                new HashMap<>();

        profile.put("fullName", fullName);
        profile.put("email", email);
        profile.put("phone", phone);
        profile.put("qualification", qualification);
        profile.put("experience", experience);
        profile.put("skills", skills);
        profile.put("certifications", certifications);

        String userId = user.getUid();

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(profile)
                .addOnSuccessListener(unused -> Toast.makeText(
                        ProfileActivity.this,
                        "Profile saved successfully!",
                        Toast.LENGTH_LONG
                ).show())
                .addOnFailureListener(e -> Toast.makeText(
                        ProfileActivity.this,
                        "Failed to save profile: "
                                + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }
}