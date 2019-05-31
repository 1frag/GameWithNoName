package com.example.gamewithnoname.maps;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.UserLocation;

import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

public class MapMainMenu extends Fragment {

    private MapView mapView;
    private Map mMap;
    private Point mFinishMarker;
    private final String TAG = String.format("%s/%s",
            "HITS", "MapMainMenu");
    private InputListener mInputListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        Log.i(TAG, "run onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "run onCreateView");
        View rootView = inflater.inflate(R.layout.map_holder, container, false);
//        mapView = rootView.findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        // yandex:
        MapKitFactory.initialize(rootView.getContext());
        // Укажите имя activity вместо map.

        mapView = rootView.findViewById(R.id.mapViewMain);
        Location now = UserLocation.imHere;
        mapView.getMap().move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mMap = mapView.getMap();
        mInputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                Log.i(TAG, String.format("onMapTap at %s %s",
                        point.getLatitude(), point.getLongitude()));
                mFinishMarker = point;
                map.getMapObjects()
                        .clear();
                map.getMapObjects()
                        .addCircle(new Circle(point, 50),
                                Color.BLACK,
                                10,
                                Color.RED);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                Log.i(TAG, "onMapLongTap");
            }
        };
        mMap.addInputListener(mInputListener);
        // end.

        return rootView;
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

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        Log.i(TAG, "onMapReady");
//
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "fuck perm");
//            // todo: это ситуация когда мы хотим показать
//            //  местоположение, но прав на это нет, при этом
//            //  сейчас ничего не происходит, просто показываем
//            //  карту без синего кружочка
//            return;
//        }
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.setBuildingsEnabled(true);
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        Location location = UserLocation.imHere;
//        double a = location.getLatitude();
//        double b = location.getLongitude();
//
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mMap.setMyLocationEnabled(true);
//
//        LatLng place = new LatLng(a, b);
////        mMap.addMarker(new MarkerOptions().position(place).title("Current location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
//        mMap.animateCamera(CameraUpdateFactory
//                .newLatLngZoom(place, 12.0f));
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                mMap.clear();
//                mFinishMarker = new MarkerOptions()
//                        .position(latLng)
//                        .title("Finish");
//                mMap.addMarker(mFinishMarker);
//            }
//        });
//    }

    public Point getFinishMarker() {
        if (mFinishMarker == null) {
            return null;
        }
        return mFinishMarker;
    }

//    @Override
//    public void onResume() {
//        mapView.onResume();
//        super.onResume();
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }

}
