package com.example.gamewithnoname;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BotLocation {

    private GoogleMap mMap;
    private Activity mActivity;
    private PolylineOptions mPath;
    private LatLng mCenter;
    private LatLng mLastPlace;
    private LatLng mFinish;
    private final String TAG = String.format("%s/%s",
            "HITS", "BotLocation");

    public BotLocation(Activity activity, GoogleMap map, LatLng finish) {
        mActivity = activity;
        mMap = map;
        mFinish = finish;
        mLastPlace = new LatLng(56.451737, 84.987451);
        mPath = new PolylineOptions()
                .color(0x9900ff11)
                .width(25);
        mMap.addPolyline(mPath);
    }

    public void start(int fullTime, final int segment) {
        final Timer timer = new Timer();

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Random r = new Random();
//                double a = 56 + r.nextDouble();
//                double b = 85 + r.nextDouble();

                double vx = mFinish.latitude - mLastPlace.latitude;
                double vy = mFinish.longitude - mLastPlace.longitude;

                double a = r.nextDouble() / 5000f + mLastPlace.latitude + vx / Math.max(vx, vy) / 5000;
                double b = r.nextDouble() / 5000f + mLastPlace.longitude + vy / Math.max(vx, vy) / 5000;

                Log.i(TAG, String.format("%s %s %s %s", vx, vy, a, b));

                vx = mFinish.latitude - a;
                vy = mFinish.longitude - b;
                double sizeAfter = vx * vx + vy * vy;
                Log.i(TAG, String.format("%f", sizeAfter));

                if (sizeAfter < 5e-7) {
                    Log.i(TAG, "end");
                    timer.cancel();
                    return;
                }

                mCenter = new LatLng(a, b);

                mPath = new PolylineOptions()
                        .add(mCenter)
                        .add(mLastPlace)
                        .color(0x9900ff11);

                mLastPlace = new LatLng(a, b);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addPolyline(mPath);
                    }
                });
            }
        };

        timer.schedule(timerTask, 1000, segment);

    }

}
