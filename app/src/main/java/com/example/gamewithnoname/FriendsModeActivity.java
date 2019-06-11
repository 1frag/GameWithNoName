package com.example.gamewithnoname;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.Gamers.GamersResponse;
import com.example.gamewithnoname.ServerConnection.Points.PointsResponse;
import com.example.gamewithnoname.ServerConnection.Points.PointsCallbacks;
import com.example.gamewithnoname.ServerConnection.Simple.SimpleCallbacks;
import com.example.gamewithnoname.ServerConnection.Statistics.StatisticsResponse;
import com.example.gamewithnoname.ServerConnection.UpdateStateCallbacks;
import com.example.gamewithnoname.data.model.LoggedInUser;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;

    private SimpleCallbacks initCallbacks;
    private SimpleCallbacks goCallbacks;
    private SimpleCallbacks killRunningGameCallbacks;

    private ConnectionServer connectionServer;
    private String inviteString;
    private MapView mapView;
    private Map mMap;
    private Integer counterCoins = 0;
    private ArrayList<MapObject> coinspositions = new ArrayList<>();
    private ArrayList<MapObject> lastPlayersPositions = new ArrayList<>();
    private LinearLayout stageHandler;
    private int choise = 0;
    private int GETTING_RADIUS = 18;
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
        stageHandler(0);
        configCreateMode();
        configJoinGame();
    }

    private void stageHandler(int stage) {
        if (stage == 0) {
            /*Стартовое положение дел*/
            (findViewById(R.id.button_create_game)).setVisibility(View.VISIBLE);
            (findViewById(R.id.button_join_game)).setVisibility(View.VISIBLE);
            (findViewById(R.id.imageButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton2)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.textViewCode)).setVisibility(View.INVISIBLE);

            // go button set first type
            (findViewById(R.id.button_go)).setVisibility(View.GONE);

            // linearlayout's param magic:
            LinearLayout layout = findViewById(R.id.layout_mapview);
            ViewGroup.LayoutParams params = layout.getLayoutParams();

            layout.setLayoutParams(params);

            if (coinspositions != null){
                for (MapObject mapObject : coinspositions){
                    mMap.getMapObjects().remove(mapObject);
                }
                coinspositions.clear();
            }

            if (lastPlayersPositions != null){
                for (MapObject mapObject : lastPlayersPositions){
                    mMap.getMapObjects().remove(mapObject);
                }
                lastPlayersPositions.clear();
            }

        } else if (stage == 1) {
            /*После того как заджойнился или создал игру*/
            (findViewById(R.id.button_create_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_join_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton)).setVisibility(View.VISIBLE);
            (findViewById(R.id.imageButton2)).setVisibility(View.VISIBLE);
            (findViewById(R.id.textViewCode)).setVisibility(View.VISIBLE);
            (findViewById(R.id.button_go)).setVisibility(View.VISIBLE);

            // go button set second type
            configGoButton();

        } else if (stage == 2) {
            /*В игре*/
            (findViewById(R.id.button_create_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_join_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton2)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.textViewCode)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_go)).setVisibility(View.VISIBLE);

            // go button set second type
            configGoCancelButton();
        }
    }

    private void configGoCancelButton() {
        Button btn = findViewById(R.id.button_go);
        btn.setBackgroundColor(0x55FF0000);
        btn.setText("Закончить игру");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: алерт на подтверждение
                // Убить текущую запущенную игру
                killRunningGameCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        stageHandler(0);
                        mTimer.cancel();
                        for (MapObject mapObject : coinspositions) {
                            mMap.getMapObjects().remove(mapObject);
                        }
                        coinspositions.clear();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError --> killRunningGameCallbacks");
                        Toast.makeText(FriendsModeActivity.this,
                                "У нас проблемы))",
                                Toast.LENGTH_SHORT).show();
                    }
                };

                connectionServer.initKillRunGame(
                        LoggedInUser.getName(),
                        inviteString
                );
                connectionServer.connectSimple(killRunningGameCallbacks);

            }
        });
    }

    private void drawCoins(List<PointsResponse> points) {
        int translucentRed = 0x55FF0000;
        counterCoins = 0;
        for (MapObject obj : coinspositions) {
            mMap.getMapObjects().remove(obj);
        }
        coinspositions.clear();

        for (PointsResponse point : points) {
            coinspositions.add(
                    mMap.getMapObjects().addCircle(
                            new Circle(
                                    new Point(
                                            point.getLatitude(),
                                            point.getLongitude()
                                    ), GETTING_RADIUS - 3
                            ),
                            Color.RED,
                            3,
                            translucentRed
                    )
            );

            IconStyle iconStyle = new IconStyle();
            iconStyle.setFlat(true);
            iconStyle.setVisible(true);
            ImageProvider imageProvider = new ImageProvider() {
                @Override
                public String getId() {
                    counterCoins++;
                    return String.format("coins#%s", counterCoins);
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

            coinspositions.add(
                    mMap.getMapObjects().addPlacemark(
                            new Point(
                                    point.getLatitude(),
                                    point.getLongitude()
                            ),
                            imageProvider,
                            iconStyle
                    )
            );

        }
    }

    private void configGoButton() {
        Button btn = findViewById(R.id.button_go);
        btn.setBackgroundColor(0xff0099cc); // in colors_holo.xml holo_blue_dark
        btn.setText("I'm ready to start"); // todo: already in strings.xml
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        Log.i(TAG, String.format("value in configGoButton: %s", value));
                        switch (value) {
                            case "2":
                                Toast.makeText(FriendsModeActivity.this,
                                        "begin_game :: 2 (param is invalid)",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case "3":
                                Toast.makeText(FriendsModeActivity.this,
                                        "begin_game :: 3 (game there is not)",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case "4":
                                Toast.makeText(FriendsModeActivity.this,
                                        "begin_game :: 4 (access denied)",
                                        //начать может только создатель ссылки
                                        Toast.LENGTH_SHORT).show();
                                return;
                        }
                        if (!value.equals("1"))
                            Log.i(TAG, String.format("value in goCallbacks: %s", value));
                        else
                            stageHandler(2); // единственный успешный вариант
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError --> goCallbacks");
                    }
                };

                int time = Integer.parseInt( ((EditText)findViewById(R.id.editText2)).getText().toString());

                connectionServer.initBeginGame(
                        LoggedInUser.getName(),
                        inviteString,
                        time // todo: надо бы где то это менять, не? to @Asya
                );
                connectionServer.connectSimple(goCallbacks);

            }
        });

    }

    private void configMap() {
        Log.i(TAG, "configMap");
        Location now = UserLocation.imHere;
        mMap.move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 18.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

//        mMap.getUserLocationLayer().setEnabled(true);

    }

    private void configJoinGame() {
        Button btn = findViewById(R.id.button_join_game);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = openSettingsDialog();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        final AlertDialog alertDialog = (AlertDialog) dialog;
                        alertDialog.findViewById(R.id.button_paste).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = clipboardManager.getPrimaryClip();
                                int itemCount = clipData.getItemCount();

                                if (itemCount > 0) {
                                    // Get source text.
                                    ClipData.Item item = clipData.getItemAt(0);
                                    String text = item.getText().toString();

                                    // Set the text to target textview.
                                    ((EditText) alertDialog.findViewById(R.id.link_invite)).setText(text);
                                }
                            }
                        });
                    }
                });
                dialog.show();
                //stop!
            }
        });

    }

    private Dialog openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.layout_join, null))
                // todo: transfer to string.xml ("Join")
                // todo: transfer to string.xml ("Cancel")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        AlertDialog alertDialog = (AlertDialog) dialog;
                        EditText linkInvite = alertDialog.findViewById(R.id.link_invite);
                        if (linkInvite == null) {
                            Log.i(TAG, "CATCH NULL POINTER EXCEPTION #1");
                            return;
                        }
                        inviteString = linkInvite.getText().toString();

                        initCallbacks = new SimpleCallbacks() {
                            @Override
                            public void onSuccess(@NonNull String value) {
                                switch (value) {
                                    case "2":
                                        Toast.makeText(FriendsModeActivity.this,
                                                "query is incorrect",
                                                //todo: этого никогда не должно произойти
                                                // но если случился такой тост то либо
                                                // изменилось что-то на сервере либо на клиенте
                                                // в любом случае надо сообщить об этом куда то
                                                // чтобы разработчики знали что это произошло
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    case "3":
                                        Toast.makeText(FriendsModeActivity.this,
                                                "Ошибка аутентификации", // todo: transfer to string.xml
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    case "4":
                                        Toast.makeText(FriendsModeActivity.this,
                                                "Ссылка некорректна", // todo: transfer to string.xml
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    case "5":
                                        Toast.makeText(FriendsModeActivity.this,
                                                "Игра уже началась", // todo: transfer to string.xml
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                }
                                ((TextView) findViewById(R.id.textViewCode)).setText(value);
                                inviteString = ((TextView) findViewById(R.id.textViewCode))
                                        .getText().toString();
                                mainGameLoop();
                                stageHandler(1);
                            }

                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                Toast.makeText(FriendsModeActivity.this,
                                        "problem with internet", // todo: transfer to string.xml
                                        Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "onError --> joinButton");
                            }
                        };
                        connectionServer.initJoinGame(
                                LoggedInUser.getName(),
                                UserLocation.imHere.getLatitude(),
                                UserLocation.imHere.getLongitude(),
                                inviteString
                        );
                        connectionServer.connectSimple(initCallbacks);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing to say, just exit
                    }
                });
        return builder.create();
    }

    private void configCreateMode() {
        Button btn = findViewById(R.id.button_create_game);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choise = 1;
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
                        } else if (value.equals("3")) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "Ошибка аутентификации", // todo: transfer to string.xml
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ((TextView) findViewById(R.id.textViewCode)).setText(value);
                        inviteString = ((TextView) findViewById(R.id.textViewCode))
                                .getText().toString();
                        mainGameLoop();
                        stageHandler(1);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Toast.makeText(FriendsModeActivity.this,
                                "problem with internet", // todo: transfer to string.xml
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onError --> configCreateMode");
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

        final UpdateStateCallbacks gameStateCallback = new UpdateStateCallbacks() {
            @Override
            public void gamersUpdate(@NonNull List<GamersResponse> gamers) {

                for (MapObject circle : lastPlayersPositions) {
                    mMap.getMapObjects().remove(circle);
                }
                lastPlayersPositions.clear();

                for (final GamersResponse gamer : gamers) {
                    // сервер не выдаст большое число
                    gamer.setColor(0xFF000000 + gamer.getColor());

                    // todo: рисовать gamerов подругому!!!
                    lastPlayersPositions.add(
                            mMap.getMapObjects().addCircle(
                                    new Circle(
                                            new Point(
                                                    gamer.getLatitude(),
                                                    gamer.getLongitude()
                                            ),
                                            5
                                    ),
                                    gamer.getColor(),
                                    15,
                                    gamer.getColor()
                            )
                    );

                    IconStyle iconStyle = new IconStyle();
                    iconStyle.setFlat(true);
                    iconStyle.setVisible(true);
                    ImageProvider imageProvider = new ImageProvider() {
                        @Override
                        public String getId() {
                            return gamer.getName();
                        }

                        @Override
                        public Bitmap getImage() {
                            Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                            bitmap.eraseColor(Color.TRANSPARENT);
                            for (int i = 0; i < 10; i++) {
                                for (int j = 0; j < 10; j++) {
                                    if ((i - 5) * (i - 5) + (j - 5) * (j - 5) <= 25) {
                                        bitmap.setPixel(i, j, gamer.getColor());
                                    }
                                }
                            }
                            return bitmap;
                        }
                    };

                    lastPlayersPositions.add(
                            mMap.getMapObjects().addPlacemark(
                                    new Point(
                                            gamer.getLatitude(),
                                            gamer.getLongitude()
                                    ),
                                    imageProvider,
                                    iconStyle
                            )
                    );
                }
            }

            @Override
            public void coinsUpdate(@NonNull List<PointsResponse> coins) {
                Log.i(TAG, "checkCoins is true (2)");
                drawCoins(coins);
            }

            @Override
            public void gameOver(@NonNull StatisticsResponse stats) {
                Toast.makeText(FriendsModeActivity.this,
                        String.format("You get %s coins and %s rating, great!",
                                stats.getCoins(), stats.getRating()),
                        Toast.LENGTH_LONG).show();
                stageHandler(0);
                mTimer.cancel();
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
                connectionServer.connectUpdateState(gameStateCallback);
//                connectionServer.connectPoints(gameCallbacks);
            }
        };

        mTimer.schedule(timerTask, 1000, 1000);
    }
}
