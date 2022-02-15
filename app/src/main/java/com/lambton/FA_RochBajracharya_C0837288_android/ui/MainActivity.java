package com.lambton.FA_RochBajracharya_C0837288_android.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lambton.FA_RochBajracharya_C0837288_android.R;
import com.lambton.FA_RochBajracharya_C0837288_android.db.DatabaseClient;
import com.lambton.FA_RochBajracharya_C0837288_android.db.PlaceDao;
import com.lambton.FA_RochBajracharya_C0837288_android.model.Place;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    FloatingActionButton fabAdd;
    GoogleMap mMap = null;
    PlaceDao placeDao;

    MaterialButton btnSatelite;
    MaterialButton btnHybrid;
    MaterialButton btnTerrian;
    MaterialButton btnNone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placeDao = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().placeDao();

        initialize();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Map View");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    private void initialize() {
        fabAdd = findViewById(R.id.float_add);
        btnSatelite = findViewById(R.id.button_satelite);
        btnHybrid = findViewById(R.id.button_hybrid);
        btnTerrian = findViewById(R.id.button_terrain);
        btnNone = findViewById(R.id.button_none);

        fabAdd.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddNewPlaceActivity.class)));

        btnSatelite.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnHybrid.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        btnTerrian.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        btnNone.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
    }

    void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_menu:
                startActivity(new Intent(getApplicationContext(), PlaceListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        enableLocation();

        List<Place> places = placeDao.getAllPlaces();

        for (Place place : places) {
            LatLng loc = new LatLng(place.getLatitude(), place.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(place.getName()));
        }

        mMap.setOnMarkerClickListener(marker -> {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
            for (Place place : places) {
                if (place.getName().contentEquals(marker.getTitle())) {
                    makeBottomSheet(place);
                }
            }
            return false;
        });

        mMap.setOnMapLongClickListener(latLng -> {
            Toast.makeText(MainActivity.this, "Add Location to list", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AddNewPlaceActivity.class);
            intent.putExtra("long", latLng.longitude);
            intent.putExtra("lan", latLng.latitude);
            startActivity(intent);
        });


    }

    void makeBottomSheet(Place place) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_map_detail);

        Place place2 = placeDao.getProductById(place.getId());

        TextView tvTitle = bottomSheetDialog.findViewById(R.id.text_title);
        CheckBox cvCompleted = bottomSheetDialog.findViewById(R.id.cv_isCompleted);
        CheckBox cvFav = bottomSheetDialog.findViewById(R.id.cv_isFav);
        MaterialButton btnEdit = bottomSheetDialog.findViewById(R.id.button_edit);

        tvTitle.setText(place2.getName());

        if (place2.getFav()) {
            cvFav.setChecked(true);
        } else {
            cvFav.setChecked(false);
        }

        if (place2.getVisit()) {
            cvCompleted.setChecked(true);
        } else {
            cvCompleted.setChecked(false);
        }

        cvCompleted.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Place place1 = placeDao.getProductById(place.getId());
                place1.setVisit(true);

                placeDao.update(place1);
            } else {
                Place place1 = placeDao.getProductById(place.getId());
                place1.setVisit(false);

                placeDao.update(place1);
            }
        });

        cvFav.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Place place1 = placeDao.getProductById(place.getId());
                place1.setFav(true);

                placeDao.update(place1);
            } else {
                Place place1 = placeDao.getProductById(place.getId());
                place1.setFav(false);

                placeDao.update(place1);
            }
        });

        btnEdit.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), EditPlaceActivity.class);
            i.putExtra("id", place2.getId());
            bottomSheetDialog.dismiss();
            startActivity(i);
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    private FusedLocationProviderClient fusedLocationClient;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void enableLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            //TODO
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        enableLocation();
    }

    LocationRequest mLocationRequest;

    /*
     * Purpose - find the user location from gps or sim
     *
     * @author - RB
     */
    private double latitude, longitude;

    protected void startLocationUpdates() {

        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(2000);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        final LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (fusedLocationClient != null) {
                        if (location != null) {
                            mMap.setOnMapLoadedCallback(() -> {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                fusedLocationClient.removeLocationUpdates(mLocationCallback);
                            });

                        }
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());

    }

}