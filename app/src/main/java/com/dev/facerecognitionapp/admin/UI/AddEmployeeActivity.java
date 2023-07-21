package com.dev.facerecognitionapp.admin.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.facerecognitionapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEmployeeActivity extends AppCompatActivity {
    private EditText etDOB,empName,empField;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    private PreviewView previewView;
    private ImageView imageView;
    private Button btnCapture,btnAddEmployee;

    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private DatabaseReference databaseRef;
    private String base64Image;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        etDOB = findViewById(R.id.etDOB);
        empName=findViewById(R.id.etName);
        empField=findViewById(R.id.etWorkField);
        previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.ivPhoto);
        btnCapture = findViewById(R.id.btnTakePhoto);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }

            private void capturePhoto() {
                File outputDirectory = getOutputDirectory();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentTime = sdf.format(System.currentTimeMillis());
                File photoFile = new File(outputDirectory, "IMG_" + currentTime + ".jpg");

                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(photoFile).build();

                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(AddEmployeeActivity.this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Check if the photo file is successfully created
                        if (photoFile.exists()) {
                            // Image capture is successful, display the captured image
                            Uri savedUri = Uri.fromFile(photoFile);
                            Bitmap bitmap = BitmapFactory.decodeFile(savedUri.getPath());
                            imageView.setImageBitmap(bitmap);

                            // Convert the bitmap to a base64 encoded string
                            base64Image = encodeBitmapToBase64(bitmap);


                        } else {
                            // Error: Failed to create photo file
                            Toast.makeText(AddEmployeeActivity.this, "Failed to capture image. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Image capture failed, show an error message
                        Toast.makeText(AddEmployeeActivity.this, "Failed to capture image. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            private String encodeBitmapToBase64(Bitmap bitmap) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            }

            private File getOutputDirectory() {
                File mediaDir = getExternalMediaDirs()[0];
                File outputDirectory = new File(mediaDir, "captures");

                if (!outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        Log.e("CameraActivity", "Failed to create output directory.");
                        return null;
                    }
                }
                return outputDirectory;
            }


        });


        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // Set up the preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());

                    // Set up image capture
                    imageCapture = new ImageCapture.Builder().build();

                    // Select front camera as the default
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build();

                    // Attach the camera to the preview
                    Camera camera = cameraProvider.bindToLifecycle(AddEmployeeActivity.this, cameraSelector, preview, imageCapture);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void showDatePickerDialog(View view) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set the initial date to the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                // Update the EditText with the selected date
                String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                etDOB.setText(selectedDate);
            }
        }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();

        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = empName.getText().toString().trim();
                String dob = etDOB.getText().toString().trim();
                String workField = empField.getText().toString().trim();

                // Perform data validation if necessary
                if (name.isEmpty() || dob.isEmpty() || workField.isEmpty() || base64Image.isEmpty()) {
                    Toast.makeText(AddEmployeeActivity.this, "Please fill all fields and capture the photo", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a unique key for the employee
                String employeeId = databaseRef.child("employees").push().getKey();

                // Create an Employee object to store data
                Employee employee = new Employee(name, dob, workField, base64Image);

                // Save the employee data to Firebase
                databaseRef.child("employees").child(employeeId).setValue(employee)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddEmployeeActivity.this, "Employee added successfully", Toast.LENGTH_SHORT).show();

                                // Clear the input fields after successful addition
                                empName.setText("");
                                etDOB.setText("");
                                empField.setText("");
                                imageView.setImageBitmap(null); // Clear the imageView
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddEmployeeActivity.this, "Failed to add employee", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}