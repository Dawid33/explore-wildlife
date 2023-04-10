package com.android.ui.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.android.AppActivity;
import com.android.LoginAndRegisterActivity;
import com.android.api.GetNearestPostsRequest;
import com.android.api.GetPostsRequest;
import com.android.databinding.FragmentHomeBinding;
import com.android.R;
import com.android.databinding.FragmentMapsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MapsFragment extends Fragment {

    //arbitrary values
    static final int MAX_POSTS = 30;
    static final int MAX_RANGE = 10;
    static final float SEARCH_FAILED_ALPHA = 0.1f;
    static final float SEARCH_SUCCESS_ALPHA = 1f;

    private GoogleMap map;
    private SearchView searchView;
    private BitmapDescriptor defaultIcon;
    private List<Marker> markers;

    private FragmentMapsBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = (SearchView) getView().findViewById(R.id.idSearchView);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                applySearchFilter(searchView.getQuery().toString());
                return false;
            }
        });
        defaultIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon);
        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            populateMap();
        });

    }

    public void populateMap() {
        if (markers == null) {
            markers = new ArrayList<Marker>();
        }
        LatLng centre = map.getCameraPosition().target;
        //TODO:
        //request to server, returns with JSON data about each marker and its info
        //data is to be processed, stored in an array of objects with relevant data
        //json file and processing based on existing post processing
        /*
        markerData[] markers;
        foreach (markerData marker in markers) {
            placeMarker(marker.name, marker.info, marker.latitude, marker.longitude, );
        }
        */

        FutureTask<GetNearestPostsRequest.GetNearestPostsRequestResult> getPosts = new FutureTask<>(new GetNearestPostsRequest(centre.latitude, centre.longitude));
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(getPosts);
        try {
            JSONArray posts = getPosts.get().posts;
            for(int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                placeMarker(post.getString("title"), post.getString("description"), post.getDouble("latitude"), post.getDouble("longitude"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void placeMarker(String name, String info, double latitude, double longitude) {
        placeMarker(name, info, latitude, longitude, null);
    }

    public void placeMarker(String name, String info, double latitude, double longitude, BitmapDescriptor icon) {
        markers.add(map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(name)
                .snippet(info)
                .icon(icon == null ? defaultIcon : icon)
        ));
    }

    public void applySearchFilter(String filter) {
        //lowercase + removes leading and trailing whitespace
        filter = filter.toLowerCase(Locale.ROOT).trim();
        if (markers.size() > 0) {
            for (int i = 0; i < markers.size(); i++) {
                //if not contained in a marker's title or body
                if (!markers.get(i).getTitle().toLowerCase().contains(filter)
                        && !markers.get(i).getSnippet().toLowerCase().contains(filter)) {
                    markers.get(i).setAlpha(SEARCH_FAILED_ALPHA);
                }
                else {
                    markers.get(i).setAlpha(SEARCH_SUCCESS_ALPHA);
                }
            }
        }
    }

    public void refreshMap(View view) {
        if (markers.size() > 0) {
            for (int i = markers.size() - 1; i >= 0; i--) {
                markers.get(i).remove();
                markers.remove(i);
            }
        }
        populateMap();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}