package com.android.ui.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.api.GetImageRequest;
import com.android.api.GetPostsRequest;
import com.android.api.GetPostsRequest.GetPostsRequestResult;
import com.android.api.UploadImageRequest;
import com.android.databinding.FragmentPostsBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class PostsFragment extends Fragment {
    private FragmentPostsBinding binding;

    public static final int PICK_IMAGE = 1;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                System.out.println(uri.toString());

//                Glide.with(context
//                        .load(url)
//                        .into(new CustomTarget<Drawable>() {
//                            @Override
//                            public void onResourceReady(Drawable resource, Transition<Drawable> transition) {
//                                // Do something with the Drawable here.
//                            }
//
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) {
//                                // Remove the Drawable provided in onResourceReady from any Views and ensure
//                                // no references to it remain.
//                            }
//                        });
                FutureTarget<Bitmap> futureTarget =
                        Glide.with(getContext())
                                .asBitmap()
                                .load(uri)
                                .submit();

                try {
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    FutureTask<Bitmap> getImage = new FutureTask<>(futureTarget::get);
                    exec.submit(getImage);
                    Bitmap image = getImage.get();

                    FutureTask<UploadImageRequest.UploadImageRequestResult> getPosts= new FutureTask<>(new UploadImageRequest(image, getActivity().getExternalFilesDir(null)));
                    exec.submit(getPosts);
                    UploadImageRequest.UploadImageRequestResult result = getPosts.get();

                    if (result.requestSucceeded) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show());
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error uploading image", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    });

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

        binding.getLatestPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FutureTask<GetPostsRequestResult> getPosts = new FutureTask<>(new GetPostsRequest());
                ExecutorService exec = Executors.newSingleThreadExecutor();
                exec.submit(getPosts);
                try {
                    GetPostsRequestResult result = getPosts.get();
                    if (result.requestSucceeded) {
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//                        Adding adapter
                        binding.recyclerView.setAdapter(new PostsAdapter(result.posts));
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error getting latest posts", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
        binding.createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//                        Adding adapter
                binding.recyclerView.setAdapter(new PostsAdapter("hi"));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}