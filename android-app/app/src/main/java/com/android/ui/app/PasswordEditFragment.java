package com.android.ui.app;

import static com.android.Global.imageApiUrl;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.Global;
import com.android.R;
import com.android.api.AccountRetrievalRequest;
import com.android.api.UpdatePasswordRequest;
import com.android.api.UpdateProfileRequest;
import com.android.databinding.FragmentAccountEditBinding;
import com.android.databinding.FragmentPasswordEditBinding;
import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class PasswordEditFragment extends Fragment {

    private FragmentPasswordEditBinding binding;

    private PasswordEditFragment.PasswordEditFragmentListener listener;

    public PasswordEditFragment() {
        // Required empty public constructor
    }

    //    Interface for communicating with the activity
    public interface PasswordEditFragmentListener {
        void goToBackEditAccountFromPasswordEdit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_password_edit, container, false);
        // Inflate the layout for this fragment

        binding = FragmentPasswordEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = String.valueOf(binding.passwordChangeInput.getText());
                String passwordConfirm = String.valueOf(binding.passwordChangeConfirmInput.getText());

                if(password.equals(passwordConfirm)){
                    FutureTask<UpdatePasswordRequest.UpdatePasswordRequestResult> updatePassword = new FutureTask<>(new UpdatePasswordRequest( password));
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    exec.submit(updatePassword);
                    try {
                        UpdatePasswordRequest.UpdatePasswordRequestResult result = updatePassword.get();
                        if (!result.isRegistered) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Update Error: " + result.error, Toast.LENGTH_SHORT).show());
                        }
                        listener.goToBackEditAccountFromPasswordEdit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Inputs must match!!", Toast.LENGTH_SHORT).show());
                }

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

//        Make sure that the context implements this interface
        if (context instanceof AccountFragment.AccountFragmentListener) {
            listener = (PasswordEditFragment.PasswordEditFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PasswordEditFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }
}