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

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String imageApiUrl = "https://explorewildlife.net/api/image?id=";
    JSONArray data;

//    ======================== TEST VARIABLES =============================
    String test1;

//    ======================== TEST VARIABLES =============================


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public View view;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            this.view = view;
            textView = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    public PostsAdapter(JSONArray data) {
       this.data = data;
    }

    public PostsAdapter(String test1){
        this.test1 = test1;
    }

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

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder viewHolder, int position) {
        try {
            viewHolder.textView.setText(test1);
        } catch (Exception ignored) {}
    }

    @Override
    public int getItemCount() {
        return data.length();
    }
}
