package com.example.flashcardapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.flashcardapp.Activities.UneditableDeckActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private List<Marker> markers;
    private String deckKey;
    private CollectionReference deckksWithNonnullLocations;
    private ArrayList<String> decks;
    private ArrayList<Map> locations;
    private ArrayList<String> owners;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        deckKey = getString(R.string.NameString);
        decks = new ArrayList<>();
        locations = new ArrayList<>();
        owners = new ArrayList<>();
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMyLocation();
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(getContext(), "Cannot display location at this time", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setMyLocation();
        } else {
            // Request permission to access location
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        // TODO pull all decks from online that have non-null location values
        deckksWithNonnullLocations = FirebaseFirestore.getInstance().collection("decks");
        deckksWithNonnullLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot != null && documentSnapshot.get("name") != null && documentSnapshot.get("location") != null) {
                            String owner = "owner";
                            if (documentSnapshot.get("owner") != null) {
                                owner = documentSnapshot.get("owner").toString();
                            }
                            decks.add(documentSnapshot.get("name").toString());
                            locations.add((Map) documentSnapshot.get("location"));
                            owners.add(owner);
                        }
                    }
                }
                addHeatMap();
            }
        });
        //addClickableMarkers();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                Intent intent = new Intent(getContext(), UneditableDeckActivity.class);
                intent.putExtra(deckKey, title);
                startActivity(intent);
            }
        });
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                return false;
//            }
//        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            // TODO figure out camera zoom to switch from heatmap to markers
            @Override
            public void onCameraMoveStarted(int i) {
                if (mMap.getCameraPosition().zoom > 15) {
                    // Change to clickable markers
                    if (markers.size() == 0) {
                        mOverlay.clearTileCache();
                        addClickableMarkers();
                    }
                } else {
                    addHeatMap();
                    if (markers != null) {
                        for (Marker m : markers) {
                            m.remove();
                        }
                    }
                    markers = new ArrayList<>();
                }
            }
        });
    }

    private void addHeatMap() {
        List<LatLng> list = new ArrayList<>();

        //double lat = 40;


        for (int i = 0; i < locations.size(); i++) {
            Map<String, Object> loc = locations.get(i);
            double latitude = (Double) loc.get("latitude");
            double longitude = (Double) loc.get("longitude");
            list.add(new LatLng(latitude, longitude));
        }

        if (list.size() > 0) {
            mProvider = new HeatmapTileProvider.Builder().data(list).build();
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }

    private void setMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void addClickableMarkers() {
        markers = new ArrayList<>();
        for (int i = 0; i < decks.size(); i++) {
            Map<String, Object> loc = locations.get(i);
            String name = decks.get(i);
            String owner = owners.get(i);
            double latitude = (Double) loc.get("latitude");
            double longitude = (Double) loc.get("longitude");
            MarkerOptions m = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(name);
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }
    }
}
