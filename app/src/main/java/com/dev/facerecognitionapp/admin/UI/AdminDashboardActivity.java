package com.dev.facerecognitionapp.admin.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dev.facerecognitionapp.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView add_employee,edit_employee;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        add_employee = (CardView) findViewById(R.id.AddEmployee);
        edit_employee=findViewById(R.id.EditEmployee);



        add_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this,AddEmployeeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        edit_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this,EditEmployeeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}