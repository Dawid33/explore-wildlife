package com.android.ui.app;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.AnimalModel;
import com.android.Global;
import com.android.api.GetAnimalsRequest;
import com.android.ui.app.interfaces.AnimalsRecyclerViewInterface;
import com.android.R;
import com.android.databinding.FragmentBestiaryBinding;
import com.android.ui.app.adapters.AnimalsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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

        Global.executorService.execute(() -> {
            getActivity().runOnUiThread(() -> {
                try {
                    prepareTestAnimals();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                animalsAdapter = new AnimalsAdapter(this.getContext(), animalModelArrayList, this);
                int count = 0;
                for (int i = 0; i < animalModelArrayList.size(); i++ ) {
                    if (animalModelArrayList.get(i).getWitnessedInstances() > 0) {
                        count += 1;
                    }
                }
                binding.collected.setText(count + "/" + animalModelArrayList.size() + " Collected");
                binding.animalsRecyclerView.setAdapter(animalsAdapter);
            });
        });
        binding.collected.setText("Loading Animals...");
        binding.animalsRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 3));
    }

    private void prepareTestAnimals() throws ExecutionException, InterruptedException, JSONException {
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

        FutureTask<GetAnimalsRequest.GetAnimalRequestResponse> animals = new FutureTask<>(new GetAnimalsRequest(Global.loggedInUserID));
        Global.executorService.submit(animals);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(animals);
        GetAnimalsRequest.GetAnimalRequestResponse response = animals.get();

        for(int i = 0; i < response.animals.length(); i++) {
            JSONObject animal = response.animals.getJSONObject(i);
            for(int j = 0; j < animalResources.length(); j++){
                if (animalModelArrayList.get(j).getName().toLowerCase().equals(animal.getString("name").toLowerCase())) {
                    animalModelArrayList.get(j).setWitnessedInstances(animal.getInt("count"));
                }
            }
        }

    }

    @Override
    public void onItemClick(int position) {
//        DO SOMETHING AWESOME SUCH AS BRINGING USER TO A DETAILED ANIMAL VIEW
    }
    }