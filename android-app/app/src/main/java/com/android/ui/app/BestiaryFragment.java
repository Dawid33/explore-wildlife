package com.android.ui.app;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.AnimalModel;
import com.android.AnimalsRecyclerViewInterface;
import com.android.R;
import com.android.databinding.FragmentBestiaryBinding;
import com.android.databinding.FragmentPostsBinding;

import java.util.ArrayList;
import java.util.List;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link BestiaryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class BestiaryFragment extends Fragment implements AnimalsRecyclerViewInterface {

    AnimalsAdapter animalsAdapter;

    private List<String> list;
    ArrayList<AnimalModel> animalModelArrayList = new ArrayList<>();

    private FragmentBestiaryBinding binding;

    int[] animalImages = {R.drawable.sheep};

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    public BestiaryFragment() {
//        // Required empty public constructor
//    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Bestiary.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BestiaryFragment newInstance(String param1, String param2) {
//        BestiaryFragment fragment = new BestiaryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bestiary, container, false);

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