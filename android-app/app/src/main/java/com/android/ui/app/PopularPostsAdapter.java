package com.android.ui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.PopularPostsRecyclerViewInterface;
import com.android.PostModel;
import com.android.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PopularPostsAdapter extends RecyclerView.Adapter<PopularPostsAdapter.ViewHolder> {
    public static final String imageApiUrl = "https://explorewildlife.net/api/image?id=";
    JSONArray data;

    //    ======================== TEST VARIABLES =============================
//    String test1;
//    List<String> texts;

    Context context;
    private PopularPostsRecyclerViewInterface popularPostsRecyclerViewInterface;
    ArrayList<PostModel> postModelArrayList;

//    ======================== TEST VARIABLES =============================


    /**
     * Define all the XML elements in this class for them to be referenced later. Using the view object, you can get the different sub elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView testText;
        public ImageView imageView;

//      ============ TEST VARIABLES ===============

        TextView postLikes;
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
        public ViewHolder(@NonNull View itemView, PopularPostsRecyclerViewInterface popularPostsRecyclerViewInterface) {
            super(itemView);

            postLikes = itemView.findViewById(R.id.postLikes);

            postImage = itemView.findViewById(R.id.postImage);


//            Setting onclick for each view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkInterfaceAndPosition(itemView, popularPostsRecyclerViewInterface)){
                        popularPostsRecyclerViewInterface.onItemClick(getAdapterPosition());
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
        private boolean checkInterfaceAndPosition(@NonNull View itemView, PopularPostsRecyclerViewInterface postsRecyclerViewInterface){
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

    public PopularPostsAdapter(JSONArray data) {
        this.data = data;
    }

    public PopularPostsAdapter(Context context, ArrayList<PostModel> postModelArrayList, PopularPostsRecyclerViewInterface popularPostsRecyclerViewInterface) {
        this.context = context;
        this.postModelArrayList = postModelArrayList;
        this.popularPostsRecyclerViewInterface = popularPostsRecyclerViewInterface;
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
                .inflate(R.layout.popular_post_row_item, viewGroup, false);
        return new PopularPostsAdapter.ViewHolder(view, popularPostsRecyclerViewInterface);
    }


    /**
     * Called after onCreateViewHolder is called. Binds data to the View object
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PopularPostsAdapter.ViewHolder viewHolder, int position) {
//        viewHolder.testText.setText(test1);
//
        viewHolder.postLikes.setText(Integer.toString(postModelArrayList.get(position).getPostLikes()));
        viewHolder.postImage.setImageResource(R.drawable.heart_draw);
    }

    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }


}
