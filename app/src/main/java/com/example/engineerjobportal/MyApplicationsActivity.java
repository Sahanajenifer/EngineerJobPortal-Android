package com.example.engineerjobportal;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashSet;
import java.util.Set;

public class MyApplicationsActivity extends AppCompatActivity {

    private LinearLayout applicationsContainer;
    private TextView tvNoApplications;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_applications);

        applicationsContainer =
                findViewById(R.id.applicationsContainer);

        tvNoApplications =
                findViewById(R.id.tvNoApplications);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadApplications();
    }

    // =========================================
    // LOAD APPLICATIONS
    // =========================================

    private void loadApplications() {

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser == null) {

            tvNoApplications.setText(
                    R.string.my_applications_subtitle
            );

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String userId =
                currentUser.getUid();

        db.collection("applications")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    applicationsContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        tvNoApplications.setText(
                                "No applications found."
                        );

                        tvNoApplications.setVisibility(
                                View.VISIBLE
                        );

                        return;
                    }

                    tvNoApplications.setText("");

                    tvNoApplications.setVisibility(
                            View.GONE
                    );

                    // =========================================
                    // REMOVE DUPLICATE DISPLAY
                    // =========================================

                    Set<String> displayedApplications =
                            new HashSet<>();

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String rawJobTitle = document.getString("jobTitle");
                        String jobTitle;

                        if (rawJobTitle == null || rawJobTitle.trim().isEmpty()) {
                            String altJob = document.getString("job");
                            jobTitle = altJob != null ? altJob : "";
                        } else {
                            jobTitle = rawJobTitle;
                        }

                        String rawCompany = document.getString("company");
                        String company = rawCompany != null ? rawCompany : "";

                        String applicationKey =
                                userId.trim() +
                                        "_" +
                                        jobTitle
                                                .trim()
                                                .toLowerCase() +
                                        "_" +
                                        company
                                                .trim()
                                                .toLowerCase();

                        // Skip duplicate application
                        if (displayedApplications
                                .contains(applicationKey)) {

                            continue;
                        }

                        displayedApplications.add(
                                applicationKey
                        );

                        showApplication(document);
                    }
                })
                .addOnFailureListener(e -> {

                    tvNoApplications.setText(
                            "Unable to load applications."
                    );

                    tvNoApplications.setVisibility(
                            View.VISIBLE
                    );

                    Toast.makeText(
                            this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================
    // SHOW APPLICATION CARD
    // =========================================

    private void showApplication(
            QueryDocumentSnapshot document) {

        // =========================================
        // GET DATA
        // =========================================

        String applicantName = extractApplicantName(document);

        String email = document.getString("email");
        String phone = document.getString("phone");

        String rawJobTitle = document.getString("jobTitle");
        String rawJob = document.getString("job");
        String jobTitle = !isEmpty(rawJobTitle) ? rawJobTitle : (!isEmpty(rawJob) ? rawJob : "");

        String company = document.getString("company");
        String location = document.getString("location");
        String salary = document.getString("salary");
        String qualification = document.getString("qualification");
        String experience = document.getString("experience");
        String skills = document.getString("skills");
        String certifications = document.getString("certifications");

        String rawStatus = document.getString("status");
        String status = !isEmpty(rawStatus) ? rawStatus : "Submitted";

        // =========================================
        // CARD
        // =========================================

        CardView card =
                new CardView(this);

        card.setRadius(22);
        card.setCardElevation(7);
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                22
        );

        card.setLayoutParams(cardParams);

        // =========================================
        // CARD CONTENT
        // =========================================

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                24,
                24,
                24,
                24
        );

        // =========================================
        // APPLICATION LABEL
        // =========================================

        TextView applicationLabel =
                createTextView(
                        "JOB APPLICATION",
                        12,
                        Color.rgb(21, 101, 192),
                        true
                );

        content.addView(
                applicationLabel
        );

        // =========================================
        // JOB TITLE
        // =========================================

        TextView jobView =
                createTextView(
                        safeValue(jobTitle),
                        21,
                        Color.rgb(18, 59, 99),
                        true
                );

        jobView.setPadding(
                0,
                7,
                0,
                2
        );

        content.addView(
                jobView
        );

        // =========================================
        // COMPANY
        // =========================================

        TextView companyView =
                createTextView(
                        safeValue(company),
                        16,
                        Color.DKGRAY,
                        true
                );

        content.addView(
                companyView
        );

        addDivider(content);

        // =========================================
        // APPLICANT
        // =========================================

        content.addView(
                createLabel("APPLICANT")
        );

        content.addView(
                createValue(applicantName)
        );

        // =========================================
        // EMAIL
        // =========================================

        content.addView(
                createLabel("EMAIL")
        );

        content.addView(
                createValue(email)
        );

        // =========================================
        // PHONE
        // =========================================

        content.addView(
                createLabel("PHONE")
        );

        content.addView(
                createValue(phone)
        );

        // =========================================
        // LOCATION
        // =========================================

        content.addView(
                createLabel("LOCATION")
        );

        content.addView(
                createValue(location)
        );

        // =========================================
        // SALARY
        // =========================================

        content.addView(
                createLabel("SALARY")
        );

        content.addView(
                createValue(salary)
        );

        addDivider(content);

        // =========================================
        // QUALIFICATION
        // =========================================

        content.addView(
                createLabel("QUALIFICATION")
        );

        content.addView(
                createValue(qualification)
        );

        // =========================================
        // EXPERIENCE
        // =========================================

        content.addView(
                createLabel("EXPERIENCE")
        );

        content.addView(
                createValue(experience)
        );

        // =========================================
        // SKILLS
        // =========================================

        content.addView(
                createLabel("SKILLS")
        );

        content.addView(
                createValue(skills)
        );

        // =========================================
        // CERTIFICATIONS
        // =========================================

        content.addView(
                createLabel("CERTIFICATIONS")
        );

        content.addView(
                createValue(certifications)
        );

        addDivider(content);

        // =========================================
        // APPLICATION STATUS
        // =========================================

        TextView statusLabel =
                createTextView(
                        "APPLICATION STATUS",
                        12,
                        Color.rgb(107, 114, 128),
                        true
                );

        statusLabel.setGravity(
                Gravity.CENTER
        );

        statusLabel.setPadding(
                0,
                5,
                0,
                5
        );

        content.addView(
                statusLabel
        );

        TextView statusView =
                createTextView(
                        getStatusText(status),
                        17,
                        getStatusColor(status),
                        true
                );

        statusView.setGravity(
                Gravity.CENTER
        );

        statusView.setPadding(
                0,
                8,
                0,
                5
        );

        content.addView(
                statusView
        );

        // =========================================
        // ADD CONTENT TO CARD
        // =========================================

        card.addView(
                content
        );

        // =========================================
        // ADD CARD TO SCREEN
        // =========================================

        applicationsContainer.addView(
                card
        );
    }

    private String extractApplicantName(QueryDocumentSnapshot document) {
        String name = document.getString("applicantName");
        if (!isEmpty(name)) return name;
        String applicant = document.getString("applicant");
        if (!isEmpty(applicant)) return applicant;
        String fullName = document.getString("fullName");
        if (!isEmpty(fullName)) return fullName;
        return "Not provided";
    }

    // =========================================
    // CREATE TEXT VIEW
    // =========================================

    private TextView createTextView(
            String text,
            float size,
            int color,
            boolean bold) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(
                size
        );

        textView.setTextColor(
                color
        );

        if (bold) {

            textView.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return textView;
    }

    // =========================================
    // CREATE LABEL
    // =========================================

    private TextView createLabel(
            String text) {

        TextView view =
                createTextView(
                        text,
                        12,
                        Color.rgb(
                                107,
                                114,
                                128
                        ),
                        true
                );

        view.setPadding(
                0,
                10,
                0,
                3
        );

        return view;
    }

    // =========================================
    // CREATE VALUE
    // =========================================

    private TextView createValue(
            String value) {

        TextView view =
                createTextView(
                        safeValue(value),
                        15,
                        Color.rgb(
                                55,
                                65,
                                81
                        ),
                        false
                );

        view.setPadding(
                0,
                0,
                0,
                3
        );

        return view;
    }

    // =========================================
    // DIVIDER
    // =========================================

    private void addDivider(
            LinearLayout layout) {

        View divider =
                new View(this);

        divider.setBackgroundColor(
                Color.rgb(
                        225,
                        229,
                        234
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        params.setMargins(
                0,
                18,
                0,
                8
        );

        divider.setLayoutParams(
                params
        );

        layout.addView(
                divider
        );
    }

    // =========================================
    // STATUS TEXT
    // =========================================

    private String getStatusText(
            String status) {

        if (status == null) {
            return "Submitted";
        }

        if (status.equalsIgnoreCase("Shortlisted")) {

            return "✓ SHORTLISTED";
        }

        if (status.equalsIgnoreCase("Rejected")) {

            return "✕ REJECTED";
        }

        if (status.equalsIgnoreCase("Under Review")) {

            return "● UNDER REVIEW";
        }

        if (status.equalsIgnoreCase("Applied")) {

            return "● APPLIED";
        }

        return status.toUpperCase();
    }

    // =========================================
    // STATUS COLOR
    // =========================================

    private int getStatusColor(
            String status) {

        if (status == null) {

            return Color.rgb(
                    210,
                    140,
                    20
            );
        }

        if (status.equalsIgnoreCase(
                "Shortlisted")) {

            return Color.rgb(
                    34,
                    139,
                    70
            );
        }

        if (status.equalsIgnoreCase(
                "Rejected")) {

            return Color.rgb(
                    190,
                    50,
                    50
            );
        }

        if (status.equalsIgnoreCase(
                "Under Review")) {

            return Color.rgb(
                    210,
                    140,
                    20
            );
        }

        return Color.rgb(
                21,
                101,
                192
        );
    }

    // =========================================
    // CHECK EMPTY
    // =========================================

    private boolean isEmpty(
            String value) {

        return value == null ||
                value.trim().isEmpty();
    }

    // =========================================
    // SAFE VALUE
    // =========================================

    private String safeValue(
            String value) {

        if (isEmpty(value)) {

            return "Not provided";
        }

        return value;
    }
}