package com.android.ui.app;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.R;
import com.android.api.CreatePostRequest;
import com.android.api.UploadImageRequest;
import com.android.databinding.FragmentCreatePostBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CreatePostFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private String coordinates = null;
    private String cityName = null;

    private String category = "SCENERY";
    private String species = "";
    private FragmentCreatePostBinding binding;

    private double longitude = 0, latitude = 0;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getContext());
                        try {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            // Gets the coordinates of the location and concatenates them into a string.
//                            coordinates = location.getLatitude() + ", " + location.getLongitude();
                            coordinates = latitude + ", " + longitude;


                            // Gets city closest to cooridantes.
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            cityName = addresses.get(0).getLocality();
                            binding.postLocationInput.setText(cityName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);

        setThumbnail();

        binding.createPostButton.setOnClickListener(view -> {
            String UriString = CreatePostFragmentArgs.fromBundle(getArguments()).getPhotoPath();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(UriString,bmOptions);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            FutureTask<UploadImageRequest.UploadImageRequestResult> uploadImage = new FutureTask<>(new UploadImageRequest(rotated));
            ExecutorService exec = Executors.newSingleThreadExecutor();
            exec.submit(uploadImage);

            try {
                UploadImageRequest.UploadImageRequestResult result = uploadImage.get();
                String image_id = (String) result.data.get("image_id");



                FutureTask<CreatePostRequest.CreatePostRequestResult> createPost = new FutureTask<>(new CreatePostRequest(binding.postTitleInput.getText().toString(), image_id, longitude, latitude, binding.postDescriptionInput.getText().toString(), category, species));
                exec.submit(createPost);
                CreatePostRequest.CreatePostRequestResult createPostResult = createPost.get();
                if (createPostResult.success) {
                    System.out.println("SUCCESS IN CREATING POST");
                } else {
                    System.out.println("FAILED IN CREATING POST");
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //        Setting up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        binding.spinnerCategory.setOnItemSelectedListener(this);

//        TESTING ANIMALS SPINNER

        ArrayList<String> animals = prepareTestAnimals();

        String[] animalNames = new String[animals.size()];

        animalNames = animals.toArray(animalNames);

        ArrayAdapter<String> adapterSpecies = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, animalNames);
        binding.spinnerSpecies.setAdapter(adapterSpecies);

        binding.spinnerSpecies.setOnItemSelectedListener(this);

        binding.spinnerSpecies.setVisibility(View.GONE);

        return binding.getRoot();
    }

    // Getting location permission making sure its fine and coarse. Printing a toast if not.
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Location permission granted fully.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            Toast.makeText(getContext(), "Please grant precise permissions before using the main app.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Please grant all location permissions before using the main app.", Toast.LENGTH_SHORT).show();
                        }

                        Boolean readStorageGranted = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);

                        if(readStorageGranted){
//                            Nice.
                        }
                        else {
                            Toast.makeText(getContext(), "App needs to view thumbnails", Toast.LENGTH_SHORT).show();

                        }
                    }
            );



    private void setThumbnail() {
        assert getArguments() != null;
        String UriString = CreatePostFragmentArgs.fromBundle(getArguments()).getPhotoPath();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        Bitmap bitmap = BitmapFactory.decodeFile(UriString,bmOptions);
        // Rotate the thumbnail to the correct orientation.
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        binding.createPostImage.setImageBitmap(rotated);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.spinner_category){
            category = parent.getItemAtPosition(position).toString().toUpperCase();
//        Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();

            if(category.equals("ANIMAL")){
                binding.spinnerSpecies.setVisibility(View.VISIBLE);
            }
            else{
                binding.spinnerSpecies.setVisibility(View.GONE);
            }
        }
        else{
            species = parent.getItemAtPosition(position).toString().toUpperCase();
            Toast.makeText(getContext(), species, Toast.LENGTH_SHORT).show();

        }


    }

    private ArrayList<String> prepareTestAnimals(){
//        Get entire animals array
        ArrayList<String> animalNamesList = new ArrayList<>();
        TypedArray animalResources = getResources().obtainTypedArray(R.array.animals);
        TypedArray currentAnimal;



        for(int i = 0; i < animalResources.length(); i++){
//            Find resourceID of one animal in the array
            int resourceId = animalResources.getResourceId(i, -1);
            if (resourceId < 0) {
                continue;
            }

            currentAnimal = getResources().obtainTypedArray(resourceId);
            animalNamesList.add(currentAnimal.getString(0));
//            animalModelArrayList.add(new AnimalModel(currentAnimal.getString(0), 0, currentAnimal.getDrawable(1)));
        }

        return animalNamesList;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}