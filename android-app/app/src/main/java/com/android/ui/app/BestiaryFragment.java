package com.android.ui.app;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.AnimalModel;
import com.android.ui.app.interfaces.AnimalsRecyclerViewInterface;
import com.android.R;
import com.android.databinding.FragmentBestiaryBinding;
import com.android.ui.app.adapters.AnimalsAdapter;

import java.util.ArrayList;
import java.util.List;

public class BestiaryFragment extends Fragment implements AnimalsRecyclerViewInterface {

    AnimalsAdapter animalsAdapter;

    private List<String> list;
    ArrayList<AnimalModel> animalModelArrayList = new ArrayList<>();

    private FragmentBestiaryBinding binding;

    int[] animalImages = {R.drawable.sheep};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBestiaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareTestAnimals();

        animalsAdapter = new AnimalsAdapter(this.getContext(), animalModelArrayList, this);

        binding.animalsRecyclerView.setAdapter(animalsAdapter);
        binding.animalsRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 3));
    }

    private void prepareTestAnimals(){
//        Get entire animals array
        TypedArray animalResources = getResources().obtainTypedArray(R.array.animals);
        TypedArray currentAnimal;



        for(int i = 0; i < animalResources.length(); i++){
//            Find resourceID of one animal in the array
            int resourceId = animalResources.getResourceId(i, -1);
            if (resourceId < 0) {
                continue;
            }

            currentAnimal = getResources().obtainTypedArray(resourceId);
            animalModelArrayList.add(new AnimalModel(currentAnimal.getString(0), 0, currentAnimal.getDrawable(1)));
        }
    }

    @Override
    public void onItemClick(int position) {
//        DO SOMETHING AWESOME SUCH AS BRINGING USER TO A DETAILED ANIMAL VIEW
    }

    }