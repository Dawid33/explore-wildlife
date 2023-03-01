package com.android;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.databinding.ActivityAppBinding;
import com.android.ui.app.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppActivity extends AppCompatActivity {
    private ActivityAppBinding binding;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        bottomNav.setOnItemSelectedListener((MenuItem item) -> {
            if (item.getItemId() == R.id.bottom_nav_camera ||
                item.getItemId() == R.id.bottom_nav_maps) {
                binding.goToAccountButton.setVisibility(View.GONE);
                binding.toggleNotificationsButton.setVisibility(View.GONE);
            } else {
                binding.goToAccountButton.setVisibility(View.VISIBLE);
                binding.toggleNotificationsButton.setVisibility(View.VISIBLE);
            }
            navController.navigate(item.getItemId());
            return true;
        });
        binding.goToAccountButton.setOnClickListener(view -> {
            navController.navigate(R.id.bottom_nav_account);
        });

    }
}

