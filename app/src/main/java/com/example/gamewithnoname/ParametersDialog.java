package com.example.gamewithnoname;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

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
    private Button btnContinue;
    private final String TAG = String.format("%s/%s",
            "HITS", "ParametersDialog");

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
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

        btnContinue = findViewById(R.id.buttonContinue);
        btnContinue.setEnabled(false);

    }

    public Point botStart() {
        Double r = sqrt(pow((latit - start.getLatitude()), 2) + pow((longit - start.getLongitude()), 2));
        Double mx = (latit + start.getLatitude()) / 2f;
        Double my = (longit + start.getLongitude()) / 2f;
        Double ax = latit - start.getLatitude();
        Double ay = longit - start.getLongitude();
        Double bx = -ay;
        Double by;
        by = ax;
        Double cmy1 = sqrt(3.0 * r * r / (4.0 + (4.0 * bx * bx / (by * by))));
        Double cmy2 = -cmy1;
        Double cmx1 = cmy1 * bx / by;
        Double cmx2 = -cmx1;
        cmy1 += my;
        cmy2 += my;
        cmx1 += mx;
        cmx2 += mx;
        Random random = new Random();
        Log.i(TAG, String.format("%s==%s", dist(start, finish), dist(new Point(cmx1, cmy1), finish)));
        if (random.nextBoolean()) {return new Point(cmx1, cmy1);} else {return new Point(cmx2, cmy2);}
    }

    public Point anotherBotStart() {
        Double r = sqrt(pow((latit - start.getLatitude()), 2) + pow((longit - start.getLongitude()), 2));
        double alpha = 90.0;
        alpha = Math.tan(Math.toRadians(alpha));
        Double x1 = latit;
        Double y1 = longit;
        Double x2 = start.getLatitude();
        Double y2 = start.getLongitude();
        Double k1 = (y2 - y1) / (x2 - x1);
        // todo: division 0
        Double k2_1 = (k1 - alpha) / (1 + alpha * k1);
        Double k2_2 = (k1 + alpha) / (1 - alpha * k1);
        Double b2_1 = y1 - k2_1 * x1;
        Double b2_2 = y1 - k2_2 * x1;
        Double b_for_d1 = -2 * x1 + 2 * k2_1 * b2_1 - 2 * k2_1 * y1;
        Double b_for_d2 = -2 * x1 + 2 * k2_2 * b2_2 - 2 * k2_2 * y1;
        Double d1 = pow(b_for_d1, 2) - 4 * (1 + pow(k2_1, 2)) *
                (pow(x1, 2) + pow(b2_1, 2) - 2 * b2_1 * y1 + pow(y1, 2) - pow(r, 2));
        Double d2 = pow(b_for_d2, 2) - 4 * (1 + pow(k2_2, 2)) *
                (pow(x1, 2) + pow(b2_2, 2) - 2 * b2_2 * y1 + pow(y1, 2) - pow(r, 2));
        Double x_a = (-b_for_d1 + sqrt(d1)) / (2 * (1 + pow(k2_1, 2)));
        Double y_a = k2_1 * x_a + b2_1;
        Double x_b = (-b_for_d1 - sqrt(d1)) / (2 * (1 + pow(k2_1, 2)));
        Double y_b = k2_1 * x_b + b2_1;
        Double x_c = (-b_for_d2 + sqrt(d2)) / (2 * (1 + pow(k2_2, 2)));
        Double y_c = k2_2 * x_c + b2_2;
        Double x_d = (-b_for_d2 - sqrt(d2)) / (2 * (1 + pow(k2_2, 2)));
        Double y_d = k2_2 * x_d + b2_2;
        Point a;
        Point b;
        if (sqrt((pow((x2 - x_a), 2) + pow((y2 - y_a), 2))) < sqrt((pow((x2 - x_b), 2) + pow((y2 - y_b), 2)))) {
            a = new Point(x_a, y_a);
        } else {
            a = new Point(x_b, y_b);
        }
        if (sqrt((pow((x2 - x_c), 2) + pow((y2 - y_c), 2))) < sqrt((pow((x2 - x_d), 2) + pow((y2 - y_d), 2)))) {
            b = new Point(x_c, y_c);
        } else {
            b = new Point(x_d, y_d);
        }
        Random random = new Random();
        if (random.nextBoolean()) {return a;} else {return b;}
    }

    private double dist(Point a, Point b) {
        return (a.getLongitude() - b.getLongitude()) *
                (a.getLongitude() - b.getLongitude()) +
                (a.getLatitude() - b.getLatitude()) *
                        (a.getLatitude() - b.getLatitude());
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onMasstransitRoutes(@NonNull List<Route> list) {
        Log.i(TAG, "onMasstransitRoutes");

        speed = 5.0; //будет передаваться с сервера когда-нибудь (начальное значение)
        deviation = 1.0; //и это
        shortestDistance = list.get(0).getMetadata().getWeight().getTime().getValue(); //с предыдущего активити
        Log.i(TAG, String.format("%s", shortestDistance));

        textSpeed = findViewById(R.id.textSpeedValue);
        textSpeed.setText(String.format("%.1f km/h", speed));

        time = shortestDistance * deviation / (speed * 1000 / 3600);
        textTime = findViewById(R.id.timeApproximate);
        textTime.setText(String.format("About %.1f min", time));

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
        btnContinue.setEnabled(true);
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
            textTime.setText(String.format("About %.1f min", time));
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
            textTime.setText(String.format("About %.1f min", time));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    public void nextActivity (View view) {
        Intent intentStart = new Intent(this, MapInGame.class);
        intentStart.putExtra("finishLatitude", latit);
        intentStart.putExtra("finishLongitude", longit);
        intentStart.putExtra("oncomingSensitivity", oncomingSensitivity);
        intentStart.putExtra("distance", changedDistance);
        intentStart.putExtra("speed", speed);

        Point point = anotherBotStart();
        // sometimes problem with point
        intentStart.putExtra("botStartLatitude", point.getLatitude());
        intentStart.putExtra("botStartLongitude", point.getLongitude());

        startActivity(intentStart);
    }
}
