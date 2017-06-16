package com.example.user.locationmaptest;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import com.google.android.gms.maps.Projection;
import android.view.animation.Interpolator;
import android.graphics.Point;
import android.view.animation.LinearInterpolator;

import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng location;
    private float zoomLevel = 10.0F;
    private double latitude;
    private double longitude;
    private String markerId;
    Marker marker;

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

        getLatLngFromPreActivity(); //get the lat and lon from the previous activity

        //add marker
        location = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(markerId)
                .snippet("Lat,Lng="+location.latitude+","+location.longitude));
        markerId = marker.getId();
        Toast.makeText(getApplicationContext(), markerId, Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

        // タップした時のリスナーをセット
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickLocation) {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(clickLocation)
                        .title(markerId)
                        .snippet("Lat,Lng="+clickLocation.latitude+","+clickLocation.longitude));
                markerId = marker.getId();
                Toast.makeText(getApplicationContext(), markerId, Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clickLocation, zoomLevel));
            }
        });

        // 長押しのリスナーをセット
        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng longClickLocation) {
                animateMarker(marker, longClickLocation, false);
            }
        });

        // action when marker clicked
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker clickedMarker) {
                clickedMarker.remove();
                return true;
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

    private void getLatLngFromPreActivity() {
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("currentLat", 0);
        longitude = intent.getDoubleExtra("currentLon", 0);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
