package com.example.gamewithnoname.maps;

import android.location.Location;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.gamewithnoname.BotLocation;
import com.example.gamewithnoname.DistanceBetweenTwoPoints;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CameraPosition;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.UserLocation;

import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.*;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MapInGame extends AppCompatActivity implements Session.RouteListener {

    private MapView mapView;
    private Map mMap;
    private PedestrianRouter pdRouter;
    private Point start, finish;
    private Double resultAsync;
    private final String TAG = String.format("%s/%s",
            "HITS", "MapInGame");

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "run onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // config for my favorite company and their api:
        MapKitFactory.initialize(this);
        TransportFactory.initialize(this);
        mapView = findViewById(R.id.mapViewInGame);
        mMap = mapView.getMap();
        configMap(mMap);
        // end.

    }

    private void configMap(Map map) {
        Log.i(TAG, "configMap");
        Location now = UserLocation.imHere;
        map.move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
//        map.setNightModeEnabled(true);

        // draw finish:
        double a = getIntent().getExtras().getDouble("botStartLatitude");
        double b = getIntent().getExtras().getDouble("botStartLongitude");
        start = new Point(a, b);
        map.getMapObjects().addPlacemark(start);

        // draw start:
        double c = getIntent().getExtras().getDouble("finishLatitude");
        double d = getIntent().getExtras().getDouble("finishLongitude");
        finish = new Point(c, d);
        map.getMapObjects().addPlacemark(finish);

        // todo: visualization start and path to finish
        // todo: connect bot in this map

//        pedestrianRouter = DirectionsFactory.getInstance().createDrivingRouter();
        runBot(new Point(a, b), new Point(c, d));

    }

    private void runBot(Point start, Point finish) {
        TimeOptions options = new TimeOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                start,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                finish,
                RequestPointType.WAYPOINT,
                null));

        pdRouter = TransportFactory.getInstance().createPedestrianRouter();
        pdRouter.requestRoutes(requestPoints, options, this);

        DistanceBetweenTwoPoints d = new DistanceBetweenTwoPoints(start, finish);
        resultAsync = d.getResult();
        Log.i(TAG, String.format("%s", resultAsync));
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

    @Override
    public void onMasstransitRoutes(List<Route> routes) {
        BotLocation bot = new BotLocation(this, mMap, routes.get(0).getGeometry());
        bot.start(200);
        mMap.getMapObjects().addPolyline(routes.get(0).getGeometry());
    }

    @Override
    public void onMasstransitRoutesError(Error error) {
        String errorMessage = "unknown_error_message";
        if (error instanceof RemoteError) {
            errorMessage = "remote_error_message";
        } else if (error instanceof NetworkError) {
            errorMessage = "network_error_message";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

}
