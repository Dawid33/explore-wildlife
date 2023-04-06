package com.android.ui.app;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.api.AccountRetrievalRequest;
import com.android.api.GetPostsRequest;
import com.android.api.UploadImageRequest;
import com.android.databinding.FragmentPostsBinding;
import com.android.ui.app.adapters.PostsAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FutureTask<GetPostsRequest.GetPostsRequestResult> getPosts = new FutureTask<>(new GetPostsRequest());
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(getPosts);
        // TODO: This will probably cause problems in the future as it will make the app slow.
        // The data should be loaded in the background in a non-blocking way.
        try {
            // Get posts from backend to populate the recycler view.
            GetPostsRequest.GetPostsRequestResult result = getPosts.get();

            if (result.requestSucceeded) {
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recyclerView.setAdapter(new PostsAdapter(result.posts));
            } else {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error getting latest posts", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}