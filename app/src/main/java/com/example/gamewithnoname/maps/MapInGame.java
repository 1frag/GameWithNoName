package com.example.gamewithnoname.maps;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gamewithnoname.BotLocation;
import com.example.gamewithnoname.DistanceBetweenTwoPoints;
import com.example.gamewithnoname.MainActivity;
import com.example.gamewithnoname.ParametersDialog;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
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

    public void setGameResult(int result) {
        if (result == -1) {
            final LayoutInflater factory = getLayoutInflater();
            final View menu = factory.inflate(R.layout.layout_lose, null);
            LinearLayout linearLayout = findViewById(R.id.resultLayout);
            linearLayout.removeAllViews();
            linearLayout.addView(menu, 0);

            Button buttonOkLose = findViewById(R.id.buttonOkLose);
            buttonOkLose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     Intent intent = new Intent(MapInGame.this, MainActivity.class);
                     startActivity(intent);
                }
            });

        } else { //result == 1
            final LayoutInflater factory = getLayoutInflater();
            final View menu = factory.inflate(R.layout.layout_win, null);
            LinearLayout linearLayout = findViewById(R.id.resultLayout);
            linearLayout.removeAllViews();
            linearLayout.addView(menu, 0);

            Button buttonOkWin = findViewById(R.id.buttonOkWin);
            buttonOkWin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapInGame.this, MainActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void configMap(Map map) {
        Log.i(TAG, "configMap");
        Location now = UserLocation.imHere;
        map.move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
//        map.setNightModeEnabled(true);

        map.getUserLocationLayer().setEnabled(true);

        // draw finish:
        double c = getIntent().getExtras().getDouble("finishLatitude");
        double d = getIntent().getExtras().getDouble("finishLongitude");
        finish = new Point(c, d);
        map.getMapObjects().addPlacemark(finish);

        int typeGame = getIntent().getExtras().getInt("typeGame");
        if (typeGame == 2) {
            // bot mode
            // draw start:
            double a = getIntent().getExtras().getDouble("botStartLatitude");
            double b = getIntent().getExtras().getDouble("botStartLongitude");
            start = new Point(a, b);
            map.getMapObjects().addPlacemark(start);

            runBot(new Point(a, b), new Point(c, d));
        } else if(typeGame == 1){
            // online

            onlineRush();
        }

    }

    private void onlineRush() {

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

        double speed = getIntent().getExtras().getDouble("speed");
        Log.i(TAG, String.format("speed: %s", (int)(1000f / speed)));
        bot.start((int)(3600f / speed));
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
