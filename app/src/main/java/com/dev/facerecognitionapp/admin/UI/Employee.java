package com.dev.facerecognitionapp.admin.UI;

public class Employee {
    private String name;
    private String dob;
    private String workField;
    private String base64Image; // Add the base64Image field

    // Required empty constructor for Firebase
    public Employee() {
    }

    public Employee(String name, String dob, String workField, String base64Image) {
        this.name = name;
        this.dob = dob;
        this.workField = workField;
        this.base64Image = base64Image;
    }

    // Getters and setters for the fields

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getWorkField() {
        return workField;
    }

    public void setWorkField(String workField) {
        this.workField = workField;
    }
}
