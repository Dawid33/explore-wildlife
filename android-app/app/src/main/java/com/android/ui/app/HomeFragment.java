package com.android.ui.app;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.AppActivity;
import com.android.Global;
import com.android.LoginAndRegisterActivity;
import com.android.R;
import com.android.api.GetAnimalsRequest;
import com.android.api.GetPopularPostsRequest;
import com.android.api.GetPostsRequest;
import com.android.api.LikePostRequest;
import com.android.ui.app.interfaces.PopularPostsRecyclerViewInterface;
import com.android.PostModel;
import com.android.databinding.FragmentHomeBinding;
import com.android.ui.app.adapters.PopularPostsAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class HomeFragment extends Fragment implements PopularPostsRecyclerViewInterface {

    private FragmentHomeBinding binding;
    private String cityName = null;

    PopularPostsAdapter popularPostsAdapter;
    //    This list will be everything stored in the recycler view
    private List<String> list;

    ArrayList<PostModel> postModelArrayList = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppActivity app = (AppActivity) getActivity();
//                Intent loginIntent = new Intent(app, LoginAndRegisterActivity.class);
//                startActivity(loginIntent);
////                NavHostFragment.findNavController(HomeFragment.this)
////                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });

        FutureTask<GetPopularPostsRequest.GetPopularPostsRequestResult> getPosts = new FutureTask<>(new GetPopularPostsRequest(10));
        Global.executorService.submit(getPosts);
        Global.executorService.execute(() -> {
            try {
                // Get posts from backend to populate the recycler view.
                GetPopularPostsRequest.GetPopularPostsRequestResult result = getPosts.get();

                if (result.requestSucceeded) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                    getActivity().runOnUiThread(() -> {
                        try {
                            binding.recyclerViewPopularPosts.setLayoutManager(layoutManager);
                            binding.recyclerViewPopularPosts.setAdapter(new PopularPostsAdapter(result.posts, this));
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

            TypedArray animalResources = getResources().obtainTypedArray(R.array.animals);
            FutureTask<GetAnimalsRequest.GetAnimalRequestResponse> animals = new FutureTask<>(new GetAnimalsRequest(Global.loggedInUserID));
            Global.executorService.submit(animals);
            GetAnimalsRequest.GetAnimalRequestResponse response = null;
            try {
                response = animals.get();
                int receivedLength = response.animals.length();
                int actualLength = animalResources.length();
                getActivity().runOnUiThread(() -> {
                    binding.progressBarAchievements.setProgress(receivedLength);
                    binding.achievementsText.setText(receivedLength + " animals out of " + actualLength + " collected");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Like Error", Toast.LENGTH_SHORT).show());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}