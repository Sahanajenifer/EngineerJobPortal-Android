package com.example.engineerjobportal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class AdminApplicationsActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout adminApplicationsContainer;
    private TextView tvAdminNoApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // =========================================
        // ADMIN SECURITY CHECK
        // =========================================

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null ||
                currentUser.getEmail() == null ||
                !currentUser.getEmail()
                        .equalsIgnoreCase("admin01@gmail.com")) {

            Toast.makeText(
                    AdminApplicationsActivity.this,
                    "Access denied. Admin only.",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent = new Intent(
                    AdminApplicationsActivity.this,
                    HomeActivity.class
            );

            startActivity(intent);
            finish();
            return;
        }

        // =========================================
        // ADMIN SCREEN
        // =========================================

        setContentView(R.layout.activity_admin_applications);

        adminApplicationsContainer =
                findViewById(R.id.adminApplicationsContainer);

        tvAdminNoApplications =
                findViewById(R.id.tvAdminNoApplications);

        loadApplications();
    }

    // =========================================
    // LOAD APPLICATIONS
    // =========================================

    private void loadApplications() {

        adminApplicationsContainer.removeAllViews();

        tvAdminNoApplications.setVisibility(View.GONE);

        db.collection("applications")
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                AdminApplicationsActivity.this,
                                "Unable to load applications.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (task.getResult().isEmpty()) {

                        tvAdminNoApplications.setVisibility(
                                View.VISIBLE
                        );

                        tvAdminNoApplications.setText(
                                R.string.my_applications_subtitle
                        );

                        return;
                    }

                    // =========================================
                    // REMOVE DUPLICATES FROM DISPLAY
                    // =========================================

                    Set<String> displayedApplications =
                            new HashSet<>();

                    for (QueryDocumentSnapshot document :
                            task.getResult()) {

                        String rawUserId = document.getString("userId");
                        String userId = rawUserId != null ? rawUserId : "";

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

                        // -----------------------------------------
                        // Create unique application key
                        //
                        // Same user + same job + same company
                        // = duplicate
                        // -----------------------------------------

                        String applicationKey =
                                userId.trim() + "_" +
                                        jobTitle.trim().toLowerCase() + "_" +
                                        company.trim().toLowerCase();

                        // -----------------------------------------
                        // Skip duplicate application
                        // -----------------------------------------

                        if (displayedApplications
                                .contains(applicationKey)) {

                            continue;
                        }

                        displayedApplications.add(
                                applicationKey
                        );

                        // Display application
                        createApplicationCard(document);
                    }
                });
    }

    private String extractApplicantName(QueryDocumentSnapshot document) {
        String name = document.getString("applicantName");
        if (name != null && !name.trim().isEmpty()) return name;
        String fullName = document.getString("fullName");
        if (fullName != null && !fullName.trim().isEmpty()) return fullName;
        String applicant = document.getString("applicant");
        if (applicant != null && !applicant.trim().isEmpty()) return applicant;
        String capApplicant = document.getString("Applicant");
        if (capApplicant != null && !capApplicant.trim().isEmpty()) return capApplicant;
        return "Not provided";
    }

    // =========================================
    // CREATE PROFESSIONAL APPLICATION CARD
    // =========================================

    private void createApplicationCard(
            QueryDocumentSnapshot document) {

        // =========================================
        // GET DATA
        // =========================================

        String applicantName = extractApplicantName(document);

        String rawJobTitle = document.getString("jobTitle");
        String rawJob = document.getString("job");
        String jobTitle = (rawJobTitle != null && !rawJobTitle.trim().isEmpty())
                ? rawJobTitle : (rawJob != null ? rawJob : "");

        String company = document.getString("company");
        String email = document.getString("email");
        String phone = document.getString("phone");
        String qualification = document.getString("qualification");
        String experience = document.getString("experience");
        String skills = document.getString("skills");
        String certifications = document.getString("certifications");
        String rawStatus = document.getString("status");
        String status = (rawStatus != null && !rawStatus.trim().isEmpty()) ? rawStatus : "Applied";

        // =========================================
        // CALCULATE SCORE
        // =========================================

        int score = calculateApplicantScore(
                qualification,
                experience,
                skills,
                certifications
        );

        String recommendation;

        if (score >= 80) {

            recommendation = "SHORTLIST";

        } else if (score >= 60) {

            recommendation = "UNDER REVIEW";

        } else {

            recommendation = "REJECT";
        }

        // =========================================
        // CARD
        // =========================================

        CardView card =
                new CardView(
                        AdminApplicationsActivity.this
                );

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
                20
        );

        card.setLayoutParams(cardParams);

        // =========================================
        // CARD CONTENT
        // =========================================

        LinearLayout content =
                new LinearLayout(
                        AdminApplicationsActivity.this
                );

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
                        "APPLICATION",
                        13,
                        Color.rgb(21, 101, 192),
                        true
                );

        content.addView(applicationLabel);

        // =========================================
        // APPLICANT NAME
        // =========================================

        TextView applicantView =
                createTextView(
                        safeValue(applicantName),
                        22,
                        Color.rgb(18, 59, 99),
                        true
                );

        applicantView.setPadding(
                0,
                8,
                0,
                3
        );

        content.addView(applicantView);

        // =========================================
        // JOB
        // =========================================

        TextView jobView =
                createTextView(
                        safeValue(jobTitle),
                        17,
                        Color.DKGRAY,
                        true
                );

        content.addView(jobView);

        // =========================================
        // COMPANY
        // =========================================

        TextView companyView =
                createTextView(
                        safeValue(company),
                        15,
                        Color.GRAY,
                        false
                );

        content.addView(companyView);

        addDivider(content);

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
        // APPLICANT SCORE
        // =========================================

        TextView scoreTitle =
                createTextView(
                        "APPLICANT SCORE",
                        13,
                        Color.rgb(21, 101, 192),
                        true
                );

        scoreTitle.setGravity(
                Gravity.CENTER
        );

        content.addView(scoreTitle);

        String scoreText = score + " / 100";
        TextView scoreView =
                createTextView(
                        scoreText,
                        30,
                        Color.rgb(18, 59, 99),
                        true
                );

        scoreView.setGravity(
                Gravity.CENTER
        );

        scoreView.setPadding(
                0,
                6,
                0,
                8
        );

        content.addView(scoreView);

        // =========================================
        // RECOMMENDATION
        // =========================================

        String recommendationText = "Recommendation: " + recommendation;
        TextView recommendationView =
                createTextView(
                        recommendationText,
                        16,
                        getRecommendationColor(score),
                        true
                );

        recommendationView.setGravity(
                Gravity.CENTER
        );

        recommendationView.setPadding(
                0,
                4,
                0,
                15
        );

        content.addView(
                recommendationView
        );

        addDivider(content);

        // =========================================
        // CURRENT STATUS
        // =========================================

        TextView statusTitle =
                createLabel(
                        "APPLICATION STATUS"
                );

        content.addView(
                statusTitle
        );

        TextView currentStatus =
                createValue(status);

        currentStatus.setTextSize(16);

        currentStatus.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                currentStatus
        );

        // =========================================
        // STATUS DROPDOWN
        // =========================================

        Spinner statusSpinner =
                new Spinner(
                        AdminApplicationsActivity.this
                );

        String[] statusOptions = {

                "Applied",
                "Under Review",
                "Shortlisted",
                "Rejected"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        AdminApplicationsActivity.this,
                        android.R.layout.simple_spinner_item,
                        statusOptions
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        statusSpinner.setAdapter(
                adapter
        );

        // =========================================
        // SELECT CURRENT STATUS
        // =========================================

        for (int i = 0;
             i < statusOptions.length;
             i++) {

            if (statusOptions[i]
                    .equalsIgnoreCase(status)) {

                statusSpinner.setSelection(i);

                break;
            }
        }

        // =========================================
        // UPDATE STATUS
        // =========================================

        statusSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    boolean firstCall = true;

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (firstCall) {

                            firstCall = false;

                            return;
                        }

                        String newStatus =
                                statusOptions[position];

                        db.collection("applications")
                                .document(
                                        document.getId()
                                )
                                .update(
                                        "status",
                                        newStatus
                                )
                                .addOnSuccessListener(
                                        unused -> {

                                            currentStatus
                                                    .setText(
                                                            newStatus
                                                    );

                                            Toast.makeText(
                                                    AdminApplicationsActivity.this,
                                                    "Application status updated",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                )
                                .addOnFailureListener(
                                        e -> Toast.makeText(
                                                AdminApplicationsActivity.this,
                                                "Failed to update status",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        content.addView(
                statusSpinner
        );

        // =========================================
        // ADD CONTENT TO CARD
        // =========================================

        card.addView(
                content
        );

        // =========================================
        // ADD CARD TO ADMIN SCREEN
        // =========================================

        adminApplicationsContainer.addView(
                card
        );
    }

    // =========================================
    // SCORE CALCULATION
    // =========================================

    private int calculateApplicantScore(
            String qualification,
            String experience,
            String skills,
            String certifications) {

        int score = 0;

        // -----------------------------------------
        // Qualification - 25 points
        // -----------------------------------------

        if (qualification != null &&
                !qualification.trim().isEmpty()) {

            score += 25;
        }

        // -----------------------------------------
        // Skills - 30 points
        // -----------------------------------------

        if (skills != null &&
                !skills.trim().isEmpty()) {

            String lowerSkills =
                    skills.toLowerCase();

            int skillCount =
                    lowerSkills.split(",").length;

            if (skillCount >= 4) {

                score += 30;

            } else if (skillCount == 3) {

                score += 24;

            } else if (skillCount == 2) {

                score += 18;

            } else {

                score += 10;
            }
        }

        // -----------------------------------------
        // Experience - 20 points
        // -----------------------------------------

        if (experience != null &&
                !experience.trim().isEmpty()) {

            String exp =
                    experience.toLowerCase();

            if (exp.contains("fresher")) {

                score += 10;

            } else if (exp.contains("1")) {

                score += 14;

            } else if (exp.contains("2")) {

                score += 17;

            } else {

                score += 20;
            }
        }

        // -----------------------------------------
        // Certifications - 10 points
        // -----------------------------------------

        if (certifications != null &&
                !certifications.trim().isEmpty()) {

            int certCount =
                    certifications.split(",").length;

            if (certCount >= 3) {

                score += 10;

            } else if (certCount == 2) {

                score += 8;

            } else {

                score += 5;
            }
        }

        // -----------------------------------------
        // Profile completeness - 15 points
        // -----------------------------------------

        int profileFields = 0;

        if (qualification != null &&
                !qualification.trim().isEmpty()) {

            profileFields++;
        }

        if (experience != null &&
                !experience.trim().isEmpty()) {

            profileFields++;
        }

        if (skills != null &&
                !skills.trim().isEmpty()) {

            profileFields++;
        }

        if (certifications != null &&
                !certifications.trim().isEmpty()) {

            profileFields++;
        }

        if (profileFields == 4) {

            score += 15;

        } else if (profileFields == 3) {

            score += 11;

        } else if (profileFields == 2) {

            score += 7;

        } else if (profileFields == 1) {

            score += 3;
        }

        return Math.min(
                score,
                100
        );
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
                new TextView(
                        AdminApplicationsActivity.this
                );

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
                12,
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
                new View(
                        AdminApplicationsActivity.this
                );

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
    // RECOMMENDATION COLOR
    // =========================================

    private int getRecommendationColor(
            int score) {

        if (score >= 80) {

            return Color.rgb(
                    34,
                    139,
                    70
            );
        }

        if (score >= 60) {

            return Color.rgb(
                    210,
                    140,
                    20
            );
        }

        return Color.rgb(
                190,
                50,
                50
        );
    }

    // =========================================
    // SAFE VALUE
    // =========================================

    private String safeValue(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "Not provided";
        }

        return value;
    }
}