package com.example.gamewithnoname.fragments_maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.gamewithnoname.BotLocation;
import com.example.gamewithnoname.R;
import com.example.gamewithnoname.UserLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapInGame extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = String.format("%s/%s",
            "HITS", "MapInGame");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(TAG, "run onCreate");
        UserLocation.SetUpLocationListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapInGame);
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

        LatLng finish = new LatLng(
                getIntent().getExtras().getDouble("latitude"),
                getIntent().getExtras().getDouble("longitude")
        );

        MarkerOptions markerOptions = new MarkerOptions()
                .position(finish)
                .title("Finish");
        mMap.addMarker(markerOptions);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied -> nothing to show");
            return;
        }
        Location location = UserLocation.imHere;
        double a = location.getLatitude();
        double b = location.getLongitude();

        mMap.setMyLocationEnabled(true); // it's blue point

        LatLng place = new LatLng(a, b);

        // it's red marker
//        mMap.addMarker(new MarkerOptions().position(place).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.animateCamera(CameraUpdateFactory
                .newLatLngZoom(place, 12.0f));

        BotLocation botLocation = new BotLocation(this, mMap, finish);
        botLocation.start(8000, 300);

    }

}
