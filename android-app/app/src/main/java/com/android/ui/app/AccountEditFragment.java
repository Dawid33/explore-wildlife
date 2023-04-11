package com.android.ui.app;

import static com.android.Global.imageApiUrl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.Global;
import com.android.R;
import com.android.api.AccountRetrievalRequest;
import com.android.databinding.FragmentAccountEditBinding;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.FutureTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountEditFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private FragmentAccountEditBinding binding;
    private String mParam1;
    private String mParam2;
    private Drawable selectedImageDrawable = null;

    public AccountEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountEditFragment newInstance(String param1, String param2) {
        AccountEditFragment fragment = new AccountEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                    binding.usernameInput.setText(finalUsername);
                    binding.emailInput.setText(finalEmail);
                    binding.phoneNumberInput.setText(finalPhoneNumber);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}