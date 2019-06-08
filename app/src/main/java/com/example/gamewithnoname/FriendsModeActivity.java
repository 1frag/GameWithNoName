package com.example.gamewithnoname;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.ui_view.ViewProvider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;
    private PointsCallbacks gameCallbacks;
    private SimpleCallbacks initCallbacks;
    private SimpleCallbacks goCallbacks;
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
        configGoButton();

    }

    private void configGoButton() {
        Button btn = findViewById(R.id.button_go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        if (value.equals("2")) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "begin_game :: 2 (param is invalid)",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else if (value.equals("3")) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "begin_game :: 3 (game there is not)",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else if (value.equals("4")) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "begin_game :: 4 (access denied)",
                                    //начать может только создатель ссылки
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ((Button) findViewById(R.id.button_join_game)).setEnabled(false);
                        ((Button) findViewById(R.id.button_create_game)).setEnabled(false);
                        ((EditText) findViewById(R.id.editTextCode)).setEnabled(false);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "eroor!");
                    }
                };

                connectionServer.initBeginGame(
                        LoggedInUser.getName(),
                        inviteString
                );
                connectionServer.connectSimple(goCallbacks);

            }
        });

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
                        ((TextView) findViewById(R.id.textViewCode)).setText(value);
                        inviteString = ((TextView) findViewById(R.id.textViewCode))
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
                        ((TextView) findViewById(R.id.textViewCode)).setText(value);
                        inviteString = ((TextView) findViewById(R.id.textViewCode))
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

                        IconStyle iconStyle = new IconStyle();
                        iconStyle.setFlat(true);
                        iconStyle.setVisible(true);
                        final String namePoint = point.getName();
                        ImageProvider imageProvider = new ImageProvider() {
                            @Override
                            public String getId() {
                                return namePoint;
                            }

                            @Override
                            public Bitmap getImage() {
                                Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                                bitmap.eraseColor(Color.TRANSPARENT);
                                for (int i = 0; i < 10; i++) {
                                    for (int j = 0; j < 10; j++) {
                                        if ((i - 5) * (i - 5) + (j - 5) * (j - 5) <= 25) {
                                            bitmap.setPixel(i, j, Color.BLUE);
                                        }
                                    }
                                }
                                return bitmap;
                            }
                        };


                        mMap.getMapObjects().addPlacemark(
                                new Point(
                                        point.getLatitude(),
                                        point.getLongitude()
                                ),
                                imageProvider,
                                iconStyle
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
