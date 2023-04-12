package com.android.ui.app;

import static com.android.Global.imageApiUrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.Global;
import com.android.api.AccountRetrievalRequest;
import com.android.api.UpdateProfileRequest;
import com.android.databinding.FragmentAccountEditBinding;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class AccountEditFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private FragmentAccountEditBinding binding;
    private Drawable selectedImageDrawable = null;

    private AccountEditFragment.AccountEditFragmentListener listener;


    public AccountEditFragment() {
        // Required empty public constructor
    }

    //    Interface for communicating with the activity
    public interface AccountEditFragmentListener {
        void goToBackEditAccount();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void imageChoser() {
        // Choosing a single image.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Launching the Intent.
        launchImagePicker.launch(intent);
    }

    ActivityResultLauncher<Intent> launchImagePicker
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContext().getContentResolver(),
                                    selectedImageUri);
                            selectedImageDrawable = new BitmapDrawable(getResources(), selectedImageBitmap);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        binding.profilePictureEdit.setImageDrawable(selectedImageDrawable);
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAccountEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                            .into(binding.profilePictureEdit);
                    binding.usernameEditInput.setText(finalUsername);
                    binding.emailEditInput.setText(finalEmail);
                    binding.phoneNumberEditInput.setText(finalPhoneNumber);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        binding.profilePictureEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChoser();
            }
        });

        binding.saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(binding.usernameEditInput.getText());
                String email = String.valueOf(binding.emailEditInput.getText());
                String phoneNumber = String.valueOf(binding.phoneNumberEditInput
                        .getText());

                FutureTask<UpdateProfileRequest.UpdateProfileRequestResult> updateProfile = new FutureTask<>(new UpdateProfileRequest( username, email, phoneNumber));
                ExecutorService exec = Executors.newSingleThreadExecutor();
                exec.submit(updateProfile);
                try {
                    UpdateProfileRequest.UpdateProfileRequestResult result = updateProfile.get();
                    if (!result.isRegistered) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Update Error: " + result.error, Toast.LENGTH_SHORT).show());
                    }
                    listener.goToBackEditAccount();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

//        Make sure that the context implements this interface
        if (context instanceof AccountFragment.AccountFragmentListener) {
            listener = (AccountEditFragment.AccountEditFragmentListener) context;
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