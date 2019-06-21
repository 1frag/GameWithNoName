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
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.utils.UserLocation;

import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;

import java.util.ArrayList;
import java.util.List;

public class MapMainMenu extends Fragment {

    private MapView mapView;
    private Map mMap;
    private PedestrianRouter pdRouter;
    private Session.RouteListener callback;
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
        MapKitFactory.initialize(rootView.getContext());

        mapView = rootView.findViewById(R.id.mapViewMain);

        if (UserLocation.enable) {
            Location now = UserLocation.imHere;
            mapView.getMap().move(
                    new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 11.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 0),
                    null);
        }

        mMap = mapView.getMap();
        mInputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point finish) {
                Log.i(TAG, String.format("onMapTap at %s %s",
                        finish.getLatitude(), finish.getLongitude()));
                mFinishMarker = finish;
                map.getMapObjects()
                        .clear();
                map.getMapObjects()
                        .addCircle(new Circle(finish, 50),
                                Color.BLACK,
                                10,
                                Color.RED)
                        .setZIndex(-1);

                Point now = new Point(
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude()
                );
                Point start = new Point(now.getLatitude(), now.getLongitude());

                pdRouter = TransportFactory.getInstance().createPedestrianRouter();
                pdRouter.requestRoutes(initPath(start, finish),
                        initOptions(),
                        callback);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                Log.i(TAG, "onMapLongTap");
            }
        };
        mMap.addInputListener(mInputListener);
        mMap.getUserLocationLayer().setEnabled(true);
        // end.

        return rootView;
    }

    public void setCallback(Session.RouteListener callback) {
        this.callback = callback;
    }

    private TimeOptions initOptions() {
        return new TimeOptions();
    }

    private List<RequestPoint> initPath(Point start, Point finish) {
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                start,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                finish,
                RequestPointType.WAYPOINT,
                null));
        return requestPoints;
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

    public Point getFinishMarker() {
        if (mFinishMarker == null) {
            return null;
        }
        return mFinishMarker;
    }

}
