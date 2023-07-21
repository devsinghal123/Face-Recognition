package com.dev.facerecognitionapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.facerecognitionapp.Employee.EmployeeLoginActivity;
import com.dev.facerecognitionapp.admin.AdminLoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button admin_login,employee_login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        admin_login=findViewById(R.id.admin_login);
        employee_login=findViewById(R.id.employee_login);

        admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        employee_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EmployeeLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Confirmation")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}