package com.android.ui.app;

import android.os.Bundle;

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
import com.android.databinding.FragmentPostsBinding;

import org.json.JSONArray;
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

        binding.getLatestPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FutureTask<GetPostsRequestResult> getPosts= new FutureTask<>(new GetPostsRequest());
                ExecutorService exec = Executors.newSingleThreadExecutor();
                exec.submit(getPosts);
                try {
                    GetPostsRequestResult result = getPosts.get();
                    if (result.requestSucceeded) {
//                        for (int i = 0; i < result.posts.length(); i++) {
//                            try {
//                                JSONArray images = ((JSONObject) result.posts.get(i)).getJSONArray("images");
//                                GetImageRequest.GetImageRequestResult[] api_results = new GetImageRequest.GetImageRequestResult[images.length()];
//                                for (int j = 0; j < images.length(); j++) {
//                                    FutureTask<GetImageRequest.GetImageRequestResult> getImage = new FutureTask<>(new GetImageRequest((String) images.get(i)));
//                                    exec.submit(getImage);
//                                    api_results[i] = getImage.get();
//                                    System.out.println(api_results[i]);
//                                }
//                            } catch (Exception ignored) {}
//                        }

                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.recyclerView.setAdapter(new PostsAdapter(result.posts));
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error getting latest posts", Toast.LENGTH_SHORT).show());
                    }
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