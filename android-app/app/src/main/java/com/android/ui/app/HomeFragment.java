package com.android.ui.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.AppActivity;
import com.android.LoginAndRegisterActivity;
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
        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppActivity app = (AppActivity) getActivity();
                Intent loginIntent = new Intent(app, LoginAndRegisterActivity.class);
                startActivity(loginIntent);
//                NavHostFragment.findNavController(HomeFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        FutureTask<GetPopularPostsRequest.GetPopularPostsRequestResult> getPosts = new FutureTask<>(new GetPopularPostsRequest(10));
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(getPosts);
        // TODO: This will probably cause problems in the future as it will make the app slow.
        // The data should be loaded in the background in a non-blocking way.
        try {
            // Get posts from backend to populate the recycler view.
            GetPopularPostsRequest.GetPopularPostsRequestResult result = getPosts.get();

            if (result.requestSucceeded) {
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);


                binding.recyclerViewPopularPosts.setLayoutManager(layoutManager);
//                binding.recyclerView.setAdapter(new PostsAdapter(result.posts));

                binding.recyclerViewPopularPosts.setAdapter(new PopularPostsAdapter(result.posts, this));

            } else {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error getting latest posts", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        prepareTestPosts();

//        popularPostsAdapter = new PopularPostsAdapter(this.getContext(), postModelArrayList, this);
//        try {
//            popularPostsAdapter = new PopularPostsAdapter(postModelArrayList, this);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        binding.recyclerViewPopularPosts.setAdapter(popularPostsAdapter);
//        binding.recyclerViewPopularPosts.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.progressBarAchievements.setProgress(0);

    }

    private void prepareTestPosts(){
        String testUsername = "testUser", testTime = "1d ago";
        int testLikes = 4200;

        int testPostAvatar, testImage;

        for (int i = 0; i < 10; i++) {
            postModelArrayList.add(new PostModel(testUsername, testTime, testLikes));
        }

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