package com.android.ui.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.AppActivity;
import com.android.LoginAndRegisterActivity;
import com.android.R;
import com.android.databinding.FragmentHomeBinding;
import com.android.databinding.FragmentPostsBinding;

public class PostsFragment extends Fragment {
    private FragmentPostsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
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