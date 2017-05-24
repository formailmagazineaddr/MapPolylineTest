package com.example.user.fusedlocationtest;

//package com.example.user.maptest;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng iamhere;
    private float zoomLevel = 10.0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //get the lat and lon from the previous activity
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("currentLat", 0);
        double longitude = intent.getDoubleExtra("currentLon", 0);

        //add marker
        iamhere = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(iamhere).title("I am here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iamhere, zoomLevel));

        // タップした時のリスナーをセット
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng tapLocation) {
                // tapされた位置の緯度経度
                iamhere = new LatLng(tapLocation.latitude, tapLocation.longitude);
                mMap.addMarker(new MarkerOptions().position(iamhere).title(""+tapLocation.latitude+" :"+ tapLocation.longitude));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iamhere, zoomLevel));

            }
        });

        // 長押しのリスナーをセット
        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng longpushLocation) {
                LatLng newlocation = new LatLng(longpushLocation.latitude, longpushLocation.longitude);
                mMap.addMarker(new MarkerOptions().position(newlocation).title(""+longpushLocation.latitude+" :"+ longpushLocation.longitude));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newlocation, zoomLevel));
            }
        });
    }

    //-------------------------------------
    // called when pushed 'delegate' button
    //-------------------------------------
    public void delegateButtonHandler(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
