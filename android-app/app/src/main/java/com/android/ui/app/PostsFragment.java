package com.android.ui.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.PostModel;
import com.android.PostsRecyclerViewInterface;
import com.android.databinding.FragmentPostsBinding;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment implements PostsRecyclerViewInterface {

//    ================= TEST CODE ================

    //    Creating the recycler view adapter
//    private RecyclerView.Adapter adapter;

    PostsAdapter postsAdapter;

    private static final String createPostApiUrl = "http://10.0.2.2:8080/api/posts";

    //    This list will be everything stored in the recycler view
    private List<String> list;

    ArrayList<PostModel> postModelArrayList = new ArrayList<>();

//    ================= TEST CODE ================


    private FragmentPostsBinding binding;

//    public static final int PICK_IMAGE = 1;

//    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri uri) {
//                    System.out.println(uri.toString());
//
////                Glide.with(context
////                        .load(url)
////                        .into(new CustomTarget<Drawable>() {
////                            @Override
////                            public void onResourceReady(Drawable resource, Transition<Drawable> transition) {
////                                // Do something with the Drawable here.
////                            }
////
////                            @Override
////                            public void onLoadCleared(@Nullable Drawable placeholder) {
////                                // Remove the Drawable provided in onResourceReady from any Views and ensure
////                                // no references to it remain.
////                            }
////                        });
//                    FutureTarget<Bitmap> futureTarget =
//                            Glide.with(getContext())
//                                    .asBitmap()
//                                    .load(uri)
//                                    .submit();
//
//                    try {
//                        ExecutorService exec = Executors.newSingleThreadExecutor();
//                        FutureTask<Bitmap> getImage = new FutureTask<>(futureTarget::get);
//                        exec.submit(getImage);
//                        Bitmap image = getImage.get();
//
//                        FutureTask<UploadImageRequest.UploadImageRequestResult> getPosts = new FutureTask<>(new UploadImageRequest(image, getActivity().getExternalFilesDir(null)));
//                        exec.submit(getPosts);
//                        UploadImageRequest.UploadImageRequestResult result = getPosts.get();
//
//                        if (result.requestSucceeded) {
//                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show());
//                        } else {
//                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error uploading image", Toast.LENGTH_SHORT).show());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

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


//        binding.recyclerView.setHasFixedSize(true);
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        binding.getLatestPosts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FutureTask<GetPostsRequestResult> getPosts = new FutureTask<>(new GetPostsRequest());
//                ExecutorService exec = Executors.newSingleThreadExecutor();
//                exec.submit(getPosts);
//                try {
//                    GetPostsRequestResult result = getPosts.get();
//                    if (result.requestSucceeded) {
//                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
////                        Adding adapter
//                        binding.recyclerView.setAdapter(new PostsAdapter(result.posts));
//                    } else {
//                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error getting latest posts", Toast.LENGTH_SHORT).show());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        ========== ATTEMPT TO GET RECYCLERVIEW WORKING ======================


        prepareTestPosts();

        postsAdapter = new PostsAdapter(this.getContext(), postModelArrayList, this);

        binding.recyclerView.setAdapter(postsAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));


//        ========== ATTEMPT TO GET RECYCLERVIEW WORKING ======================

//        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mGetContent.launch("image/*");
//                binding.createPost.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
////                        Adding adapter
//                        binding.recyclerView.setAdapter(new PostsAdapter("hi"));
//                    }
//                });
//            }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
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
    public void onItemClick(int position) {
//        DO SOMETHING AWESOME SUCH AS BRINGING USER TO A DETAILED POST VIEW
    }
}