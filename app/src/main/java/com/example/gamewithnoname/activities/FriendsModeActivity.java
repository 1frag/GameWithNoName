package com.example.gamewithnoname.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.BeginGameCallbacks;
import com.example.gamewithnoname.callbacks.KickPlayerCallbacks;
import com.example.gamewithnoname.callbacks.KillRGCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.models.responses.MessageResponse;
import com.example.gamewithnoname.utils.UserLocation;
import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.models.responses.StatisticsResponse;
import com.example.gamewithnoname.callbacks.UpdateStateCallbacks;
import com.example.gamewithnoname.models.User;
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
import static com.example.gamewithnoname.utils.Constants.JOINER;
import static com.example.gamewithnoname.utils.Constants.WAIT_GAME;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;

    private MapView mapView;
    private CurCntData datas;
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
    private int own;
    private int stage;
    private int type_game;

    class Gamer {
        String name;
        Integer color;

        Gamer(String name, Integer color) {
            this.name = name;
            this.color = color;
        }
    }

    class CurCntData {
        Integer mMessages;
        Integer mCoins;

        CurCntData() {
            mMessages = 0;
            mCoins = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_mode);

        datas = new CurCntData();
        mapView = findViewById(R.id.mapViewFrMode);
        mMap = mapView.getMap();
        configMap();

        if (getIntent().getExtras() == null) {
            stageHandler(0);
            return;
        }

        configBtnSend();
        own = getIntent().getExtras().getInt("own"); // creator or joiner
        stage = getIntent().getExtras().getInt("stage"); // run or wait
        mainGameLoop();
        if (stage == WAIT_GAME)
            stageHandler(1);
        else /* PLAY_GAME **/
            stageHandler(2);
        configExitButton();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
        mTimer.purge();
    }

    private void buildOwn(int type) {
        if (own == CREATOR && stage == 2) {
            (findViewById(R.id.floatingAdmButton)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.floatingAdmButton)).setVisibility(View.INVISIBLE);
        }
    }

    private void stageHandler(int rstage) {
        this.stage = rstage;
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
            (findViewById(R.id.image_button_exit)).setEnabled(true);

            // go button set second own
            configGoButton();

        } else if (stage == 2) {
            /*В игре*/
            (findViewById(R.id.button_create_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_join_game)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageButton2)).setVisibility(View.INVISIBLE);
