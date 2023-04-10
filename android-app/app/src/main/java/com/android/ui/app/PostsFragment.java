package com.android.ui.app;

import android.content.Intent;
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

import com.android.AppActivity;
import com.android.Global;
import com.android.LoginAndRegisterActivity;
import com.android.PostModel;
import com.android.api.AccountRetrievalRequest;
import com.android.api.GetPostsRequest;
import com.android.api.LikePostRequest;
import com.android.api.RegisterRequest;
import com.android.api.UploadImageRequest;
import com.android.databinding.FragmentPostsBinding;
import com.android.ui.app.adapters.PostsAdapter;
import com.android.ui.app.interfaces.PostsRecyclerViewInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import org.json.JSONException;
import org.json.JSONObject;

//    ================= TEST CODE ================
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class PostsFragment extends Fragment implements PostsRecyclerViewInterface {


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
        Global.executorService.submit(getPosts);
        PostsFragment p = this;
        Global.executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get posts from backend to populate the recycler view.
                    GetPostsRequest.GetPostsRequestResult result = getPosts.get();

                    if (result.requestSucceeded) {
                        getActivity().runOnUiThread(() -> {
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            try {
                                binding.recyclerView.setAdapter(new PostsAdapter(result.posts, p));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
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
    public void onLikeClicked(PostModel postModel) {
        postModel.setLiked(!postModel.isLiked());

        FutureTask<LikePostRequest.LikePostRequestResult> like = new FutureTask<>(new LikePostRequest(postModel.getPostID()));
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(like);
        try {
            LikePostRequest.LikePostRequestResult result = like.get();
            if (!result.requestSucceeded) {
//                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Like Error: " + result.error, Toast.LENGTH_SHORT).show());
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Like Error", Toast.LENGTH_SHORT).show());

            }

            // Switch activity no matter what in case login system doesn't work.
//            LoginAndRegisterActivity currentActivity = (LoginAndRegisterActivity)getActivity();
//            Intent app = new Intent(currentActivity, AppActivity.class);
//            startActivity(app);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Toast.makeText(this.getContext(), "Hi!!", Toast.LENGTH_SHORT).show();
    }
}