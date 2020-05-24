package com.example.ingredientsmapv1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private boolean mLocationPermissionGranted;
    Object mLastKnownLocation;
    private double longitude;
    private double latitude;
    Geocoder geocoder;
    List<Address> addresses;
    private GoogleMap mMap;
    private ArrayList<String> ingredients;
    private HashMap<String, String> map = new HashMap<>();
    LatLng currentLocation;
    private Float distance = 0.000f;
    private int counter = 0;
    private TextView emissions;
    private TextView address;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        getLocationPermission();
        if (extras != null) {
            ingredients = (ArrayList<String>) getIntent().getSerializableExtra("EXTRA_RECIPE_INGREDIENTS");
            getCoordinates();
        }
        setContentView(R.layout.activity_maps);
        emissions = findViewById(R.id.CO2);
        address = findViewById(R.id.address);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Getting coordinates of every ingredient
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getCoordinates() {
        for (int i = 0; i < ingredients.size(); i++) {
            String ing = ingredients.get(i);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("locations/" + ing);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    map.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        if (map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String ing = entry.getKey();
                double lat = Double.parseDouble(entry.getValue().split(",")[0]);
                double lng = Double.parseDouble(entry.getValue().split(",")[1]);
                LatLng foodLocation = new LatLng(lat, lng);
                String address = getAddress(foodLocation);
                mMap.addMarker(new MarkerOptions().position(foodLocation).title(ing + ":  " + address));
                calculateDistance(foodLocation);
            }
        } else {
            Log.d("MAP EMPTY", "onMapReady: the hashmap was empty");
        }
        currentLocation = new LatLng(latitude, longitude);
        String currentAddress = getAddress(currentLocation);
        address.setText(currentAddress);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Sei qui"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(14.0f);
    }

    // This method calculates the distances (in meters) between every supplier and our current position
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calculateDistance(LatLng foodLocation) {
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, foodLocation.latitude, foodLocation.longitude, results);
        for (Float f : results) {
            distance = Float.sum(distance, f);
        }
        if (counter == 1) {
            calculateEmissions(distance);
            counter = 0;
        } else {
            counter++;
        }
    }

    // Based on the distance in kilometers (converted from previous method) calculates the CO2 impact
    private void calculateEmissions(Float distance) {
        // Considering 147 g CO2/km which is the goal set by EU for 2020 for commercial vehicles
        float inKM = distance / 1000.00f;
        float grCO2 = inKM * 147.00f;
        emissions.setText((grCO2 * 2) + " grC02/roundtrip");
    }

    /*
     * Request location permission, so that we can get the location of the
     * device.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Updates the map
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // This method allows us to get the address based on the position (expressed in coordinates)
    public String getAddress(LatLng LatLng) {
        geocoder = new Geocoder(this, Locale.getDefault());
        String fullAddress = " ";
        try {
            addresses = geocoder.getFromLocation(LatLng.latitude, LatLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            fullAddress = address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullAddress;
    }
}
