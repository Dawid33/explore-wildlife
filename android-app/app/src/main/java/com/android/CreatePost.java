package com.android;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.databinding.FragmentBestiaryBinding;
import com.android.databinding.FragmentCreatePostBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePost extends Fragment {

//    Points to media?

    String[] projection = new String[] {

    };
    String selection = "";
    String[] selectionArgs = new String[] {

    };
    String sortOrder = "";
    private Cursor mediaStoreCursor = getContext().getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder

);


    private FusedLocationProviderClient fusedLocationClient;

    private String cityName = null;

    private FragmentCreatePostBinding binding;

    public CreatePost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreatePost.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePost newInstance(String param1, String param2) {
        CreatePost fragment = new CreatePost();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            cityName = addresses.get(0).getLocality();
                            binding.postLocationText.setText(cityName);
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

        setThumbnail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();


        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_post, container, false);
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

//    Swapping, cleaning up cursors
    private Cursor swapCursor(Cursor cursor){
        if(mediaStoreCursor == cursor){
            return null;
        }

        Cursor oldCursor = mediaStoreCursor;

        this.mediaStoreCursor = cursor;

        if(cursor != null){
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor){
        Cursor oldCursor = swapCursor(cursor);

        if(oldCursor != null){
            oldCursor.close();
        }
    }

    private Bitmap getBitmapFromMediaStore(int position){
        int idIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int mediaTypeIndex = mediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        mediaStoreCursor.moveToPosition(position);

        switch (mediaStoreCursor.getInt(mediaTypeIndex)){
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
//                return MediaStore.Images.Thumbnails.getThumbnail(
//                        getActivity().getContentResolver(),
//                        mediaStoreCursor.getLong(idIndex),
//                        MediaStore.Images.Thumbnails,
//                        null
//                );

            default:
                return null;
        }

    }

    private void setThumbnail(){
        try {

            Size thumbSize = new Size(300, 300);
            String UriString = CreatePostArgs.fromBundle(getArguments()).getPhotoPath();

//            int thumbColumn = mediaStoreCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
//            int _thumpId = mediaStoreCursor.getInt(thumbColumn);
//            Uri imageUri_t = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,_thumpId);
//            Uri imageUri_t = Uri.parse(UriString);


            Bitmap thumbBitmap = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                thumbBitmap = getContext().getContentResolver().loadThumbnail(imageUri_t, thumbSize, null);
            } else {
//                thumbBitmap = MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(),
//                        R.drawable.id_card, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }

//            binding.createPostImage.setImageBitmap(thumbBitmap);
//            Toast.makeText(getContext(), imageUri_t.toString(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(), UriString, Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(getContext(), "Nope. Didn't work", Toast.LENGTH_SHORT).show();

        }
    }
}