package com.android.ui.app;
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
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.AppActivity;
import com.android.databinding.FragmentAccountBinding;
import com.android.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.CameraActivity;
import com.android.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    FragmentCameraBinding binding;
    // Two buttons in the application.
    private ImageButton capture, toggleFlash;

    // Image preview that's in the background.
    private PreviewView previewView;

    // Context in which to run camera related stuff
    private Context safeContext;

    // This is the launcher for the permission request.
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            startCamera();
        } else {
            Toast.makeText(getActivity(), "Please grant all permissions before using the camera.", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        safeContext = context;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);

        AppActivity activity = (AppActivity)(getActivity());

        previewView = binding.camPreview;
        toggleFlash = binding.flashToggle;
        capture = binding.capture;

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }
        return binding.getRoot();
    }

    public void startCamera() {
        // This is the aspect ratio of the preview.
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());

        // This is the camera provider.
        ListenableFuture listenableFuture = ProcessCameraProvider.getInstance(safeContext);

        listenableFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available.
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                // Set up the preview use case to display camera preview.
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                // Set up the capture use case to allow users to take photos.
                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation()).build();

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
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        }, ContextCompat.getMainExecutor(safeContext));
    }

    // This method will take the picture and save it to external storage.
    public void takePicture(ImageCapture imageCapture) {
        // This is the file where the image will be saved and it's name.
        final File file = new File(getActivity().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");

        // This is the output file options.
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        // This is the function that takes the picture.
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Image Saved: " + file.getPath(), Toast.LENGTH_SHORT).show());
                try {
                    // Insert the image into the media store database so it shows up in the gallery.
                    MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(),
                            file.getAbsolutePath(),
                            String.valueOf(System.currentTimeMillis()),
                            "From App");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Failed to Save: " + error.getMessage(), Toast.LENGTH_SHORT).show());
//                startCamera(activity);
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
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Flash is Not Available Currently", Toast.LENGTH_SHORT).show());
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