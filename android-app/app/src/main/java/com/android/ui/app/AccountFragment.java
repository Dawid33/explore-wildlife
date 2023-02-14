package com.android.ui.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.AppActivity;
import com.android.CameraActivity;
import com.android.LoginAndRegisterActivity;
import com.android.R;
import com.android.databinding.FragmentAccountBinding;
import com.android.databinding.FragmentPostsBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();

        // Connecting with the buttons in the layout.
//        previewView = findViewById(R.id.camPreview);
//        toggleFlash = findViewById(R.id.flashToggle);
//        capture = findViewById(R.id.capture);
//
//        // Again checking permissions.
//        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            activityResultLauncher.launch(Manifest.permission.CAMERA);
//        } else {
//            startCamera();
//        }

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppActivity app = (AppActivity) getActivity();
                Intent loginIntent = new Intent(app, LoginAndRegisterActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}