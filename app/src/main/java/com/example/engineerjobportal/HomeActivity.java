package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnAdminApplications;

    private LinearLayout jobResultsContainer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // UI elements
        etSearch = findViewById(R.id.etSearch);

        Button btnSearch = findViewById(R.id.btnSearch);

        Button btnSoftware = findViewById(R.id.btnSoftware);
        Button btnElectrical = findViewById(R.id.btnElectrical);
        Button btnMechanical = findViewById(R.id.btnMechanical);
        Button btnCivil = findViewById(R.id.btnCivil);

        Button btnProfile = findViewById(R.id.btnProfile);
        btnAdminApplications = findViewById(R.id.btnAdminApplications);

        Button btnHomeNav = findViewById(R.id.btnHomeNav);
        Button btnApplicationsNav = findViewById(R.id.btnApplicationsNav);
        Button btnProfileNav = findViewById(R.id.btnProfileNav);

        jobResultsContainer = findViewById(R.id.jobResultsContainer);

        // Check Admin account
        checkAdminAccess();

        // Search button
        btnSearch.setOnClickListener(v -> {

            String searchText =
                    etSearch.getText()
                            .toString()
                            .trim()
                            .toLowerCase();

            if (searchText.isEmpty()) {

                Toast.makeText(
                        HomeActivity.this,
                        "Please enter a job to search.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            searchJobs(searchText);
        });

        // Software category
        btnSoftware.setOnClickListener(v -> {

            etSearch.setText(R.string.software_engineer);

            searchJobs("software");
        });

        // Electrical category
        btnElectrical.setOnClickListener(v -> {

            etSearch.setText(R.string.electrical_engineer);

            searchJobs("electrical");
        });

        // Mechanical category
        btnMechanical.setOnClickListener(v -> {

            etSearch.setText(R.string.mechanical_engineer);

            searchJobs("mechanical");
        });

        // Civil category
        btnCivil.setOnClickListener(v -> {

            etSearch.setText(R.string.civil_engineer);

            searchJobs("civil");
        });

        // My Profile button
        btnProfile.setOnClickListener(v -> openProfile());

        // Bottom Home
        btnHomeNav.setOnClickListener(v -> Toast.makeText(
                HomeActivity.this,
                "Home",
                Toast.LENGTH_SHORT
        ).show());

        // Bottom Applications
        btnApplicationsNav.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    MyApplicationsActivity.class
            );

            startActivity(intent);
        });

        // Bottom Profile
        btnProfileNav.setOnClickListener(v -> openProfile());
    }

    // ----------------------------------------------------
    // ADMIN ACCESS
    // ----------------------------------------------------

    private void checkAdminAccess() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {

            String email = currentUser.getEmail();

            if (email != null &&
                    email.equalsIgnoreCase("admin01@gmail.com")) {

                // Admin account
                btnAdminApplications.setVisibility(View.VISIBLE);

                btnAdminApplications.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            HomeActivity.this,
                            AdminApplicationsActivity.class
                    );

                    startActivity(intent);
                });

            } else {

                // Normal user
                btnAdminApplications.setVisibility(View.GONE);
            }

        } else {

            // No logged-in user
            btnAdminApplications.setVisibility(View.GONE);
        }
    }

    // ----------------------------------------------------
    // PROFILE
    // ----------------------------------------------------

    private void openProfile() {

        Intent intent = new Intent(
                HomeActivity.this,
                ProfileActivity.class
        );

        startActivity(intent);
    }

    // ----------------------------------------------------
    // SEARCH JOBS
    // ----------------------------------------------------

    private void searchJobs(String searchText) {

        jobResultsContainer.removeAllViews();

        TextView searching =
                new TextView(HomeActivity.this);

        searching.setText("Searching...");
        searching.setTextSize(16);
        searching.setPadding(
                10,
                20,
                10,
                20
        );

        jobResultsContainer.addView(searching);

        db.collection("jobs")
                .get()
                .addOnCompleteListener(task -> {

                    jobResultsContainer.removeAllViews();

                    if (!task.isSuccessful()) {

                        Toast.makeText(
                                HomeActivity.this,
                                "Firestore error",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    boolean found = false;

                    for (QueryDocumentSnapshot document :
                            task.getResult()) {

                        String title =
                                document.getString("title");

                        String company =
                                document.getString("company");

                        String location =
                                document.getString("location");

                        String salary =
                                document.getString("salary");

                        String experience =
                                document.getString("experience");

                        if (title != null &&
                                title.toLowerCase()
                                        .contains(searchText)) {

                            found = true;

                            TextView jobView =
                                    new TextView(
                                            HomeActivity.this
                                    );

                            String jobDisplayText = title + " - " + company;
                            jobView.setText(jobDisplayText);

                            jobView.setTextSize(18);

                            jobView.setPadding(
                                    20,
                                    30,
                                    20,
                                    30
                            );

                            jobView.setClickable(true);
                            jobView.setFocusable(true);

                            // Open Job Details
                            jobView.setOnClickListener(v -> {

                                Intent intent =
                                        new Intent(
                                                HomeActivity.this,
                                                JobDetailsActivity.class
                                        );

                                intent.putExtra(
                                        "title",
                                        title
                                );

                                intent.putExtra(
                                        "company",
                                        company
                                );

                                intent.putExtra(
                                        "location",
                                        location
                                );

                                intent.putExtra(
                                        "salary",
                                        salary
                                );

                                intent.putExtra(
                                        "experience",
                                        experience
                                );

                                startActivity(intent);
                            });

                            jobResultsContainer.addView(
                                    jobView,
                                    new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                            );
                        }
                    }

                    if (!found) {

                        TextView noJobs =
                                new TextView(
                                        HomeActivity.this
                                );

                        String noJobsText = "No jobs found for \"" + searchText + "\".";
                        noJobs.setText(noJobsText);

                        noJobs.setTextSize(16);

                        noJobs.setPadding(
                                10,
                                20,
                                10,
                                20
                        );

                        jobResultsContainer.addView(
                                noJobs
                        );
                    }
                });
    }
}