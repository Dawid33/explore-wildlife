package com.android.ui.app;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.AppActivity;
import com.android.LoginAndRegisterActivity;
import com.android.PopularPostsRecyclerViewInterface;
import com.android.PostModel;
import com.android.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

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
        prepareTestPosts();

        popularPostsAdapter = new PopularPostsAdapter(this.getContext(), postModelArrayList, this);

        binding.recyclerViewPopularPosts.setAdapter(popularPostsAdapter);
        binding.recyclerViewPopularPosts.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
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
    public void onItemClick(int position) {

    }
}