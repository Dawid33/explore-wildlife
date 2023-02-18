package com.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.AppActivity;
import com.android.LoginAndRegisterActivity;
import com.android.R;
import com.android.api.LoginRequest;
import com.android.api.RegisterRequest;
import com.android.databinding.FragmentRegistrationBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class RegisterFragment extends Fragment {
    private FragmentRegistrationBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
//        return null;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(RegisterFragment.this)
                        .navigate(R.id.action_registerFragment_to_LoginFragment);
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(binding.emailInput.getText());
                String password = String.valueOf(binding.passwordInput.getText());

                FutureTask<LoginRequest.LoginRequestResult> login = new FutureTask<>(new LoginRequest(email, password));
                FutureTask<RegisterRequest.RegisterRequestResult> register = new FutureTask<>(new RegisterRequest());
                ExecutorService exec = Executors.newSingleThreadExecutor();
                exec.submit(login);
                try {
                    LoginRequest.LoginRequestResult result = login.get();
                    if (!result.isLoggedIn) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Login Error: " + result.error, Toast.LENGTH_SHORT).show());
                    }

                    // Switch activity no matter what in case login system doesn't work.
                    LoginAndRegisterActivity currentActivity = (LoginAndRegisterActivity)getActivity();
                    Intent app = new Intent(currentActivity, AppActivity.class);
                    startActivity(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
