package com.android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    // Two buttons in the application.
    private ImageButton capture, toggleFlash;

    // Image preview that's in the background.
    private PreviewView previewView;

    // This is the launcher for the permission request.
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            startCamera();
        } else {
            Toast.makeText(this, "Please grant all permissions before using the camera.", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Connecting with the buttons in the layout.
        previewView = findViewById(R.id.camPreview);
        toggleFlash = findViewById(R.id.flashToggle);
        capture = findViewById(R.id.capture);

        // Again checking permissions.
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }
    }

    public void startCamera() {
        // This is the aspect ratio of the preview.
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());

        // This is the camera provider.
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available.
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                // Set up the preview use case to display camera preview.
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                // Set up the capture use case to allow users to take photos.
                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                // Choose the camera by requiring a lens facing.
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Unbind use cases before rebinding.
                cameraProvider.unbindAll();

                // Bind use cases to camera.
                Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture);

                // This is the function that takes the picture.
                capture.setOnClickListener(v -> {
                    // Checking for the write permission.
                    if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    } else {
                        takePicture(imageCapture);
                    }
                });

                // This is the function that toggles the flash.
                toggleFlash.setOnClickListener(v -> setFlashIcon(camera));

                // This is the function that sets the preview.
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // This method will take the picture and save it to external storage.
    private void takePicture(ImageCapture imageCapture) {
        // This is the file where the image will be saved and it's name.
        final File file = new File(getExternalFilesDir(null),  System.currentTimeMillis() + ".jpg");

        // This is the output file options.
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        // This is the function that takes the picture.
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            // This is the function that will be called when the image is saved.
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Image Saved: " + file.getPath(), Toast.LENGTH_SHORT).show());
                startCamera();
            }

            // This is the function that will be called when the image is not saved.
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Failed to Save: " + error.getMessage(), Toast.LENGTH_SHORT).show());
                startCamera();
            }
        });
    }

    // This method will set the flash icon depending on the current state of the flash.
    private void setFlashIcon(Camera camera){
        // Checking if the flash is available.
        if (camera.getCameraInfo().hasFlashUnit()) {
            // Checking if the flash is on or off.
            if (camera.getCameraInfo().getTorchState().getValue() == 0){
                // Turning the flash on.
                camera.getCameraControl().enableTorch(true);
                // Setting the flash icon to off.
                toggleFlash.setImageResource(R.drawable.baseline_flash_off_24);
            } else {
                // Turning the flash off.
                camera.getCameraControl().enableTorch(false);
                // Setting the flash icon to on.
                toggleFlash.setImageResource(R.drawable.baseline_flash_on_24);
            }
        } else {
            // If the flash is not available then show a toast.
            runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Flash is Not Available Currently", Toast.LENGTH_SHORT).show());
        }
    }

    // This method will return the aspect ratio of the preview.
    private int aspectRatio(int width, int height) {
        // This is the aspect ratio of the preview.
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        // Checking if the aspect ratio is 4:3 or 16:9.
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
}