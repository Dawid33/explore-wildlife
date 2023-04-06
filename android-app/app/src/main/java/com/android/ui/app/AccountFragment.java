package com.android.ui.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.AppActivity;
import com.android.Global;
import com.android.LoginAndRegisterActivity;
import com.android.api.AccountRetrievalRequest;
import com.android.api.LoginRequest;
import com.android.databinding.FragmentAccountBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        PUT CODE HERE FOR PULLING USER INFORMATION FROM THE DATABASE
        String username = "DefaultDisplayName";
        String email = "DefaultEmail";
        String phoneNumber = "DefaultPhoneNumber";

        FutureTask<AccountRetrievalRequest.AccountRetrievalRequestResult> account = new FutureTask<>(new AccountRetrievalRequest(Global.loggedInUserID));
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(account);

        try {
            AccountRetrievalRequest.AccountRetrievalRequestResult result = account.get();

            // Set the values to the appropriate elements in the JSON
            username = result.account.get("display_name").toString();
            email = result.account.get("email").toString();
            phoneNumber = result.account.get("phone_number").toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.signOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppActivity app = (AppActivity) getActivity();
                Intent loginIntent = new Intent(app, LoginAndRegisterActivity.class);
                startActivity(loginIntent);
            }
        });

        binding.accountUsername.setText(username);

        binding.accountEmail.setText(email);

        binding.accountPhoneNumber.setText(phoneNumber);

        binding.accountInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.accountPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}