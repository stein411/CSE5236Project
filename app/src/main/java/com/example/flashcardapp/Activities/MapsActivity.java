package com.example.flashcardapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcardapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private Button backButton;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private List<Marker> markers;
    private String deckKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deckKey = getString(R.string.NameString);
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
                Toast.makeText(getApplicationContext(), "Cannot display location at this time", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            setMyLocation();
        } else {
            // Request permission to access location
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        addHeatMap();
        //addClickableMarkers();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                Intent intent = new Intent(getApplicationContext(), UneditableDeckActivity.class);
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

        double lat = 40;

        // Populate with dummy data
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat + (i/10000.0), -83 + (i/10000.0)));
            }
        }
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat - (i/10000.0), -83 + (i/10000.0)));
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat + (i/10000.0), -83 - (i/10000.0)));
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat - (i/10000.0), -83 - (i/10000.0)));
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat - (i/10000.0), -83));
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat + (i/10000.0), -83));
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat, -83 - (i/10000.0)));
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                list.add(new LatLng(lat, -83 + (i/10000.0)));
            }
        }


        mProvider = new HeatmapTileProvider.Builder().data(list).build();
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private void setMyLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void addClickableMarkers() {
        markers = new ArrayList<>();

        // Populate with dummy data
        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40 + (i/10000.0), -83 + (i/10000.0))).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }
        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40 - (i/10000.0), -83 + (i/10000.0))).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40 + (i/10000.0), -83 - (i/10000.0))).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40 - (i/10000.0), -83 - (i/10000.0))).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40 - (i/10000.0), -83)).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40 + (i/10000.0), -83)).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40, -83 - (i/10000.0))).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

        for (int i = 0; i < 20; i++) {
            MarkerOptions m = new MarkerOptions().position(new LatLng(40, -83 + (i/10000.0))).title("" + markers.size());
            Marker m1 = mMap.addMarker(m);
            markers.add(m1);
        }

    }
}
