package com.example.gamewithnoname;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.Map;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BotLocation {

    private Map mMap;
    private Activity mActivity;
    private int ind = 0;
    private ArrayList<Point> path = new ArrayList<>();
    private final String TAG = String.format("%s/%s",
            "HITS", "BotLocation");

    public BotLocation(Activity activity, Map map, Polyline linePath) {
        mActivity = activity;
        mMap = map;
        List<Point> points = linePath.getPoints();
        for (int i = 1; i < points.size(); i++) {
            Point now = points.get(i - 1);
            double vx0 = points.get(i).getLatitude() - points.get(i - 1).getLatitude();
            double vy0 = points.get(i).getLongitude() - points.get(i - 1).getLongitude();
            double ny = Math.sqrt((vy0 * vy0) / (vy0 * vy0 + vx0 * vx0)) / 111111.0;
            double nx = Math.sqrt(1 - ny * ny) / 111111.0;

            if (vx0 < 0) nx = -nx;
            if (vy0 < 0) ny = -ny;

            Log.i(TAG, String.format("(%s %s), (%s %s)", vx0, vy0, ny, nx));
            Log.i(TAG, String.format("%s == 1", ny * ny + nx * nx));
            Log.i(TAG, String.format("%s == %s", nx / ny, vx0 / vy0));

            path.add(now);
            while (check(points.get(i - 1), now, points.get(i))) {
                now = new Point(
                        now.getLatitude() + nx,
                        now.getLongitude() + ny
                );
                path.add(now);
//                Log.i(TAG, String.format("%s %s", now.getLongitude(), now.getLongitude()));
            }
        }

    }

    private boolean check(Point start, Point now, Point finish) {
        double fx = Math.abs(start.getLatitude() - finish.getLatitude());
        double fy = Math.abs(start.getLongitude() - finish.getLongitude());
        double nx = Math.abs(start.getLatitude() - now.getLatitude());
        double ny = Math.abs(start.getLongitude() - now.getLongitude());
        if (fx <= nx) return false;
        if (fy <= ny) return false;
        return true;
    }

    public void start(final int segment) {
        final Timer timer = new Timer();

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ind++;
                if (ind >= path.size()) {
                    timer.cancel();
                    return;
                }

                Location now = UserLocation.imHere;
                double a = now.getLatitude() - path.get(ind).getLatitude();
                double b = now.getLongitude() - path.get(ind).getLongitude();
                double z = a * a + b * b;
                if (z < 0.000003) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity,
                                    "You win", 1).show();
                        }
                    });
                    timer.cancel();
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.getMapObjects().addCircle(new Circle(path.get(ind), 1),
                                Color.BLACK, 1, Color.RED);
                    }
                });
            }
        };

        timer.schedule(timerTask, 1000, segment);

    }

}
