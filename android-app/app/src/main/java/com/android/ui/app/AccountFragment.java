package com.android.ui.app;

import static com.android.Global.imageApiUrl;

import android.content.Context;
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
import com.android.api.GetImageRequest;
import com.android.api.LoginRequest;
import com.android.databinding.FragmentAccountBinding;
import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    private AccountFragment.AccountFragmentListener listener;


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

        FutureTask<AccountRetrievalRequest.AccountRetrievalRequestResult> account = new FutureTask<>(new AccountRetrievalRequest(Global.loggedInUserID));
        Global.executorService.submit(account);
        Global.executorService.execute(() -> {
            String username = "", phoneNumber = "", email = "";
            try {
                AccountRetrievalRequest.AccountRetrievalRequestResult result = account.get();

                System.out.println(imageApiUrl + result.account.get("profile_pic_id"));

                // Set the values to the appropriate elements in the JSON
                final String finalUsername = result.account.get("display_name").toString();
                final String finalEmail = result.account.get("email").toString();
                final String finalPhoneNumber = result.account.get("phone_number").toString();
                String profilePicId = result.account.getString("profile_pic_id");

                getActivity().runOnUiThread(() -> {
                    Glide.with(getContext())
                            .load(imageApiUrl + profilePicId)
                            .into(binding.profilePicture);
                    binding.accountUsername.setText(finalUsername);
                    binding.accountEmail.setText(finalEmail);
                    binding.accountPhoneNumber.setText(finalPhoneNumber);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        binding.signOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppActivity app = (AppActivity) getActivity();
                Intent loginIntent = new Intent(app, LoginAndRegisterActivity.class);
                startActivity(loginIntent);
            }
        });

        binding.accountUsername.setText("DefaultDisplayName");
        binding.accountEmail.setText("DefaultEmail");
        binding.accountPhoneNumber.setText("DefaultPhoneNumber");

        binding.accountInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goToEditAccount();
            }
        });

        binding.accountPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goToEditPassword();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //    Interface for communicating with the activity
    public interface AccountFragmentListener {
        void goToEditAccount();

        void goToEditPassword();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

//        Make sure that the context implements this interface
        if (context instanceof AccountFragment.AccountFragmentListener) {
            listener = (AccountFragment.AccountFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AccountFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }
}