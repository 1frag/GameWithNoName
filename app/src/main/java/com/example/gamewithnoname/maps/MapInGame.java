package com.example.gamewithnoname.maps;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.UserLocation;

import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

public class MapInGame extends AppCompatActivity {

    private MapView mapView;
    private final String TAG = String.format("%s/%s",
            "HITS", "MapInGame");

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "run onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // config for my favorite company and their api:
        MapKitFactory.initialize(this);
        mapView = findViewById(R.id.mapViewInGame);
        configMap(mapView.getMap());
        // end.

    }

    private void configMap(Map map) {
        Log.i(TAG, "configMap");
        Location now = UserLocation.imHere;
        map.move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        map.setNightModeEnabled(true);

        // draw finish:
        double a = getIntent().getExtras().getDouble("finish_latitude");
        double b = getIntent().getExtras().getDouble("finish_longitude");
        map.getMapObjects().addPlacemark(new Point(a, b));

        // draw start:
        a = getIntent().getExtras().getDouble("start_latitude");
        b = getIntent().getExtras().getDouble("start_longitude");
        map.getMapObjects().addPlacemark(new Point(a, b));

        // todo: visualization start and path to finish
        // todo: connect bot in this map

    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

}
