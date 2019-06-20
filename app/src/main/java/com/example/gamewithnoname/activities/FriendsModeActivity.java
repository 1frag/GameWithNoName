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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.BeginGameCallbacks;
import com.example.gamewithnoname.callbacks.ChangeCoinsCallbacks;
import com.example.gamewithnoname.callbacks.KickPlayerCallbacks;
import com.example.gamewithnoname.callbacks.KillRGCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.dialogs.DialogMessages;
import com.example.gamewithnoname.dialogs.DialogSecondMode;
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

import static com.example.gamewithnoname.R.layout.layout_write_time;
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
    private int own;
    private int stage;
    private int type_game;

    private int anotherRadius;
    private TextView textAnotherRadius;

    private Integer changedTime = 0;

    private boolean chat_is_show = false;

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

        anotherRadius = 1;

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
        if (own == CREATOR && stage == 1) {
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
//            (findViewById(R.id.floatingAdmButton)).setVisibility(View.VISIBLE);
            if (!chat_is_show)
                (findViewById(R.id.text_view_code)).setVisibility(View.VISIBLE);
            else
                (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.image_button_exit)).setEnabled(true);

            // go button set second own
            // configGoButton();

        } else if (stage == 2) {
            /*В игре*/
            (findViewById(R.id.button_create_game)).setVisibility(View.INVISIBLE);
            if (!chat_is_show)
                (findViewById(R.id.text_view_code)).setVisibility(View.VISIBLE);
            else
                (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
//            (findViewById(R.id.floatingAdmButton)).setVisibility(View.INVISIBLE);
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
        int translucentYellow = 0x55FFFF00;
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
                            Color.YELLOW,
                            3,
                            translucentYellow
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
                                bitmap.setPixel(i, j, 0xFFffee00);
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

    public void initGame(final View view) {
        ConnectionServer.getInstance().initInitGame(User.getName(), null);
        ConnectionServer.getInstance().connectInitGame(new BeginGameCallbacks() {
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
            public void success(int minutes) {
                changeTime(view, minutes);
            }

            @Override
            public void someProblem(Throwable t) {
                Log.i(TAG, t.getMessage());
                Toast.makeText(FriendsModeActivity.this,
                        t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, null);
    }

    private void changeTime(final View view, final int minimumMinutes) {
        final Dialog dialog = createTimeDialog();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                AlertDialog dialog = (AlertDialog) dialogInterface;
                dialog.setContentView(layout_write_time);
                final EditText textTime = dialog.findViewById(R.id.editText2);
                dialog.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            changedTime = Integer.parseInt(textTime.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "Input valid time!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (changedTime < minimumMinutes) {
                            Toast.makeText(FriendsModeActivity.this,
                                    "Нужно указать больше времени!!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        onclickGoButton(view);
                    }
                });
            }
        });
        dialog.show();
    }

    private Dialog createTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsModeActivity.this);
        return builder.create();
    }

    private void onclickGoButton(View view) {
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
            public void success(int one) {
                stageHandler(2);
            }

            @Override
            public void someProblem(Throwable t) {
                Log.i(TAG, t.getMessage());
                Toast.makeText(FriendsModeActivity.this,
                        t.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        final ArrayList<View> viewsToDisable = null;
        ConnectionServer.getInstance().initBeginGame(
                User.getName(),
                changedTime,
                viewsToDisable
        );
        ConnectionServer.getInstance().connectBeginGame(callback, viewsToDisable);

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

                int translucentRed = 0x55FF0000;
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

                    /***/
                    lastPlayersPositions.add(
                            mMap.getMapObjects().addCircle(
                                    new Circle(
                                            new Point(
                                                    gamer.getLatitude(),
                                                    gamer.getLongitude()
                                            ), gamer.getRadius()
                                    ),
                                    Color.RED,
                                    1,
                                    translucentRed
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
                addMessages(messages);
                datas.mMessages += messages.size();
            }

            @Override
            public void gameOver(final StatisticsResponse stats) {
                Toast.makeText(FriendsModeActivity.this,
                        String.format("You get %s coins, great!",
                                stats.getCoins()),
                        Toast.LENGTH_LONG).show();

                final Dialog dialog = createGameOverDialog();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        dialog.setContentView(R.layout.layout_end_2m);
                        TextView textView = dialog.findViewById(R.id.textView6);
                        textView.setText(String.format(
                                getResources().getString(R.string.you_pick_n_coins),
                                stats.getCoins())
                        );
                        dialog.findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stageHandler(0);
                            }
                        });
                        dialog.setCancelable(false);
                    }
                });

                dialog.show();
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
                stage = progress + 1;
                stageHandler(stage);
            }

            @Override
            public void changeTypeGame(Integer type) {
                if (type_game != type) {
                    type_game = type;
                }
            }

            @Override
            public void changeTimer(Integer time) {
                if (time < 0) time = 0;
                TextView textView = findViewById(R.id.text_view_code);
                int sec = time % 60;
                time /= 60;
                int min = time % 60;
                time /= 60;
                int hour = time % 24;
                time /= 24;
                int days = time;
                String result;
                if (days != 0) {
                    result = String.format("%s day(s)", days);
                } else if (hour != 0) {
                    result = String.format("%s hour(s)", hour);
                } else {
                    result = String.format("%s%s:%s%s",
                            min / 10, min % 10,
                            sec / 10, sec % 10);
                }
                textView.setText(result);
            }
        };

        final TimerTask timerTask = new TimerTask() {
            final ArrayList<View> viewsToDisable = null;

            @Override
            public void run() {
                ConnectionServer.getInstance().initUpdateMap(
                        User.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude(),
                        datas.mMessages,
                        datas.mCoins,
                        viewsToDisable
                );
                ConnectionServer.getInstance().connectUpdateState(gameStateCallback, viewsToDisable);
            }
        };

        mTimer.schedule(timerTask, 1000, 1000);
    }

    private Dialog createGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsModeActivity.this);
        return builder.create();
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

                                    final ArrayList<View> viewsToDisable = null;
                                    ConnectionServer.getInstance().initKickPlayer(targetName, viewsToDisable);
                                    ConnectionServer.getInstance().connectKickPlayer(callback, viewsToDisable);
                                }
                            });

                        } else {
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
        final ArrayList<View> viewsToDisable = null;
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
                        text,
                        viewsToDisable
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
                        }, viewsToDisable
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

                        final ArrayList<View> viewsToDisable = null;
                        ConnectionServer.getInstance().initKillRunGame(
                                User.getName(),
                                viewsToDisable
                        );
                        ConnectionServer.getInstance().connectKillRG(krgCallback, viewsToDisable);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @SuppressLint("ResourceType")
    public void openMessages(View view) {
        ConstraintLayout chat = findViewById(R.id.include);
        if (chat.getVisibility() == View.VISIBLE) {
            closeMessages(chat);
            chat_is_show = false;
        } else {
            (findViewById(R.id.floatingAdmButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_multi_chat)).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            chat.setVisibility(View.VISIBLE);
            chat_is_show = true;
        }
    }

    public void closeMessages(View view) {
        findViewById(R.id.include).setVisibility(View.GONE);
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

    public void openBuyDialog(View view) {
        SeekBar seekBar = findViewById(R.id.seekBarRadius);
        seekBar.setOnSeekBarChangeListener(new radiusListener());

        ConstraintLayout extensions = findViewById(R.id.include1);
        if (extensions.getVisibility() == View.VISIBLE) {
            extensions.setVisibility(View.INVISIBLE);
        } else {
            extensions.setVisibility(View.VISIBLE);
        }
    }

    private class radiusListener implements SeekBar.OnSeekBarChangeListener {
        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            anotherRadius = progress + 1;
            textAnotherRadius = findViewById(R.id.textRadiusVal);
            textAnotherRadius.setText(String.format("%d", anotherRadius));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    public void applyExtensions(View view) {
        ConnectionServer.getInstance().initChangeRadius(User.getName(), anotherRadius, getCost(), null);
        ConnectionServer.getInstance().connectChangeRadius(new ChangeCoinsCallbacks() {
            @Override
            public void successful(int money) {

                findViewById(R.id.include1).setVisibility(View.INVISIBLE);
            }

            @Override
            public void notEnoughMoney() {
                Toast.makeText(FriendsModeActivity.this,
                        "У вас недостаточно денег",
                        Toast.LENGTH_LONG).show();
                findViewById(R.id.include1).setVisibility(View.INVISIBLE);
            }
        }, null);
    }

    private Integer getCost() {
        return (int) Math.pow(2, anotherRadius);
    }
}
