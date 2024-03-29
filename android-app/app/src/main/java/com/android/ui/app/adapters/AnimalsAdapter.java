package com.android.ui.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.AnimalModel;
import com.android.ui.app.interfaces.AnimalsRecyclerViewInterface;
import com.android.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.ViewHolder> {
    JSONArray data;

    //    ======================== TEST VARIABLES =============================
    String test1;
    List<String> texts;

    Context context;
    private AnimalsRecyclerViewInterface animalsRecyclerViewInterface;
    ArrayList<AnimalModel> animalModelArrayList;

//    ======================== TEST VARIABLES =============================


    /**
     * Define all the XML elements in this class for them to be referenced later. Using the view object, you can get the different sub elements
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

//      ============ TEST VARIABLES ===============

        TextView animalName, timesEncountered;
        ImageView animalImage;
        CardView timesEncounteredContainer;



        //        Attaching stuff from the layout file to this ViewHolder class
        public ViewHolder(@NonNull View itemView, AnimalsRecyclerViewInterface animalsRecyclerViewInterface) {
            super(itemView);

            animalName = itemView.findViewById(R.id.animalName);
            timesEncountered = itemView.findViewById(R.id.timesEncountered);
            timesEncounteredContainer = itemView.findViewById(R.id.timesEncounteredContainer);

            animalImage = itemView.findViewById(R.id.animalImage);


//            Setting onclick for each view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkInterfaceAndPosition(itemView, animalsRecyclerViewInterface)){
                        animalsRecyclerViewInterface.onItemClick(getAdapterPosition());
                    }
//                    SUPER AWESOME ONCLICK CODE GOES HERE
                }
            });

        }

        private boolean checkInterfaceAndPosition(@NonNull View itemView, AnimalsRecyclerViewInterface animalsRecyclerViewInterface){
            if(animalsRecyclerViewInterface != null){
                int pos = getAdapterPosition();

                return pos != RecyclerView.NO_POSITION;
            }
            return false;
        }

    }

    public AnimalsAdapter(JSONArray data) {
        this.data = data;
    }

    public AnimalsAdapter(String test1) {
        this.test1 = test1;
    }

    public AnimalsAdapter(Context context, ArrayList<AnimalModel> animalModelArrayList, AnimalsRecyclerViewInterface animalsRecyclerViewInterface) {
        this.context = context;
        this.animalModelArrayList = animalModelArrayList;
        System.out.println("SIZE = " + animalModelArrayList.size());
        this.animalsRecyclerViewInterface = animalsRecyclerViewInterface;
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
                .inflate(R.layout.bestiary_row_item, viewGroup, false);
        return new AnimalsAdapter.ViewHolder(view, animalsRecyclerViewInterface);
    }


    @Override
    public void onBindViewHolder(@NonNull AnimalsAdapter.ViewHolder viewHolder, int position) {
        viewHolder.animalName.setText(animalModelArrayList.get(position).getName());
        if (animalModelArrayList.get(position).getWitnessedInstances() > 0) {
            viewHolder.animalImage.setColorFilter(ContextCompat.getColor(context, R.color.aquamarine));
        } else {
            viewHolder.timesEncountered.setVisibility(View.GONE);
            viewHolder.timesEncounteredContainer.setVisibility(View.GONE);
        }
        viewHolder.timesEncountered.setText(Integer.toString(animalModelArrayList.get(position).getWitnessedInstances()));
        viewHolder.animalImage.setImageDrawable(animalModelArrayList.get(position).getDrawableImage());

    }

    @Override
    public int getItemCount() {
        return animalModelArrayList.size();
    }


}
