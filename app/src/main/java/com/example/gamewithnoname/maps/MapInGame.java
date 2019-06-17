package com.example.gamewithnoname.maps;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.utils.BotLocation;
import com.example.gamewithnoname.utils.DistanceBetweenTwoPoints;
import com.example.gamewithnoname.activities.MainActivity;
import com.example.gamewithnoname.callbacks.ChangeCoinsCallbacks;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.UpdateStateBotCallbacks;
import com.example.gamewithnoname.models.User;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.utils.UserLocation;

import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.*;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.gamewithnoname.utils.Constants.ACTION_GO;
import static com.example.gamewithnoname.utils.Constants.ACTION_STOP;

public class MapInGame extends AppCompatActivity implements Session.RouteListener {

    private static final Integer PRICE_STOP_BOT = -1;
    private static final Integer DELAY_IN_STOPPED = 10000; // todo: getDelayInStopped
    private static final Integer DELAY_IN_DISABLE = 5000; // todo: getDelayInDisable

    private MapView mapView;
    private Map mMap;
    private BotLocation bot;
    private final String TAG = String.format("%s/%s", "HITS", "MapInGame");

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
                    finish();
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
                    finish();
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
        Point finish = new Point(c, d);
        map.getMapObjects().addPlacemark(finish);

        // bot mode
        // draw start:
        double a = getIntent().getExtras().getDouble("botStartLatitude");
        double b = getIntent().getExtras().getDouble("botStartLongitude");
        Point start = new Point(a, b);
        map.getMapObjects().addPlacemark(start);

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

        PedestrianRouter pdRouter = TransportFactory.getInstance().createPedestrianRouter();
        pdRouter.requestRoutes(requestPoints, options, this);

        DistanceBetweenTwoPoints d = new DistanceBetweenTwoPoints(start, finish);
        Double resultAsync = d.getResult();
        // todo: это явно было написано человеком
        //  который не сильно то разбирался в этом
        //  если появится $$время$$ то надо разобраться
        //  в этом кусочке кода и привести его в нормальный вид
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
        bot = new BotLocation(this, mMap, routes.get(0).getGeometry());

        double speed = getIntent().getExtras().getDouble("speed");
        Log.i(TAG, String.format("speed: %s", (int) (1000f / speed)));
        bot.start(
                (int) (3600f / speed),
                new UpdateStateBotCallbacks() {
                    @Override
                    public void timeBotToFinish(int seconds) {
                        TextView textView = findViewById(R.id.textBotToEnd);
                        textView.setText(
                                String.format(
                                        getResources().getString(R.string.activity_maps_bot_finishes),
                                        seconds / 60
                                )
                        );
                    }

                    @Override
                    public void distGamerToBot(int dist) {
                        //todo: put dist in any textview
//                        Log.i(TAG, String.format("distGamerToBot is %s", dist));
                    }
                }
        );
//        рисуем путь сразу весь:
//        mMap.getMapObjects().addPolyline(routes.get(0).getGeometry());
    }

    @Override
    public void onMasstransitRoutesError(@NonNull Error error) {
        String errorMessage = "unknown_error_message";
        if (error instanceof RemoteError) {
            errorMessage = "remote_error_message";
        } else if (error instanceof NetworkError) {
            errorMessage = "network_error_message";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void stopBot(View view) {

        ConnectionServer.getInstance().initChangeCoins(
                User.getName(),
                PRICE_STOP_BOT
        );
        ConnectionServer.getInstance().connectChangeCoins(new ChangeCoinsCallbacks() {
            @Override
            public void successful(int money) {

                bot.manageBot(ACTION_STOP);
                findViewById(R.id.mapsButtonPause).setEnabled(false);
                Timer timerManageBot = new Timer();

                final TimerTask goBotTask = new TimerTask() {
                    @Override
                    public void run() {
                        bot.manageBot(ACTION_GO);
                        Log.i(TAG, "Он пошел!");
                    }
                };

                final TimerTask enableButtonTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.mapsButtonPause).setEnabled(true);
                                Log.i(TAG, "Оно разблокировалось!");
                            }
                        });
                    }
                };

                timerManageBot.schedule(goBotTask, DELAY_IN_STOPPED);
                timerManageBot.schedule(enableButtonTask,
                        DELAY_IN_STOPPED + DELAY_IN_DISABLE);
            }

            @Override
            public void badQuery() {
                Log.i(TAG, "badQuery");
            }

            @Override
            public void userDoesNotExist() {
                Log.i(TAG, "userDoesNotExist");
            }

            @Override
            public void notEnoughMoney() {
                Log.i(TAG, "notEnoughMoney");
            }
        });

    }
}
