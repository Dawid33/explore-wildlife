package com.android.ui.app;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.api.CreatePostRequest;
import com.android.api.RegisterRequest;
import com.android.databinding.FragmentCreatePostBinding;
import com.android.ui.LoginFragment;
import com.android.ui.RegisterFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CreatePostFragment extends Fragment {

    private String coordinates = null;
    private String cityName = null;

    private Bitmap postImage = null;

    private double latitude = 0, longitude = 0;
    private FragmentCreatePostBinding binding;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getContext());
                        try {

                            // Gets the coordinates of the location and concatenates them into a string.

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

//                            coordinates = location.getLatitude() + ", " + location.getLongitude();
                            coordinates = latitude + ", " + longitude;


                            // Gets city closest to cooridantes.
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            cityName = addresses.get(0).getLocality();
                            binding.postLocationInput.setText(cityName);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);

        setThumbnail();

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCreatePostRequest();

            }
        });
    }

    // Getting location permission making sure its fine and coarse. Printing a toast if not.
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Location permission granted fully.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            Toast.makeText(getContext(), "Please grant precise permissions before using the main app.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Please grant all location permissions before using the main app.", Toast.LENGTH_SHORT).show();
                        }

                        Boolean readStorageGranted = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);

                        if (readStorageGranted) {
//                            Nice.
                        } else {
                            Toast.makeText(getContext(), "App needs to view thumbnails", Toast.LENGTH_SHORT).show();

                        }
                    }
            );


    private void setThumbnail() {
        assert getArguments() != null;
        String UriString = CreatePostFragmentArgs.fromBundle(getArguments()).getPhotoPath();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

//        Bitmap bitmap = BitmapFactory.decodeFile(UriString,bmOptions);

        postImage = BitmapFactory.decodeFile(UriString, bmOptions);

//        bitmap = BitmapFactory.decodeFile(UriString,bmOptions);
        // Rotate the thumbnail to the correct orientation.
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        postImage = Bitmap.createBitmap(postImage, 0, 0, postImage.getWidth(), postImage.getHeight(), matrix, true);

        binding.createPostImage.setImageBitmap(postImage);
    }

    private void sendCreatePostRequest() {
        String postTitle = String.valueOf(binding.postTitleInput.getText());
        String postDescription = String.valueOf(binding.postDescriptionInput.getText());

        FutureTask<CreatePostRequest.CreatePostRequestResult> createPost = new FutureTask<>(new CreatePostRequest(Globals.getUserID(), postTitle, postDescription, latitude, longitude, postImage));
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(createPost);
        try {
            CreatePostRequest.CreatePostRequestResult result = createPost.get();
            if (!result.hasError) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Create Post Error: " + result.error, Toast.LENGTH_SHORT).show());
            }

            // Switch activity no matter what in case login system doesn't work.
//            LoginAndRegisterActivity currentActivity = (LoginAndRegisterActivity)getActivity();
//            Intent app = new Intent(currentActivity, AppActivity.class);
//            startActivity(app);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}