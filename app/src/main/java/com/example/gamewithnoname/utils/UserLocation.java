package com.example.gamewithnoname.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class UserLocation implements LocationListener {

    public static Location imHere; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
    public static boolean enable = false;
    private static final String TAG = String.format("%s/%s",
            "HITS", "UserLocation");
    private static LocationManager locationManager;
    public static FusedLocationProviderClient fusedLocationClient;

    public static void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
    {
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new UserLocation();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        enable = true;
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                2,
                locationListener);

        imHere = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(imHere == null) {
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    @Override
    public void onLocationChanged(final Location loc) {
        if (imHere == null) {
            imHere = loc;
        }
        if(fusedLocationClient != null){
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                imHere = location;
                            }
                        }
                    });
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}