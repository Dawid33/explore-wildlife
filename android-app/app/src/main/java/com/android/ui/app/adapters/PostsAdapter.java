package com.android.ui.app.adapters;

import static com.android.ui.app.adapters.PopularPostsAdapter.imageApiUrl;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.Global;
import com.android.PostModel;
import com.android.R;
import com.android.api.AccountRetrievalRequest;
import com.android.ui.app.interfaces.PostsRecyclerViewInterface;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    JSONArray postData;

    private ArrayList<PostModel> postModelList = new ArrayList<>();
    private PostsRecyclerViewInterface postsListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postUsername;
        public TextView postTime;
        public TextView postLikes;
        public ImageView postAvatarImage;
        public ImageView postImage;

        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            postUsername = view.findViewById(R.id.postUsername);
            postImage = view.findViewById(R.id.postImage);
            postAvatarImage = view.findViewById(R.id.postAvatarImage);
            postTime = view.findViewById(R.id.postTime);
            postLikes = view.findViewById(R.id.postLikes);
        }
    }

    public PostsAdapter(JSONArray postData) {
        this.postData = postData;
    }

    public PostsAdapter(JSONArray postData, PostsRecyclerViewInterface postsRecyclerViewInterface) throws JSONException {
        this.postData = postData;
        this.postsListener = postsRecyclerViewInterface;

        for(int i = 0; i < postData.length(); i++){
            String content = postData.getJSONObject(i).getString("content");
            String createdAt = postData.getJSONObject(i).getString("created_at");
            String createdBy = postData.getJSONObject(i).getString("created_by");
            String likes = postData.getJSONObject(i).getString("likes");
            String postID = postData.getJSONObject(i).getString("post_id");
            boolean hasLiked = postData.getJSONObject(i).getBoolean("has_liked");

// Calculate is liked

            postModelList.add(new PostModel(postID, createdBy, createdAt, Integer.parseInt(likes), hasLiked, content));
        }
    }

    /**
     * This is called whenever ViewHolder is created. This ViewHolder is the one extended from the RecyclerView ViewHolder
     *
     * @param viewGroup The ViewGroup into which the new View will be added after it is bound to
     *                  an adapter position.
     * @param viewType  The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_row_item, viewGroup, false);
        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder viewHolder, int position) {
        PostModel currentPost = postModelList.get(viewHolder.getAdapterPosition());

        int likes = Integer.parseInt(viewHolder.postLikes.getText().toString());

        if(currentPost.isLiked()){
            viewHolder.postLikes.getCompoundDrawables()[0].setTint(ContextCompat.getColor(viewHolder.postLikes.getContext(), R.color.aquamarine));
            viewHolder.postLikes.setTextColor(ContextCompat.getColor(viewHolder.postLikes.getContext(), R.color.aquamarine));
        }

        try {
            viewHolder.postTime.setText(postData.getJSONObject(position).getString("created_at"));
            viewHolder.postLikes.setText(postData.getJSONObject(position).getString("likes"));
            try {
                ExecutorService exec = Executors.newSingleThreadExecutor();
                JSONObject post = postData.getJSONObject(position);
                FutureTask<AccountRetrievalRequest.AccountRetrievalRequestResult> getUser = new FutureTask<>(new AccountRetrievalRequest((String) post.get("created_by")));
                exec.submit(getUser);
                AccountRetrievalRequest.AccountRetrievalRequestResult userResult = getUser.get();
                viewHolder.postUsername.setText(userResult.account.get("display_name").toString());
                JSONArray images = ((JSONObject) postData.get(position)).getJSONArray("images");

                // Load avatar from server
                Glide.with(viewHolder.view)
                        .load(imageApiUrl + userResult.account.get("profile_pic_id"))
                        .into(viewHolder.postAvatarImage);

                // Only get the first image for simplicity, there might be more in the array
                Glide.with(viewHolder.view)
                        .load(imageApiUrl + images.get(0))
                        .into(viewHolder.postImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.postLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostModel currentPost = postModelList.get(viewHolder.getAdapterPosition());

                int likes = Integer.parseInt(viewHolder.postLikes.getText().toString());


                String UID = Global.loggedInUserID;

                if(currentPost.isLiked()){
                    viewHolder.postLikes.getCompoundDrawables()[0].setTint(ContextCompat.getColor(viewHolder.postLikes.getContext(), R.color.myrtle_green));
                    viewHolder.postLikes.setTextColor(ContextCompat.getColor(viewHolder.postLikes.getContext(), R.color.myrtle_green));

                    viewHolder.postLikes.setText(Integer.toString(--likes));
                }
                else{
                    viewHolder.postLikes.getCompoundDrawables()[0].setTint(ContextCompat.getColor(viewHolder.postLikes.getContext(), R.color.aquamarine));
                    viewHolder.postLikes.setTextColor(ContextCompat.getColor(viewHolder.postLikes.getContext(), R.color.aquamarine));

                    viewHolder.postLikes.setText(Integer.toString(++likes));
                }


                postsListener.onLikeClicked(currentPost);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postData.length();
    }
}
