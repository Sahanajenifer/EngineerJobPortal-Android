package com.example.engineerjobportal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ApplyJobActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etQualification;
    private EditText etExperience;
    private EditText etSkills;
    private EditText etCertifications;
    private EditText etCoverLetter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String jobTitle = "";
    private String company = "";
    private String location = "";
    private String salary = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        TextView tvApplyJobTitle = findViewById(R.id.tvApplyJobTitle);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etQualification = findViewById(R.id.etQualification);
        etExperience = findViewById(R.id.etExperience);
        etSkills = findViewById(R.id.etSkills);
        etCertifications = findViewById(R.id.etCertifications);
        etCoverLetter = findViewById(R.id.etCoverLetter);

        Button btnSubmitApplication = findViewById(R.id.btnSubmitApplication);

        // Receive selected job details
        jobTitle = getIntent().getStringExtra("jobTitle");
        company = getIntent().getStringExtra("company");
        location = getIntent().getStringExtra("location");
        salary = getIntent().getStringExtra("salary");

        // Prevent null values
        if (jobTitle == null) {
            jobTitle = "Job";
        }

        if (company == null) {
            company = "";
        }

        if (location == null) {
            location = "";
        }

        if (salary == null) {
            salary = "";
        }

        // Display selected job
        tvApplyJobTitle.setText(jobTitle);

        // Load logged-in applicant profile
        loadApplicantDetails();

        // Submit application
        btnSubmitApplication.setOnClickListener(v -> submitApplication());
    }

    private void loadApplicantDetails() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = currentUser.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        // Full Name
                        String name =
                                documentSnapshot.getString("fullName");

                        if (name != null && !name.isEmpty()) {
                            etFullName.setText(name);
                        }

                        // Email
                        String email =
                                documentSnapshot.getString("email");

                        if (email != null && !email.isEmpty()) {

                            etEmail.setText(email);

                        } else if (currentUser.getEmail() != null) {

                            etEmail.setText(
                                    currentUser.getEmail()
                            );
                        }

                        // Phone
                        String phone =
                                documentSnapshot.getString("phone");

                        if (phone != null && !phone.isEmpty()) {
                            etPhone.setText(phone);
                        }

                        // Qualification
                        String qualification =
                                documentSnapshot.getString(
                                        "qualification"
                                );

                        if (qualification != null &&
                                !qualification.isEmpty()) {

                            etQualification.setText(
                                    qualification
                            );
                        }

                        // Experience
                        String experience =
                                documentSnapshot.getString(
                                        "experience"
                                );

                        if (experience != null &&
                                !experience.isEmpty()) {

                            etExperience.setText(
                                    experience
                            );
                        }

                        // Skills
                        String skills =
                                documentSnapshot.getString(
                                        "skills"
                                );

                        if (skills != null &&
                                !skills.isEmpty()) {

                            etSkills.setText(skills);
                        }

                        // Certifications
                        String certifications =
                                documentSnapshot.getString(
                                        "certifications"
                                );

                        if (certifications != null &&
                                !certifications.isEmpty()) {

                            etCertifications.setText(
                                    certifications
                            );
                        }

                    } else {

                        if (currentUser.getEmail() != null) {

                            etEmail.setText(
                                    currentUser.getEmail()
                            );
                        }

                        Toast.makeText(
                                this,
                                "Profile details not found. Please complete your profile.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Failed to load profile details",
                        Toast.LENGTH_SHORT
                ).show());
    }

    private void submitApplication() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get applicant details
        String name =
                etFullName.getText()
                        .toString()
                        .trim();

        String email =
                etEmail.getText()
                        .toString()
                        .trim();

        String phone =
                etPhone.getText()
                        .toString()
                        .trim();

        String qualification =
                etQualification.getText()
                        .toString()
                        .trim();

        String experience =
                etExperience.getText()
                        .toString()
                        .trim();

        String skills =
                etSkills.getText()
                        .toString()
                        .trim();

        String certifications =
                etCertifications.getText()
                        .toString()
                        .trim();

        String coverLetter =
                etCoverLetter.getText()
                        .toString()
                        .trim();

        // Validation
        if (name.isEmpty()) {

            etFullName.setError(
                    "Enter your full name"
            );

            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {

            etEmail.setError(
                    "Enter your email"
            );

            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {

            etPhone.setError(
                    "Enter your phone number"
            );

            etPhone.requestFocus();
            return;
        }

        if (qualification.isEmpty()) {

            etQualification.setError(
                    "Enter your qualification"
            );

            etQualification.requestFocus();
            return;
        }

        if (experience.isEmpty()) {

            etExperience.setError(
                    "Enter your experience"
            );

            etExperience.requestFocus();
            return;
        }

        if (skills.isEmpty()) {

            etSkills.setError(
                    "Enter your skills"
            );

            etSkills.requestFocus();
            return;
        }

        if (certifications.isEmpty()) {

            etCertifications.setError(
                    "Enter your certifications"
            );

            etCertifications.requestFocus();
            return;
        }

        String uid =
                currentUser.getUid();

        // Create application
        Map<String, Object> application =
                new HashMap<>();

        // Applicant information
        application.put(
                "userId",
                uid
        );

        application.put(
                "applicant",
                name
        );

        application.put(
                "applicantName",
                name
        );

        application.put(
                "email",
                email
        );

        application.put(
                "phone",
                phone
        );

        application.put(
                "qualification",
                qualification
        );

        application.put(
                "experience",
                experience
        );

        application.put(
                "skills",
                skills
        );

        application.put(
                "certifications",
                certifications
        );

        application.put(
                "coverLetter",
                coverLetter
        );

        // Job information
        application.put(
                "jobTitle",
                jobTitle
        );

        application.put(
                "company",
                company
        );

        application.put(
                "location",
                location
        );

        application.put(
                "salary",
                salary
        );

        // Initial status
        application.put(
                "status",
                "Submitted"
        );

        // Timestamp
        application.put(
                "timestamp",
                FieldValue.serverTimestamp()
        );

        // Save to Firestore
        db.collection("applications")
                .add(application)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    ApplyJobActivity.this,
                                    "Application submitted successfully!",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                )
                .addOnFailureListener(e -> Toast.makeText(
                        ApplyJobActivity.this,
                        "Application failed: "
                                + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }
}