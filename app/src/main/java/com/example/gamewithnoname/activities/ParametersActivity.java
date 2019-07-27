package com.example.gamewithnoname.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.CreateGWBCallbacks;
import com.example.gamewithnoname.models.User;
import com.example.gamewithnoname.utils.UserLocation;
import com.example.gamewithnoname.maps.MapInGame;
import com.example.gamewithnoname.maps.MapMainMenu;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.runtime.Error;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ParametersActivity extends AppCompatActivity {

    private Double speed;
    private Double deviation;
    private Double time;
    private Double shortestDistance;
    private Double changedDistance;
    private int angle;
    private TextView textSpeed;
    private TextView textTime;
    private TextView textAngle;
    private Point start, finish;
    private Button btnContinue;
    private Button btnParameters;
    private MapMainMenu map;
    private final String TAG = String.format("%s/%s",
            "HITS", "ParametersActivity");
    private BottomSheetDialog dialog;
    private View sheetView;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_botplay_parameters);

        btnContinue = findViewById(R.id.buttonContinue);
        btnContinue.setEnabled(false);

        btnParameters = findViewById(R.id.buttonParameters);
        btnParameters.setEnabled(false);

        sheetView = getLayoutInflater().inflate(R.layout.layout_parameters, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(sheetView);

        configureMap();

    }

    private void configureMap() {
        Log.i(TAG, "configureMap");
        MapKitFactory.setApiKey("4431f62e-4cef-4ce6-b1d5-07602abde3fd"); // todo: remove pls
        TransportFactory.initialize(this);

        if (User.getHints()) {
            (findViewById(R.id.btnInfo)).setVisibility(View.VISIBLE);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        map = new MapMainMenu();
        map.setCallback(new Session.RouteListener() {
            @Override
            public void onMasstransitRoutes(@NonNull List<Route> list) {
                // Эта функция может сказать гораздо больше чем делает это сейчас
                if (list.size() == 0) initNewParams(null);
                else initNewParams(list.get(0).getMetadata().getWeight().getTime().getValue());
            }

            @Override
            public void onMasstransitRoutesError(@NonNull Error error) {
                Log.i(TAG, "onMasstransitRoutesError");
                Toast.makeText(ParametersActivity.this,
                        getResources().getString(R.string.chouse_other_point),
                        Toast.LENGTH_LONG).show();
            }
        });
        transaction.replace(R.id.mapHolder, map);
        transaction.commit();

    }

    public Point anotherBotStart() {
        Double r = sqrt(
                pow((finish.getLatitude() - start.getLatitude()), 2)
                        + pow((finish.getLongitude() - start.getLongitude()), 2));
        double alpha = angle;
        alpha = Math.tan(Math.toRadians(alpha));
        Double x1 = finish.getLatitude();
        Double y1 = finish.getLongitude();
        Double x2 = start.getLatitude();
        Double y2 = start.getLongitude();
        if(x2.equals(x1)){
            return new Point((finish.getLatitude() + start.getLatitude()) / 2,
                    (finish.getLongitude() + start.getLongitude()) / 2);
        }
        // todo: Asya;
        Double k1 = (y2 - y1) / (x2 - x1);
        if((1 + alpha * k1) == 0 || (1 - alpha * k1) == 0){
            return new Point((finish.getLatitude() + start.getLatitude()) / 2,
                    (finish.getLongitude() + start.getLongitude()) / 2);
        }
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
        if (random.nextBoolean()) {
            return a;
        } else {
            return b;
        }
    }

    private double dist(Point a, Point b) {
        return (a.getLongitude() - b.getLongitude()) *
                (a.getLongitude() - b.getLongitude()) +
                (a.getLatitude() - b.getLatitude()) *
                        (a.getLatitude() - b.getLatitude());
    }

    @SuppressLint("DefaultLocale") // todo: change after adding different language
    public void initNewParams(@Nullable Double distance) {

        if (distance == null) {
            Toast.makeText(ParametersActivity.this,
                    getResources().getString(R.string.way_not_found),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "initNewParams");

        Location now = UserLocation.imHere;
        start = new Point(now.getLatitude(), now.getLongitude());
        speed = 7.0; //будет передаваться с сервера когда-нибудь (начальное значение)
        deviation = 1.0; //и это
        angle = 40; //и это тоже
        shortestDistance = distance;
        Log.i(TAG, String.format("%s", shortestDistance));
        textSpeed = sheetView.findViewById(R.id.textSpeedValue);
        textSpeed.setText(String.format(getResources().getString(R.string.speed_in_first_mode), speed));
        time = shortestDistance * deviation / (speed * 1000 / 60);
        Log.i(TAG, String.format("time is %s", time));
        textTime = sheetView.findViewById(R.id.timeApproximate);
        textTime.setText(String.format(getResources().getString(R.string.time_in_first_mode), time));
        textAngle = sheetView.findViewById(R.id.textViewAngleVal);
        textAngle.setText(String.format(getResources().getString(R.string.angle_in_first_mode), angle));
        changedDistance = shortestDistance * deviation;
        SeekBar speedSeekBar = sheetView.findViewById(R.id.seekBarSpeed);
        speedSeekBar.setOnSeekBarChangeListener(new speedListener());
        SeekBar angleSeekBar = sheetView.findViewById(R.id.seekBarAngle);
        angleSeekBar.setOnSeekBarChangeListener(new angleListener());
        btnContinue.setEnabled(true);
        btnParameters.setEnabled(true);
        findViewById(R.id.textInstruction).setVisibility(TextView.INVISIBLE);
    }

    private class speedListener implements SeekBar.OnSeekBarChangeListener {

        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Double value = (double) progress;
            value = 3.0 + value / 10.0;
            speed = value;
            time = shortestDistance * deviation / (speed * 1000 / 60);
            textTime.setText(String.format(getResources().getString(R.string.time_in_first_mode), time));
            textSpeed.setText(String.format(getResources().getString(R.string.speed_in_first_mode), value));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    private class angleListener implements SeekBar.OnSeekBarChangeListener {
        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Double value = (double) progress;
            value = 20 + value * 5;
            angle = (int)(double)value;
            textAngle.setText(String.format(getResources().getString(R.string.angle_in_first_mode), angle));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    public void watchParameters(View view) {
        dialog.show();
    }

    public void nextActivity(View view) {
        final Intent intentStart = new Intent(this, MapInGame.class);

        finish = map.getFinishMarker();
        if (finish == null) {
            Toast.makeText(this, getString(R.string.activity_parameters_unknown_error), Toast.LENGTH_LONG).show();
            return;
        }

        intentStart.putExtra("finishLatitude", finish.getLatitude());
        intentStart.putExtra("finishLongitude", finish.getLongitude());
        intentStart.putExtra("distance", changedDistance);
        intentStart.putExtra("speed", speed);
        intentStart.putExtra("alpha", angle);
        Point point = anotherBotStart();
        // sometimes problem with point
        intentStart.putExtra("botStartLatitude", point.getLatitude());
        intentStart.putExtra("botStartLongitude", point.getLongitude());
        intentStart.putExtra("time", 0);

        CreateGWBCallbacks callback = new CreateGWBCallbacks() {
            @Override
            public void success() {
                startActivity(intentStart);
                finish();
            }
        };

        final ArrayList<View> viewsToDisable = null;
        ConnectionServer.getInstance().initCreateGWB(
                User.getName(),
                (int)(double)angle,
                speed,
                point.getLatitude(),
                point.getLongitude(),
                finish.getLatitude(),
                finish.getLongitude(),
                viewsToDisable
        );
        ConnectionServer.getInstance().connectCreateGWB(callback, viewsToDisable);


    }

    public void showHintsParameters(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.activity_parameters_hints_headline))
                .setMessage(getString(R.string.activity_parameters_hints))
                .setNegativeButton(android.R.string.yes, null)
                .show();
    }
}
