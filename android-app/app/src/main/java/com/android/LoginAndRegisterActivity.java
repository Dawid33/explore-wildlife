package com.android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.android.databinding.ActivityLoginAndRegistrationBinding;

import android.view.Menu;
import android.view.MenuItem;

public class LoginAndRegisterActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginAndRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginAndRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}