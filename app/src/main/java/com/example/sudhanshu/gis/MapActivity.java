package com.example.sudhanshu.gis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.constants.ListAppsActivityContract;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String fine_location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String course_location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean permissionGranted = false;
    Button button;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ImageView gps;
    Double lat, longi;
    private EditText mSearchText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, StartActivity.class);
                intent.putExtra("Latitude",lat.toString() );
                intent.putExtra("Longitude", longi.toString());
                startActivity(intent);
            }
        });

        mSearchText = findViewById(R.id.input_serach);

        gps = findViewById(R.id.gps);

        getLocationPermissions();
        init();
    }

    private void init(){

        mSearchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                Toast.makeText(MapActivity.this, "Entered", Toast.LENGTH_LONG).show();
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    geoLocate();
                }
                return false;
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevideLocation();
            }
        });
    }

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try{
           list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.e("TAG", "geoLocate: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d("TAG", "Found a location: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f, address.getAddressLine(0));
        }
    }
    private void getDevideLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f, "My Location");
                        } else {
                            Toast.makeText(MapActivity.this, "Unable to get current loaction", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e("TAG", e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, final String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title).draggable(true);

        mMap.addMarker(options);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mMap.clear();
//                Toast.makeText(MapActivity.this, cameraPosition.target.toString(), Toast.LENGTH_LONG).show();
                MarkerOptions marker = new MarkerOptions().position(cameraPosition.target).title(title);
                mMap.addMarker(marker);

                 lat = cameraPosition.target.latitude;
                 longi = cameraPosition.target.longitude;


            }
        });

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

    }

    private void getLocationPermissions() {
        String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), fine_location) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), course_location) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, 1234);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionGranted = false;
        switch (requestCode) {
            case 1234:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    initMap();

                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(MapActivity.this, "Ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;

        if (permissionGranted) {
            getDevideLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
        }
    }
}
