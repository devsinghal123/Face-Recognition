package com.dev.facerecognitionapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.facerecognitionapp.R;
import com.dev.facerecognitionapp.admin.UI.AdminDashboardActivity;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText admin_email;
    private EditText admin_Password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        admin_email = findViewById(R.id.admin_email);
        admin_Password = findViewById(R.id.admin_pass);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            // User is already logged in, proceed to dashboard
            goToDashboard();
        }
    }

    public void onAdminLoginClick(View view) {
        // Get the entered email and password
        String email = admin_email.getText().toString().trim();
        String password = admin_Password.getText().toString().trim();

        // Check if both email and password fields are filled
        if (!email.isEmpty() && !password.isEmpty()) {
            // Check if the email and password match the admin credentials
            if (email.equals("admin@example.com") && password.equals("admin123")) {
                // Login successful
                Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show();

                // Store the login state
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", true);
                editor.apply();

                // Proceed to dashboard
                goToDashboard();
            } else {
                // Login failed
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Fields are not filled
            Toast.makeText(this, "Please fill in both email and password", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToDashboard() {
        // Open the admin dashboard or perform necessary actions
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish(); // Prevents the user from returning to the login activity by pressing the back button
    }
}