//            (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.floatingAdmButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.image_button_exit)).setVisibility(View.VISIBLE);

        }
    }

    private void configExitButton() {
        ImageButton btn = findViewById(R.id.image_button_exit);
        btn.setEnabled(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Убить текущую запущенную игру
                areYouSureAlert();
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
                    ;
                    return "coin";
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
        ImageButton btn = findViewById(R.id.floatingAdmButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // begin game

                BeginGameCallbacks callback = new BeginGameCallbacks() {
                    @Override
                    public void youAreNotAuthor() {
                        Toast.makeText(FriendsModeActivity.this,
                                "только автор может начать игру!!!",
                                //начать может только создатель ссылки
                                Toast.LENGTH_SHORT).show();
                        // это не должно произойти
                    }

                    @Override
                    public void notEnoughMan() {
                        Toast.makeText(FriendsModeActivity.this,
                                "Недостаточно людей для начала игры",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void success() {
                        stageHandler(2);
                    }

                    @Override
                    public void someProblem(Throwable t) {
                        Log.i(TAG, t.getMessage());
                        Toast.makeText(FriendsModeActivity.this,
                                t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                };

                ConnectionServer.getInstance().initBeginGame(
                        User.getName()
                );
                ConnectionServer.getInstance().connectBeginGame(callback);

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
            public void gamersUpdate(List<GamersResponse> gamers) {

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
            public void coinsUpdate(List<PointsResponse> coins) {
                Log.i(TAG, "checkCoins is true (2)");
                drawCoins(coins);
                datas.mCoins = coins.size();
            }

            @Override
            public void messagesUpdate(List<MessageResponse> messages) {
                Log.i(TAG, String.format("size %s", messages.size()));
                addMessages(messages);
                datas.mMessages += messages.size();
            }

            @Override
            public void gameOver(StatisticsResponse stats) {
                Toast.makeText(FriendsModeActivity.this,
                        String.format("You get %s coins and %s rating, great!",
                                stats.getCoins(), stats.getRating()),
                        Toast.LENGTH_LONG).show();
                stageHandler(0);
                mTimer.cancel();
                mTimer.purge();
            }

            @Override
            public void linkUpdate(String link) {
                ((TextView) findViewById(R.id.text_view_code)).setText(link);
            }

            @Override
            public void changeOwn(Boolean isAuthor) {
                if (isAuthor)
                    own = CREATOR;
                else
                    own = JOINER;
                buildOwn(own);
            }

            @Override
            public void changeProgress(Integer progress) {
                Log.i(TAG, String.format("stage is %s, but progress is %s", stage, progress));
                stage = progress + 1;
                stageHandler(stage);
            }

            @Override
            public void changeTypeGame(Integer type) {
                Log.i(TAG, String.format("type is %s", type));
                if (type_game != type) {
                    type_game = type;
                }
            }

            @Override
            public void changeTimer(Integer time) {
                ((TextView)findViewById(R.id.text_view_code)).setText(time.toString());
            }
        };

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ConnectionServer.getInstance().initUpdateMap(
                        User.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude(),
                        datas.mMessages,
                        datas.mCoins
                );
                ConnectionServer.getInstance().connectUpdateState(gameStateCallback);
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
                        final Integer color = gamer.color;
                        final String targetName = gamer.name;
                        final TextView textView = newView.findViewById(R.id.textView);
                        final View view = newView.findViewById(R.id.imageViewLegend);
                        final ImageButton kickPlayer = newView.findViewById(R.id.imageButtonKickPlayer);
                        textView.setText(gamer.name);
                        view.setBackgroundColor(gamer.color);

                        if (type_game == 0 && own == CREATOR) {

                            kickPlayer.setVisibility(View.VISIBLE);
                            kickPlayer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final KickPlayerCallbacks callback = new KickPlayerCallbacks() {
                                        @Override
                                        public void success() {
                                            textView.setEnabled(false);
                                            kickPlayer.setEnabled(false);
                                            view.setBackgroundColor(color - 0xcc000000);

                                            Toast.makeText(FriendsModeActivity.this,
                                                    String.format("Пользователь %s был успешно отстранён из игры", targetName),
                                                    Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void someProblem(Throwable t) {
                                            Log.i(TAG, t.getMessage());
                                        }
                                    };

                                    ConnectionServer.getInstance().initKickPlayer(targetName);
                                    ConnectionServer.getInstance().connectKickPlayer(callback);
                                }
                            });

                        } else {
                            Log.i(TAG, String.format("%s | %s", type_game, own));
                            kickPlayer.setVisibility(View.INVISIBLE);
                            kickPlayer.setOnClickListener(null);

                        }
                    }
                }
            }
        });
        dialog.show();
    }

    private void addMessages(List<MessageResponse> messages) {
        LinearLayout linearLayout = findViewById(R.id.layout_for_messages);
        for (final MessageResponse message : messages) {
            Log.i(TAG, "mes add");
            LinearLayout newView = new LinearLayout(
                    FriendsModeActivity.this);
            getLayoutInflater().inflate(
                    R.layout.layout_multi_message,
                    newView);
            linearLayout.addView(newView);
            ((TextView) newView.findViewById(R.id.textMessage)).setText(message.getText());
            newView.findViewById(R.id.imageWriter).setBackgroundColor(message.getColor() + 0xff000000);
            newView.findViewById(R.id.imageWriter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FriendsModeActivity.this,
                            String.format("Это написал %s", message.getFrom()),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void configBtnSend() {
        ImageButton btnSend = findViewById(R.id.buttonSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "qwe");
                EditText editText = findViewById(R.id.writeMessage);
                String text = editText.getText().toString();
                editText.setText("");

                ConnectionServer.getInstance().initSendMessage(
                        User.getName(),
                        text
                );

                ConnectionServer.getInstance().connectSendMessage(
                        new SendMessageCallbacks() {
                            @Override
                            public void sended() {
                                // todo: звук сообщение отправлено
                                Toast.makeText(FriendsModeActivity.this,
                                        "You message is sended successful",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someProblem(int code) {
                                // todo: звук сообщение не ушло или просто сказать как-то
                                //  человеку что проблемка и его сообщение не получил никто =(
                                Log.i(TAG, String.format("some problem %s", code));
                                Toast.makeText(FriendsModeActivity.this,
                                        String.format("problem is %s", code),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                );

            }
        });
    }

    public void areYouSureAlert() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_2m_finish_game))
                .setMessage(getString(R.string.alert_2m_finish_game_text))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        KillRGCallbacks krgCallback = new KillRGCallbacks() {
                            @Override
                            public void success() {
                                for (MapObject mapObject : coinspositions) {
                                    mMap.getMapObjects().remove(mapObject);
                                }
                                coinspositions.clear();
                                if (mTimer != null) {
                                    mTimer.cancel();
                                    mTimer.purge();
                                }
                                stageHandler(0);
                            }

                            @Override
                            public void someProblem(Throwable t) {
                                Log.i(TAG, t.getMessage());
                                Toast.makeText(FriendsModeActivity.this,
                                        "У нас проблемы))",
                                        Toast.LENGTH_SHORT).show();
                            }
                        };

                        ConnectionServer.getInstance().initKillRunGame(
                                User.getName()
                        );
                        ConnectionServer.getInstance().connectKillRG(krgCallback);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @SuppressLint("ResourceType")
    public void openMessages(View view) {

//        dialog = new BottomSheetDialog(this);
//        dialog.setOnShowListener(new DialogMessages());
//        dialog.show();
        // to refer view in layout_messages:
        // sheetView.findViewById(R.id.some_id)
        ConstraintLayout chat = findViewById(R.id.include);
        if (chat.getVisibility() == View.VISIBLE) {
            closeMessages(chat);
        } else {
            (findViewById(R.id.floatingAdmButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_multi_chat)).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            chat.setVisibility(View.VISIBLE);
        }
    }

    public void closeMessages(View view) {
        findViewById(R.id.include).setVisibility(View.INVISIBLE);
        (findViewById(R.id.button_multi_chat)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (!findViewById(R.id.image_button_exit).isClickable()) {
            (findViewById(R.id.text_view_code)).setVisibility(View.VISIBLE);
            if (own == CREATOR) {
                (findViewById(R.id.floatingAdmButton)).setVisibility(View.VISIBLE);
            }
        }
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
