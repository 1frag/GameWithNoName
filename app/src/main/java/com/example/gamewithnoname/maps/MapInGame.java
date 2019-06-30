package com.example.gamewithnoname.maps;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.callbacks.SimpleCallbacks;
import com.example.gamewithnoname.utils.BotLocation;
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

    private MapView mapView;
    private Map mMap;
    private double pathBotToFinish;
    public Timer cnterSteps;
    private BotLocation bot;
    private Integer isFirst = 1;
    private int allTime, count = 0;
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
        isFirst = 1;
        // end.
        initTimerCounterKM();
    }

    private void initTimerCounterKM() {
        cnterSteps = new Timer();
        final ArrayList<View> viewsToDisable = null;
        Log.i(TAG, "initTimer");
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ConnectionServer.getInstance().initUpdateGWB(
                        User.getName(),
                        isFirst,
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude(),
                        viewsToDisable
                );
                ConnectionServer.getInstance().connectSimple(null, viewsToDisable);
                isFirst = 0;
            }
        };
        cnterSteps.schedule(timerTask, 1000, 1000);

    }

    public Integer getDelayInStopped(){
        return allTime / 10;
    }

    public Integer getDelayInDisable(){
        return allTime / 10;
    }

    public Integer getPriceStopBot(){
        count ++;
        return 2 + (count - 1) * 3;
    }

    public void setGameResult(int result) {
        cnterSteps.cancel();
        cnterSteps.purge();
        findViewById(R.id.textBotToEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.mapsButtonPause).setVisibility(View.INVISIBLE);
        findViewById(R.id.linearLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.button3).setVisibility(View.INVISIBLE);
        final ArrayList<View> viewsToDisable = null;

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
            ConnectionServer.getInstance().initKillGWB(User.getName(), null);
            ConnectionServer.getInstance().connectSimple(null, null);

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

            int alpha = getIntent().getExtras().getInt("alpha");
            int alphas = (int) (alpha * pathBotToFinish);
            ConnectionServer.getInstance().initChangeRating(User.getName(), alphas, viewsToDisable);
            ConnectionServer.getInstance().connectSimple(null, viewsToDisable);
            TextView textScore = findViewById(R.id.textWinScore);
            textScore.setText(String.format(getResources().getString(R.string.your_score_is), alphas));

//            ConnectionServer.getInstance().initGetMySpeedGWB(User.getName(), viewsToDisable);
//            ConnectionServer.getInstance().connectSimple(new SimpleCallbacks() {
//                @Override
//                public void onSuccess(@NonNull Integer value) {
//                    TextView textSpeed = findViewById(R.id.textWinSpeed);
//                    textSpeed.setText(String.format(getResources().getString(R.string.your_speed_is), value * 3.6));
//                    ConnectionServer.getInstance().initKillGWB(User.getName(), viewsToDisable);
//                    ConnectionServer.getInstance().connectSimple(null, viewsToDisable);
//                }
//
//                @Override
//                public void onError(@NonNull Throwable throwable) {
//                    //бля, не хочу, убейте
//                }
//            }, viewsToDisable);
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
    public void onMasstransitRoutes(@NonNull List<Route> routes) {
        int meTime = getIntent().getExtras().getInt("time");
        int allstops = getIntent().getExtras().getInt("stops");
        allTime = meTime - allstops * getDelayInStopped();
        if (routes.size() == 0) {
            Toast.makeText(MapInGame.this,
                    getResources().getString(R.string.just_try_again),
                    Toast.LENGTH_LONG).show();
            final ArrayList<View> viewsToDisable = null;
            ConnectionServer.getInstance().initKillGWB(User.getName(), viewsToDisable);
            ConnectionServer.getInstance().connectSimple(null, viewsToDisable);
            if(cnterSteps != null) {
                cnterSteps.cancel();
                cnterSteps.purge();
            }
            finish();
            return;
        }
        bot = new BotLocation(this, mMap, routes.get(0).getGeometry(), allTime);

        final double speed = getIntent().getExtras().getDouble("speed");
        Log.i(TAG, String.format("speed: %s", (int) (1000f / speed)));
        bot.start(
                (int) (3600f / speed),
                new UpdateStateBotCallbacks() {
                    @Override
                    public void timeBotToFinish(int seconds) {
                        pathBotToFinish = seconds * speed / 3600.0;
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
        String errorMessage = getResources().getString(R.string.unknown_error);
        if (error instanceof RemoteError) {
            errorMessage = getResources().getString(R.string.remote_problem);
        } else if (error instanceof NetworkError) {
            errorMessage = getResources().getString(R.string.network_problem);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void stopBot(View view) {

        final ArrayList<View> viewsToDisable = null;
        ConnectionServer.getInstance().initChangeCoins(
                User.getName(),
                getPriceStopBot(),
                viewsToDisable
        );
        ConnectionServer.getInstance().connectChangeCoins(new ChangeCoinsCallbacks() {
            @Override
            public void successful(int money) {

                bot.manageBot(ACTION_STOP);
                findViewById(R.id.mapsButtonPause).setEnabled(false);
                findViewById(R.id.mapsButtonPause).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sea_button_selected));
                Timer timerManageBot = new Timer();

                final TimerTask goBotTask = new TimerTask() {
                    @Override
                    public void run() {
                        bot.manageBot(ACTION_GO);
                        Log.i(TAG, "Бот пошел!");
                    }
                };

                final TimerTask enableButtonTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (count == 8) {
                                    return;
                                }
                                findViewById(R.id.mapsButtonPause).setEnabled(true);
                                findViewById(R.id.mapsButtonPause).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pause_button));
                                Log.i(TAG, "Кнопка разблокировалась!");
                            }
                        });
                    }
                };

                timerManageBot.schedule(goBotTask, getDelayInStopped());
                timerManageBot.schedule(enableButtonTask,
                        getDelayInStopped() + getDelayInDisable());
            }

            @Override
            public void notEnoughMoney() {
                Toast.makeText(MapInGame.this,
                        getResources().getString(R.string.problem_with_money),
                        Toast.LENGTH_LONG).show();
                Log.i(TAG, "notEnoughMoney");
            }
        }, viewsToDisable);

    }

    public void killGame() {
        final ArrayList<View> viewsToDisable = null;
        ConnectionServer.getInstance().initKillGWB(User.getName(), viewsToDisable);
        ConnectionServer.getInstance().connectSimple(null, viewsToDisable);
        cnterSteps.cancel();
        cnterSteps.purge();
        finish();
    }

    public void areYouSure(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_2m_finish_game))
                .setMessage(getString(R.string.alert_2m_finish_game_text))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        killGame();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
