package com.android.ui.app;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String imageApiUrl = "https://explorewildlife.net/api/image?id=";
    JSONArray data;

    //    ======================== TEST VARIABLES =============================
    String test1;
    List<String> texts;

//    ======================== TEST VARIABLES =============================


    /**
     * Define all the XML elements in this class for them to be referenced later. Using the view object, you can get the different sub elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView testText;
        public ImageView imageView;
//        public View view;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
//            this.view = view;
//            textView = view.findViewById(R.id.textView);
            testText = view.findViewById(R.id.testText);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    public PostsAdapter(JSONArray data) {
        this.data = data;
    }

    public PostsAdapter(String test1) {
        this.test1 = test1;
    }

    public PostsAdapter(List<String> texts) {
        this.texts = texts;
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
        return new ViewHolder(view);
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
        viewHolder.testText.setText(test1);
    }

    @Override
    public int getItemCount() {
//        return data.length();
        return 0;
    }

}
