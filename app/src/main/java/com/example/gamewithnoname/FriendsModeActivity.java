package com.example.gamewithnoname;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.PointResponse;
import com.example.gamewithnoname.ServerConnection.PointsCallbacks;
import com.example.gamewithnoname.ServerConnection.SimpleCallbacks;
import com.example.gamewithnoname.data.model.LoggedInUser;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;
    private PointsCallbacks gameCallbacks;
    private SimpleCallbacks initCallbacks;
    private ConnectionServer connectionServer;
    private String inviteString;
    private MapView mapView;
    private Map mMap;
    private int choise = 0;
    private Integer resultServerCallbacks = -1;
    private final String TAG = String.format("%s/%s",
            "HITS", "FriendsModeActivity"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_mode);

        mapView = findViewById(R.id.mapViewFrMode);
        mMap = mapView.getMap();
        configMap();

        connectionServer = new ConnectionServer();
        configCreateMode();
        configJoinGame();

    }

    private void configMap() {
        Log.i(TAG, "configMap");
        Location now = UserLocation.imHere;
        mMap.move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
//        map.setNightModeEnabled(true);

        mMap.getUserLocationLayer().setEnabled(true);

    }

    private void configJoinGame() {
        Button btn = findViewById(R.id.button_join_game);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choise = 2;
                // todo: блокировка интерфейса
                initCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        if (value.equals("2")) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "query is incorrect",
                                    //todo: этого никогда не должно произойти
                                    // но если случился такой тост то либо
                                    // изменилось что-то на сервере либо на клиенте
                                    // в любом случае надо сообщить об этом куда то
                                    // чтобы разработчики знали что это произошло
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ((EditText) findViewById(R.id.textViewCode)).setText(value);
                        inviteString = ((EditText) findViewById(R.id.textViewCode))
                                .getText().toString();
                        mainGameLoop();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Toast.makeText(FriendsModeActivity.this,
                                "problem with internet", // todo: transfer to string.xml
                                Toast.LENGTH_SHORT).show();
                    }
                };

                connectionServer.initJoinGame(
                        LoggedInUser.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude(),
                        ((EditText) findViewById(R.id.editTextCode)).getText().toString()
                );
                connectionServer.connectSimple(initCallbacks);
                // todo: обратиться к серверу чтобы заjoinиться
            }
        });

    }

    private void configCreateMode() {
        Button btn = findViewById(R.id.button_create_game);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choise = 1;
                // todo: блокировка интерфейса
                initCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        if (value.equals("2")) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "query is incorrect",
                                    //todo: этого никогда не должно произойти
                                    // но если случился такой тост то либо
                                    // изменилось что-то на сервере либо на клиенте
                                    // в любом случае надо сообщить об этом куда то
                                    // чтобы разработчики знали что это произошло
                                    Toast.LENGTH_SHORT).show();
                        }
                        ((EditText) findViewById(R.id.textViewCode)).setText(value);
                        inviteString = ((EditText) findViewById(R.id.textViewCode))
                                .getText().toString();
                        mainGameLoop();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Toast.makeText(FriendsModeActivity.this,
                                "problem with internet", // todo: transfer to string.xml
                                Toast.LENGTH_SHORT).show();
                    }
                };

                connectionServer.initCreateGame(
                        LoggedInUser.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude()
                );
                connectionServer.connectSimple(initCallbacks);
            }
        });

    }

    public void mainGameLoop() {

        mTimer = new Timer();

        gameCallbacks = new PointsCallbacks() {

            @Override
            public void onSuccess(@NonNull List<PointResponse> result) {
                for (PointResponse point : result) {
                    if (point.getType() == 1) { // coin
                        mMap.getMapObjects().addCircle(
                                new Circle(
                                        new Point(
                                                point.getLatitude(),
                                                point.getLongitude()
                                        ),
                                        15
                                ),
                                Color.YELLOW,
                                10,
                                Color.YELLOW
                        );
                    } else if (point.getType() == 2) { // human
                        mMap.getMapObjects().addCircle(
                                new Circle(
                                        new Point(
                                                point.getLatitude(),
                                                point.getLongitude()
                                        ),
                                        15
                                ),
                                Color.GRAY,
                                10,
                                Color.GREEN
                        );
                    } else if (point.getType() == 3) { // old coin
                        // todo: тип пропавшей монеты (она была зобрана)
                        //  можем обработать если надо
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.i(TAG, "lol kek");
                // todo: ну это опять у них проблемки
                //  с интернетом, хз че делать
            }
        };

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectionServer.initUpdateMap(
                        LoggedInUser.getName(),
                        inviteString,
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude()
                );
                connectionServer.connectPoints(gameCallbacks);
            }
        };

        mTimer.schedule(timerTask, 1000, 1000);
    }

}
