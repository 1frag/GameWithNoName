package com.example.gamewithnoname;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.gamewithnoname.maps.MapInGame;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.PedestrianRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ParametersDialog extends AppCompatActivity implements Session.RouteListener {
    private Double latit;
    private Double longit;

    private Double speed;
    private Double deviation;
    private Double time;
    private Double shortestDistance;
    private Double changedDistance;
    private Boolean oncomingSensitivity;
    private TextView textSpeed;
    private TextView textTime;
    private PedestrianRouter pdRouter;
    private Point start, finish;
    private final String TAG = String.format("%s/%s",
            "HITS", "ParametersDialog");

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_dialog);

        latit = getIntent().getExtras().getDouble("latitude");
        longit = getIntent().getExtras().getDouble("longitude");
        finish = new Point(latit, longit);
        Location now = UserLocation.imHere;
        start = new Point(now.getLatitude(), now.getLongitude());

        TransportFactory.initialize(this);
        pdRouter = TransportFactory.getInstance().createPedestrianRouter();
        pdRouter.requestRoutes(initPath(start, finish), initOptions(), this);

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
    public void onMasstransitRoutes(@NonNull List<Route> list) {

        speed = 5.0; //будет передаваться с сервера когда-нибудь (начальное значение)
        deviation = 1.0; //и это
        shortestDistance = list.get(0).getMetadata().getWeight().getTime().getValue(); //с предыдущего активити
        Log.i(TAG, String.format("%s", shortestDistance));

        textSpeed = findViewById(R.id.textSpeedValue);
        textSpeed.setText(String.format("%.1f km/h", speed));

        time = shortestDistance * deviation / (speed * 1000 / 60);
        textTime = findViewById(R.id.timeApproximate);
        textTime.setText(String.format("Approximate time is %.1f min", time));

        changedDistance = shortestDistance * deviation;

        SeekBar speedSeekBar = findViewById(R.id.seekBarSpeed);
        speedSeekBar.setOnSeekBarChangeListener(new speedListener());

        SeekBar deviationSeekBar = findViewById(R.id.seekBarChange);
        deviationSeekBar.setOnSeekBarChangeListener(new deviationListener());

        Switch sw = findViewById(R.id.switchSensitivity);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    oncomingSensitivity = true;
                } else {
                    oncomingSensitivity = false;
                }
            }
        });
    }

    @Override
    public void onMasstransitRoutesError(@NonNull Error error) {
        String errorMessage = "unknown_error_message";
        if (error instanceof RemoteError) {
            errorMessage = "remote_error_message";
        } else if (error instanceof NetworkError) {
            errorMessage = "network_error_message";
        }

        Log.i(TAG, errorMessage);
    }

    private class speedListener implements SeekBar.OnSeekBarChangeListener {

        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // Log the progress
            //Log.d("DEBUG", "Progress is: "+progress);
            //set textView's text
            Double value = (double) progress;
            value = 3.0 + value / 10.0;
            speed = value;
            time = shortestDistance * deviation / (speed * 1000 / 60);
            textTime.setText(String.format("Approximate time is %.1f min", time));
            textSpeed.setText(String.format("%.1f km/h", value));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    private class deviationListener implements SeekBar.OnSeekBarChangeListener {

        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // Log the progress
            //Log.d("DEBUG", "Progress is: "+progress);
            //set textView's text
            double temp = (double) progress;
            deviation = 1.0 + temp / 10.0;
            time = shortestDistance * deviation / (speed * 1000 / 60);
            changedDistance = shortestDistance * deviation;
            textTime.setText(String.format("Approximate time is %.1f min", time));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    public void nextActivity (View view) {
        Intent intentStart = new Intent(this, MapInGame.class);
        intentStart.putExtra("finish_latitude", latit);
        intentStart.putExtra("finish_longitude", longit);
        // todo: generation point of bot's start, put them in:
        intentStart.putExtra("start_latitude", 56.489233);
        intentStart.putExtra("start_longitude", 84.979591);
        intentStart.putExtra("oncomingSensitivity", oncomingSensitivity);
        intentStart.putExtra("distance", changedDistance);

        startActivity(intentStart);
    }
}
