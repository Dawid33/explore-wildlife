package com.android.ui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.PostModel;
import com.android.PostsRecyclerViewInterface;
import com.android.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String imageApiUrl = "https://explorewildlife.net/api/image?id=";
    JSONArray data;

    //    ======================== TEST VARIABLES =============================
    String test1;
    List<String> texts;

    Context context;
    private PostsRecyclerViewInterface postsRecyclerViewInterface;
    ArrayList<PostModel> postModelArrayList;

//    ======================== TEST VARIABLES =============================


    /**
     * Define all the XML elements in this class for them to be referenced later. Using the view object, you can get the different sub elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView testText;
        public ImageView imageView;

//      ============ TEST VARIABLES ===============

        TextView postUsername, postTime, postLikes, postComments;
        ImageView postAvatarImage;
        ImageView postImage;


//      ============ TEST VARIABLES ===============


//        public View view;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
//            this.view = view;
//            textView = view.findViewById(R.id.textView);
            testText = view.findViewById(R.id.postUsername);
            imageView = view.findViewById(R.id.postAvatarImage);
        }

//        Attaching stuff from the layout file to this ViewHolder class
        public ViewHolder(@NonNull View itemView, PostsRecyclerViewInterface postsRecyclerViewInterface) {
            super(itemView);

            postUsername = itemView.findViewById(R.id.postUsername);
            postTime = itemView.findViewById(R.id.postTime);
            postLikes = itemView.findViewById(R.id.postLikes);
            postComments = itemView.findViewById(R.id.postComments);

            postAvatarImage = itemView.findViewById(R.id.postAvatarImage);
            postImage = itemView.findViewById(R.id.postImage);


//            Setting onclick for each view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkInterfaceAndPosition(itemView, postsRecyclerViewInterface)){
                        postsRecyclerViewInterface.onItemClick(getAdapterPosition());
                    }
//                    SUPER AWESOME ONCLICK CODE GOES HERE
                }
            });

        }

        /**
         * Before we attempt a click, check if the interface exists, as well as if the adapter position is fine.
         * @param itemView
         * @param postsRecyclerViewInterface
         * @return
         */
        private boolean checkInterfaceAndPosition(@NonNull View itemView, PostsRecyclerViewInterface postsRecyclerViewInterface){
            if(postsRecyclerViewInterface != null){
                int pos = getAdapterPosition();

                return pos != RecyclerView.NO_POSITION;
            }
            return false;
        }

//        private boolean checkInterfaceAndPosition(@NonNull View itemView, PostsRecyclerViewInterface postsRecyclerViewInterface){
//            if(postsRecyclerViewInterface != null){
//                int pos = getAdapterPosition();
//
//                if(pos != RecyclerView.NO_POSITION){
//                    postsRecyclerViewInterface.onItemClick(pos);
//                }
//            }
//        }
    }

    public PostsAdapter(JSONArray data) {
        this.data = data;
    }

    public PostsAdapter(String test1) {
        this.test1 = test1;
    }

    public PostsAdapter(Context context, ArrayList<PostModel> postModelArrayList, PostsRecyclerViewInterface postsRecyclerViewInterface) {
        this.context = context;
        this.postModelArrayList = postModelArrayList;
        this.postsRecyclerViewInterface = postsRecyclerViewInterface;
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
        return new PostsAdapter.ViewHolder(view, postsRecyclerViewInterface);
    }

//    @Override
//    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder viewHolder, int position) {
//        try {
//            viewHolder.textView.setText(data.get(position).toString());
//            try {
//                JSONArray images = ((JSONObject) data.get(position)).getJSONArray("images");
//                // Only get the first image for simplicity, there might be more in the array
//                Glide.with(viewHolder.view)
//                        .load(imageApiUrl + images.get(0))
//                        .into(viewHolder.imageView);
//            } catch (Exception ignored) {}
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Called after onCreateViewHolder is called. Binds data to the View object
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder viewHolder, int position) {
//        viewHolder.testText.setText(test1);
//
        viewHolder.postUsername.setText(postModelArrayList.get(position).getUsername());
        viewHolder.postTime.setText(postModelArrayList.get(position).getPostTime());
        viewHolder.postLikes.setText(Integer.toString(postModelArrayList.get(position).getPostLikes()));
        viewHolder.postComments.setText(Integer.toString(postModelArrayList.get(position).getPostComments()));
    }

    @Override
    public int getItemCount() {
//        return data.length();
//        return 0;
        return postModelArrayList.size();
    }


}
