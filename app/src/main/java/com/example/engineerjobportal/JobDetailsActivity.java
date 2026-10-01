package com.example.engineerjobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JobDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // -----------------------------
        // Connect XML views
        // -----------------------------
        TextView tvJobTitle = findViewById(R.id.tvJobTitle);
        TextView tvCompany = findViewById(R.id.tvCompany);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvSalary = findViewById(R.id.tvSalary);
        TextView tvExperience = findViewById(R.id.tvExperience);

        Button btnApply = findViewById(R.id.btnApply);

        // -----------------------------
        // Receive job details
        // -----------------------------
        String title = getIntent().getStringExtra("title");
        String company = getIntent().getStringExtra("company");
        String location = getIntent().getStringExtra("location");
        String salary = getIntent().getStringExtra("salary");
        String experience = getIntent().getStringExtra("experience");

        // Prevent null values from appearing
        String displayTitle = title != null ? title : "Job Title";
        String displayCompany = company != null ? company : "Not provided";
        String displayLocation = location != null ? location : "Not provided";
        String displaySalary = salary != null ? salary : "Not provided";
        String displayExperience = experience != null ? experience : "Not provided";

        // -----------------------------
        // Display job details
        // -----------------------------
        tvJobTitle.setText(displayTitle);

        String companyText = "Company: " + displayCompany;
        tvCompany.setText(companyText);

        String locationText = "Location: " + displayLocation;
        tvLocation.setText(locationText);

        String salaryText = "Salary: " + displaySalary;
        tvSalary.setText(salaryText);

        String experienceText = "Experience: " + displayExperience;
        tvExperience.setText(experienceText);

        // -----------------------------
        // APPLY NOW
        // -----------------------------
        btnApply.setOnClickListener(v -> {

            Intent intent = new Intent(
                    JobDetailsActivity.this,
                    ApplyJobActivity.class
            );

            // Send all job information
            // to ApplyJobActivity
            intent.putExtra(
                    "jobTitle",
                    displayTitle
            );

            intent.putExtra(
                    "company",
                    displayCompany
            );

            intent.putExtra(
                    "location",
                    displayLocation
            );

            intent.putExtra(
                    "salary",
                    displaySalary
            );

            intent.putExtra(
                    "experience",
                    displayExperience
            );

            startActivity(intent);
        });
    }
}