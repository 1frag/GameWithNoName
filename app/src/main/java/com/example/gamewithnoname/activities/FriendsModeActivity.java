package com.example.gamewithnoname.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.dialogs.DialogMessages;
import com.example.gamewithnoname.utils.UserLocation;
import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.callbacks.SimpleCallbacks;
import com.example.gamewithnoname.models.responses.StatisticsResponse;
import com.example.gamewithnoname.callbacks.UpdateStateCallbacks;
import com.example.gamewithnoname.models.LoggedInUser;
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

import static com.example.gamewithnoname.utils.Constants.CREATOR;
import static com.example.gamewithnoname.utils.Constants.WAIT_GAME;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;

    private SimpleCallbacks initCallbacks;
    private SimpleCallbacks goCallbacks;
    private SimpleCallbacks killRunningGameCallbacks;

    private MapView mapView;
    private Map mMap;
    private ArrayList<Gamer> dataLegend;
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
    private BottomSheetDialog dialog;

    class Gamer {
        String name;
        Integer color;

        Gamer(String name, Integer color) {
            this.name = name;
            this.color = color;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_mode);

        mapView = findViewById(R.id.mapViewFrMode);
        mMap = mapView.getMap();
        configMap();

        if (getIntent().getExtras() == null) {
            stageHandler(0);
            return;
        }

        int type = getIntent().getExtras().getInt("type"); // creator or joiner
        int stage = getIntent().getExtras().getInt("stage"); // run or wait
        mainGameLoop();
        if (stage == WAIT_GAME)
            stageHandler(1);
        else /* PLAY_GAME **/
            stageHandler(2);
        buildOwn(type);
    }

    private void buildOwn(int type) {
        if (type == CREATOR) {
            // todo: есть кнопочка запустить игру
        } else /* JOINER **/ {
            // todo: нет этой кнопки и прочее
        }
    }

    private void stageHandler(int stage) {
        if (stage == 0) {
            Intent onBack = new Intent(this, MainActivity.class);
            finish();
            startActivity(onBack);

        } else if (stage == 1) {
            /*После того как заджойнился или создал игру*/
            (findViewById(R.id.button_create_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_join_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton)).setVisibility(View.VISIBLE);
            (findViewById(R.id.imageButton2)).setVisibility(View.VISIBLE);
            (findViewById(R.id.text_view_code)).setVisibility(View.VISIBLE);
            (findViewById(R.id.button_go)).setEnabled(true);

            // go button set second type
            configGoButton();

        } else if (stage == 2) {
            /*В игре*/
            (findViewById(R.id.button_create_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_join_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton2)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_go)).setVisibility(View.VISIBLE);

            // go button set second type
            configGoCancelButton();
        }
    }

    private void configGoCancelButton() {
        ImageButton btn = findViewById(R.id.button_go);
        btn.setBackgroundColor(0x55FF0000);
//        btn.setText("Закончить игру");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: алерт на подтверждение
                // Убить текущую запущенную игру
                killRunningGameCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        stageHandler(0);
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer.purge();
                        }
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

                ConnectionServer.getInstance().initKillRunGame(
                        LoggedInUser.getName()
                );
                ConnectionServer.getInstance().connectSimple(killRunningGameCallbacks);

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
        ImageButton btn = findViewById(R.id.button_go);
        btn.setBackgroundColor(0xff0099cc); // in colors_holo.xml holo_blue_dark
//        btn.setText("I'm ready to start"); // todo: already in strings.xml
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

                int time = Integer.parseInt(((EditText) findViewById(R.id.editText2)).getText().toString());

                ConnectionServer.getInstance().initBeginGame(
                        LoggedInUser.getName()
                );
                ConnectionServer.getInstance().connectSimple(goCallbacks);

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

    public void mainGameLoop() {

        mTimer = new Timer();

        final UpdateStateCallbacks gameStateCallback = new UpdateStateCallbacks() {
            @Override
            public void gamersUpdate(@NonNull List<GamersResponse> gamers) {

                dataLegend = new ArrayList<>();
                for (GamersResponse player : gamers) {
                    dataLegend.add(new Gamer(
                            player.getName(),
                            0xFF000000 + player.getColor()
                    ));
                }

                for (MapObject circle : lastPlayersPositions) {
                    mMap.getMapObjects().remove(circle);
                }
                lastPlayersPositions.clear();

                for (final GamersResponse gamer : gamers) {
                    // сервер не выдаст большое число
                    gamer.setColor(0xFF000000 + gamer.getColor());

                    // todo: рисовать gamerов по-другому!!!
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
                mTimer.purge();
            }

            @Override
            public void updateLink(@NonNull String link) {
                ((TextView)findViewById(R.id.text_view_code)).setText(link);
            }
        };

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ConnectionServer.getInstance().initUpdateMap(
                        LoggedInUser.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude()
                );
                ConnectionServer.getInstance().connectUpdateState(gameStateCallback);
//                connectionServer.connectPoints(gameCallbacks);
            }
        };

        mTimer.schedule(timerTask, 1000, 1000);
    }

    public void legendDialog(View view) {
        Dialog dialog = openLegendDialog();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // show some information
                AlertDialog alertDialog = (AlertDialog) dialog;
                LinearLayout linearLayout = alertDialog.findViewById(R.id.layout_for_add);

                if (dataLegend != null) {
                    for (Gamer gamer : dataLegend) {
                        LinearLayout newView = new LinearLayout(
                                FriendsModeActivity.this);
                        getLayoutInflater().inflate(
                                R.layout.layout_multi_name,
                                newView);
                        linearLayout.addView(newView);
                        ((TextView) newView.findViewById(R.id.textView)).setText(gamer.name);
                        newView.findViewById(R.id.imageViewLegend).setBackgroundColor(gamer.color);
                    }
                }
            }
        });
        dialog.show();
    }

    public void openMessages(View view) {

        dialog = new BottomSheetDialog(this);

        // to refer view in layout_messages:
        // sheetView.findViewById(R.id.some_id)

        dialog.setOnShowListener(new DialogMessages());

        dialog.show();
    }

    private Dialog openLegendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.layout_multi_people, null))
                .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing to do
                    }
                });
        return builder.create();
    }

}
