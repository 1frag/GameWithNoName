package com.example.gamewithnoname.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;

import com.example.gamewithnoname.activities.ParametersActivity;
import com.example.gamewithnoname.callbacks.UpdateStateBotCallbacks;
import com.example.gamewithnoname.maps.MapInGame;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.map.MapObject;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.gamewithnoname.utils.Constants.ACTION_GO;
import static com.example.gamewithnoname.utils.Constants.ACTION_STOP;
import static com.example.gamewithnoname.utils.Constants.DELAY_TO_START;
import static com.example.gamewithnoname.utils.Constants.DISTANCE_BOT_CATCH;
import static com.example.gamewithnoname.utils.Constants.FILL_COLOR_BOT_AFTER;
import static com.example.gamewithnoname.utils.Constants.FILL_COLOR_BOT_IN;
import static com.example.gamewithnoname.utils.Constants.LOSE;
import static com.example.gamewithnoname.utils.Constants.SIZE_POINT_BOT_AFTER;
import static com.example.gamewithnoname.utils.Constants.SIZE_POINT_BOT_IN;
import static com.example.gamewithnoname.utils.Constants.SIZE_STROKE_BOT_AFTER;
import static com.example.gamewithnoname.utils.Constants.STROKE_COLOR_BOT_AFTER;
import static com.example.gamewithnoname.utils.Constants.WIN;

public class BotLocation {

    private MapObject nowPointBot;

    private IconStyle iconStyle;
    private ImageProvider imageProvider;

    private Map mMap;
    private MapInGame mActivity;
    private int ind = 0;
    private int timeBegin = 0;
    private int resultGame = 0;
    private boolean stopped = false;

    private ArrayList<Point> path = new ArrayList<>();
    private Timer mTimer;
    private final String TAG = String.format("%s/%s",
            "HITS", "BotLocation");

    public BotLocation(MapInGame activity, Map map, Polyline linePath, int time) {
        mActivity = activity;
        mMap = map;
        timeBegin = time;
        List<Point> points = linePath.getPoints();
        for (int i = 1; i < points.size(); i++) {
            Point A = points.get(i - 1);
            Point B = points.get(i);
            // I change some code, pls test it, and report if it needs
            double z = Geo.distance(A, B);
            path.add(A);

            for (double j = 0; j <= z; j += 1) {
                Point C = new Point(
                        A.getLatitude() + j * (B.getLatitude() - A.getLatitude()) / z,
                        A.getLongitude() + j * (B.getLongitude() - A.getLongitude()) / z
                );
                path.add(C);
            }
        }


        // some setting for point bot:
        iconStyle = new IconStyle();
        iconStyle.setFlat(true);
        iconStyle.setVisible(true);
        imageProvider = new ImageProvider() {
            @Override
            public String getId() {
                return "Bot";
            }

            @Override
            public Bitmap getImage() {
                Bitmap bitmap = Bitmap.createBitmap(
                        SIZE_POINT_BOT_IN,
                        SIZE_POINT_BOT_IN,
                        Bitmap.Config.ARGB_8888
                );
                bitmap.eraseColor(Color.TRANSPARENT);
                int rad = SIZE_POINT_BOT_IN / 2;
                for (int i = 0; i < SIZE_POINT_BOT_IN; i++) {
                    for (int j = 0; j < SIZE_POINT_BOT_IN; j++) {
                        if ((i - rad) * (i - rad) + (j - rad) * (j - rad) <= rad * rad) {
                            bitmap.setPixel(i, j, FILL_COLOR_BOT_IN);
                        }
                    }
                }
                return bitmap;
            }
        };

    }

    public void manageBot(Integer action) {
        if (action.equals(ACTION_STOP)) {
            stopped = true;
        }

        if (action.equals(ACTION_GO)) {
            stopped = false;
        }

    }

    private void setGameResult(int result) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
            resultGame = result;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.setGameResult(resultGame);
                }
            });
        }
    }

    public void start(final int segment, final UpdateStateBotCallbacks callback) {
        mTimer = new Timer();
        ind = timeBegin * 1000 / segment;

        if (ind >= path.size()) {
            setGameResult(LOSE);
            return;
        }

        for (int j = 0; j < ind; j++) {
            mMap.getMapObjects().addCircle(
                    new Circle(
                            path.get(j),
                            SIZE_POINT_BOT_AFTER
                    ),
                    STROKE_COLOR_BOT_AFTER,
                    SIZE_STROKE_BOT_AFTER,
                    FILL_COLOR_BOT_AFTER);
        }

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (stopped) {
                    return;
                }

                ind++;
                if (ind < 0) {
                    return;
                }

                if (ind >= path.size()) {
                    setGameResult(LOSE);
                    return;
                }

                Location now = UserLocation.imHere;
                final Point pnow = new Point(now.getLatitude(), now.getLongitude());
                double zd = Geo.distance(pnow, path.get(ind));
                if (zd <= DISTANCE_BOT_CATCH) {
                    setGameResult(WIN);
                    return;
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // вызываем колбак
                        callback.timeBotToFinish(
                                segment * (path.size() - ind) / 1000
                        );
                        callback.distGamerToBot(
                                (int) Geo.distance(
                                        path.get(ind),
                                        pnow
                                )
                        );

                        // нарисуем path.get(ind - 1) как старую точку
                        if (ind != 0) {
                            mMap.getMapObjects().addCircle(
                                    new Circle(
                                            path.get(ind - 1),
                                            SIZE_POINT_BOT_AFTER
                                    ),
                                    STROKE_COLOR_BOT_AFTER,
                                    SIZE_STROKE_BOT_AFTER,
                                    FILL_COLOR_BOT_AFTER);
                        }

                        // удаляем на карте now
                        if (nowPointBot != null)
                            mMap.getMapObjects().remove(nowPointBot);

                        // рисуем текущее положение бота
                        nowPointBot = mMap.getMapObjects().addPlacemark(
                                path.get(ind),
                                imageProvider,
                                iconStyle
                        );
                    }
                });
            }
        };

        mTimer.schedule(timerTask, DELAY_TO_START, segment);

    }

}
