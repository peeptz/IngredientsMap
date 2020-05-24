package com.example.ingredientsmapv1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    boolean mLocationPermissionGranted;
    Object mLastKnownLocation;
    double longitude;
    double latitude;
    Geocoder geocoder;
    List<Address> addresses;
    private GoogleMap mMap;
    private String name;
    final ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> ingredients;
    HashMap<String, String> map = new HashMap<>();
    LatLng currentLocation;
    Float distance = 0.000f;
    int counter = 0;
    TextView emissions;
    TextView address;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ingredients = (ArrayList<String>) getIntent().getSerializableExtra("EXTRA_RECIPE_INGREDIENTS");
            String name = extras.getString("EXTRA_RECIPE_DETAIL_NAME");
            Log.d("NAME", "onCreate of MapsActivity: " + name);
            Log.d("INGREDIENTS", "onCreate of MapsActivity: " + ingredients);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getCoordinates() {
        Log.d("MAP SIZE", "getCoordinates: " + map.size());
        Log.d("INGRIDIENTS SIZE", "getCoordinates: " + ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            String ing = ingredients.get(i);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("locations/" + ing);
//            Query query = reference.orderByChild(name);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    map.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        ;
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
        Log.d("MAP", "onMapReady: MAP" + map);
        if (map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String ing = entry.getKey();
                double lat = Double.parseDouble(entry.getValue().split(",")[0]);
                double lng = Double.parseDouble(entry.getValue().split(",")[1]);
                Log.d("POSITION", "onMapReady: POSITION FOR " + ing + " equals lat " + lat + " lng " + lng);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calculateDistance(LatLng foodLocation) {
        Log.d("COUNTER", "calculateEmissions: " + counter);
//        Log.d("LAT&LONG", "calculateEmissions: " + latitude + " " + longitude + " " + foodLocation.latitude + " " + foodLocation.longitude);
//        Log.d("LAT&LONG", "calculateEmissions: " + foodLocation);
//        Log.d("LAT&LONG", "calculateEmissions: " + currentLocation);
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, foodLocation.latitude, foodLocation.longitude, results);
        for (Float f : results) {
            distance = Float.sum(distance, f);
        }
        if (counter == 1) {
            Log.d("TOTAL METERS", "calculateEmissions: " + distance);
            calculateEmissions(distance);
            counter = 0;
        } else {
            counter++;
        }
    }

    private void calculateEmissions(Float distance) {
        // Considering 147 g CO2/km which is the goal set by EU for 2020 for commercial vehicles
        float inKM = distance / 1000.00f;
        float grCO2 = inKM * 147.00f;
        Log.d("grCO2/km", "calculateEmissions: " + grCO2);
        emissions.setText((grCO2 * 2) + " grC02/roundtrip");
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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

    public String getAddress(LatLng LatLng) {
        geocoder = new Geocoder(this, Locale.getDefault());
        String fullAddress = " ";
        try {
            addresses = geocoder.getFromLocation(LatLng.latitude, LatLng.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
//            String area = addresses.get(0).getLocality();
//            String city = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
            fullAddress = address;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullAddress;
    }
}
