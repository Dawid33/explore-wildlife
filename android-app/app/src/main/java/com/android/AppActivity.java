package com.android;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.api.LoginRequest;
import com.android.databinding.ActivityAppBinding;
import com.android.ui.app.AccountEditFragment;
import com.android.ui.app.AccountFragment;
import com.android.ui.app.CreatePostFragment;
import com.android.ui.app.PasswordEditFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class AppActivity extends AppCompatActivity implements CreatePostFragment.CreatePostFragmentListener, AccountFragment.AccountFragmentListener, AccountEditFragment.AccountEditFragmentListener, PasswordEditFragment.PasswordEditFragmentListener {
    private ActivityAppBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private Button locationButton;
    private String cityName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Code for the location bar up top.
        locationButton = binding.locationButton;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(this);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            cityName = addresses.get(0).getLocality();
                            locationButton.setText(cityName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        bottomNav.setOnItemSelectedListener((MenuItem item) -> {
            if (item.getItemId() == R.id.bottom_nav_camera ||
                item.getItemId() == R.id.bottom_nav_maps) {
                binding.locationButton.setVisibility(View.GONE);
                binding.goToAccountButton.setVisibility(View.GONE);
                binding.toggleNotificationsButton.setVisibility(View.GONE);
            } else {
                binding.locationButton.setVisibility(View.VISIBLE);
                binding.goToAccountButton.setVisibility(View.VISIBLE);
                binding.toggleNotificationsButton.setVisibility(View.VISIBLE);
            }
            navController.navigate(item.getItemId());
            return true;
        });
        binding.goToAccountButton.setOnClickListener(view -> {
            navController.navigate(R.id.bottom_nav_account);
        });
        binding.locationButton.setOnClickListener(view1 -> {
            // Get the user's location and set the location button to the nearest city.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationPermissionRequest.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(this);
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                cityName = addresses.get(0).getLocality();
                                locationButton.setText(cityName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        });

        // In case we are debugging and starting the app from this activity
        // log in with the test account so that we have his uuid for
//        if (Global.loggedInUserID == null) {
//            FutureTask<LoginRequest.LoginRequestResult> login = new FutureTask<>(new LoginRequest("test@example.com", "test"));
//            ExecutorService exec = Executors.newSingleThreadExecutor();
//            exec.submit(login);
//            try {
//                Global.loggedInUserID = login.get().userUuid;
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    // Getting location permission making sure its fine and coarse. Printing a toast if not.
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Location permission granted fully.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            Toast.makeText(this, "Please grant precise permissions before using the main app.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Please grant all location permissions before using the main app.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    public void onPostCreateSuccess() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_createPost_to_bottom_nav_home);

    }

    @Override
    public void goToEditAccount() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        navController.navigate(R.id.action_bottom_nav_account_to_accountEditFragment);
    }

    @Override
    public void goToEditPassword() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        navController.navigate(R.id.action_bottom_nav_account_to_passwordEditFragment);
    }

    @Override
    public void goToBackEditAccount() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        navController.navigate(R.id.action_accountEditFragment_to_bottom_nav_account);
    }

    @Override
    public void goToBackEditAccountFromPasswordEdit() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        navController.navigate(R.id.action_passwordEditFragment_to_bottom_nav_account);
    }
}

