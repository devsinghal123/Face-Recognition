package com.dev.facerecognitionapp.Employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;

import com.dev.facerecognitionapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmployeeLoginActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Initialize the camera provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    @SuppressLint("UnsafeExperimentalUsageError")
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        PreviewView previewView = findViewById(R.id.previewView);
        Button loginButton = findViewById(R.id.btnLogin);

        // Set up the image analysis use case for face recognition
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor, new FaceAnalyzer());

        // Select the front camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        try {
            // Unbind any previously bound use cases
            cameraProvider.unbindAll();

            // Bind the camera and use cases to the preview view
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, createPreviewUseCase(previewView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UseCase createPreviewUseCase(PreviewView previewView) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        return preview;
    }

    private class FaceAnalyzer implements ImageAnalysis.Analyzer {

        @Override
        public void analyze(ImageProxy image) {
            // Convert the ImageProxy to a Bitmap for face recognition
            Bitmap bitmap = imageToBitmap(image);

            // Perform face recognition on the bitmap

            // TODO: Add your face recognition code here

            // Close the image proxy after use
            image.close();
        }

        private Bitmap imageToBitmap(ImageProxy image) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
