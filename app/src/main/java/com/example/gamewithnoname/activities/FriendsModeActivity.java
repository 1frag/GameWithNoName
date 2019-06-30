package com.example.gamewithnoname.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.gamewithnoname.models.responses.MessageResponse;
import com.example.gamewithnoname.utils.UserLocation;
import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.models.responses.StatisticsResponse;
import com.example.gamewithnoname.callbacks.UpdateStateCallbacks;
import com.example.gamewithnoname.models.User;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.gamewithnoname.R.layout.layout_write_time;
import static com.example.gamewithnoname.utils.Constants.CREATOR;
import static com.example.gamewithnoname.utils.Constants.JOINER;
import static com.example.gamewithnoname.utils.Constants.WAIT_GAME;
import static java.lang.Math.pow;
import static com.example.gamewithnoname.utils.UserLocation.fusedLocationClient;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;

    private CurCntData datas;
    private Map mMap;
    private int bottomChoise = 0;
    private ArrayList<Gamer> dataLegend = new ArrayList<>();
    private ArrayList<MapObject> coinspositions = new ArrayList<>();
    private ArrayList<MapObject> lastPlayersPositions = new ArrayList<>();
    private final String TAG = String.format("%s/%s",
            "HITS", "FriendsModeActivity"
    );
    private int own;
    private int stage;
    private int type_game;

    private int radius;

    private int anotherRadius;

    private Integer changedTime = 0;

    private boolean chat_is_show = false;

    public void bottomPartClick(View view) {
        switch (view.getId()) {
            case R.id.button_multi_chat:
                changeBottomChoise(1);
                break;
            case R.id.button_multi_params:
                changeBottomChoise(2);
                break;
            case R.id.button_multi_legend:
                changeBottomChoise(3);
                break;
//                legendDialog();
            default:
                changeBottomChoise(0);
                break;
        }
    }

    class Gamer {
        String name;
        Integer color;
        Double latitude;
        Double longitude;

        Gamer(String name, Integer color, Double latitude, Double longitude) {
            this.name = name;
            this.color = color;
            this.latitude = latitude;
            this.longitude = longitude;
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        datas = new CurCntData();
        MapView mapView = findViewById(R.id.mapViewFrMode);

        UserLocation.SetUpLocationListener(this);
        mMap = mapView.getMap();
        configMap(mMap);
//        mMap.getUserLocationLayer().setEnabled(true);
        anotherRadius = 1;
        radius = 1;

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

    private void buildOwn() {
        if (own == CREATOR && stage == 1 && bottomChoise != 1) {
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
        int translucentYellow = 0x55F0C31F;
        for (MapObject obj : coinspositions) {
            mMap.getMapObjects().remove(obj);
        }
        coinspositions.clear();

        for (PointsResponse point : points) {
            int GETTING_RADIUS = 18;
            coinspositions.add(
                    mMap.getMapObjects().addCircle(
                            new Circle(
                                    new Point(
                                            point.getLatitude(),
                                            point.getLongitude()
                                    ), GETTING_RADIUS - 3
                            ),
                            Color.parseColor("#F0C31F"),
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
                    return "coin";
                }

                @Override
                public Bitmap getImage() {
                    Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                    bitmap.eraseColor(Color.TRANSPARENT);
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if ((i - 5) * (i - 5) + (j - 5) * (j - 5) <= 25) {
                                bitmap.setPixel(i, j, Color.GREEN);
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
                        getResources().getString(R.string.only_author_can_begin_game),
                        //начать может только создатель ссылки
                        Toast.LENGTH_SHORT).show();
                // это не должно произойти
            }

            @Override
            public void notEnoughMan() {
                Toast.makeText(FriendsModeActivity.this,
                        getResources().getString(R.string.not_enough_people),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(int minutes) {
                changeTime(minutes);
            }

            @Override
            public void someProblem(Throwable t) {
                Log.i(TAG, t.getMessage());
                Toast.makeText(FriendsModeActivity.this,
                        t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, null);
    }

    private void changeTime(final int minimumMinutes) {
        final Dialog dialog = createTimeDialog();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Objects.requireNonNull(dialog.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                final AlertDialog dialog = (AlertDialog) dialogInterface;
                dialog.setContentView(layout_write_time);
                final EditText textTime = dialog.findViewById(R.id.editText2);
                if (textTime == null) {
                    Log.i(TAG, "EditText textTime is null");
                    return;
                }
                Objects.requireNonNull(dialog.findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            changedTime = Integer.parseInt(textTime.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FriendsModeActivity.this,
                                    getResources().getString(R.string.input_valie_time),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.i(TAG, String.format("%s", minimumMinutes));
                        if (changedTime < minimumMinutes || changedTime < 5) {
                            Toast.makeText(FriendsModeActivity.this,
                                    getResources().getString(R.string.not_enough_time),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        changedTime *= 60;
                        onclickGoButton(dialog);

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

    private void onclickGoButton(final Dialog dialog) {
        BeginGameCallbacks callback = new BeginGameCallbacks() {
            @Override
            public void youAreNotAuthor() {
                Toast.makeText(FriendsModeActivity.this,
                        getResources().getString(R.string.only_author_can_begin_game),
                        //начать может только создатель ссылки
                        Toast.LENGTH_SHORT).show();
                // это не должно произойти
            }

            @Override
            public void notEnoughMan() {
                Toast.makeText(FriendsModeActivity.this,
                        getResources().getString(R.string.you_have_not_enough_money),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(int one) {
                stageHandler(2);
                dialog.cancel();
            }

            @Override
            public void someProblem(Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        };
        ConnectionServer.getInstance().initBeginGame(
                User.getName(),
                changedTime,
                null
        );
        ConnectionServer.getInstance().connectBeginGame(callback, null);
    }


    private void configMap(Map map) {
        Log.i(TAG, "configMap");
        Location now = UserLocation.imHere;
        map.move(
                new CameraPosition(new Point(now.getLatitude(), now.getLongitude()), 18.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        map.addInputListener(new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                changeBottomChoise(0);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                changeBottomChoise(0);
            }
        });

        mMap.getUserLocationLayer().setEnabled(true);

    }

    public void mainGameLoop() {

        mTimer = new Timer();

        final UpdateStateCallbacks gameStateCallback = new UpdateStateCallbacks() {
            @Override
            public void gamersUpdate(List<GamersResponse> gamers) {

                int translucentRed = 0x55FF0000;
                dataLegend.clear();
                for (GamersResponse player : gamers) {
                    dataLegend.add(new Gamer(
                            player.getName(),
                            0xFF000000 + player.getColor(),
                            player.getLatitude(),
                            player.getLongitude()
                    ));
                }

                for (MapObject circle : lastPlayersPositions) {
                    mMap.getMapObjects().remove(circle);
                }
                lastPlayersPositions.clear();

                for (final GamersResponse gamer : gamers) {
                    // сервер не выдаст большое число
                    if (gamer.getName().equals(User.getName())) {
                        continue;
                    }
                    gamer.setColor(0xFF000000 + gamer.getColor());

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
                        String.format(getResources().getString(R.string.you_get_coins),
                                stats.getCoins()),
                        Toast.LENGTH_LONG).show();

                final Dialog dialog = createGameOverDialog();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;

                        TextView textView = dialog.findViewById(R.id.textView6);
                        Button button = dialog.findViewById(R.id.button4);

                        if (textView == null || button == null) {
                            Log.i(TAG, "design is not loading");
                            return;
                        }
                        if (type_game == 0) {
                            textView.setText(String.format(
                                    getResources().getString(R.string.you_pick_n_coins),
                                    stats.getCoins())
                            );
                        }
                        else {
                            textView.setText(String.format(
                                    getResources().getString(R.string.you_get_rating),
                                    stats.getCoins()*10)
                            );
                        }
                        button.setOnClickListener(new View.OnClickListener() {
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
                buildOwn();
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
                    result = String.format(getResources().getString(R.string.count_days_in_timer), days);
                } else if (hour != 0) {
                    result = String.format(getResources().getString(R.string.count_hours_in_timer), hour);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserLocation.SetUpLocationListener(FriendsModeActivity.this);
                    }
                });
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

        mTimer.schedule(timerTask, 0, 1000);
    }

    private Dialog createGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsModeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_end_2m, null));
        return builder.create();
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
                            String.format(getResources().getString(R.string.author_is), message.getFrom()),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void configBtnSend() {
        final ArrayList<View> viewsToDisable = null;
        ImageButton btnSend = findViewById(R.id.buttonSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                                Toast.makeText(FriendsModeActivity.this,
//                                        getResources().getString(R.string.message_sended_successful),
//                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someProblem(int code) {
                                // todo: звук сообщение не ушло или просто сказать как-то
                                //  человеку что проблемка и его сообщение не получил никто =(
                                Log.i(TAG, String.format("some problem %s", code));
                                Toast.makeText(FriendsModeActivity.this,
                                        String.format(getResources().getString(R.string.message_is_not_sended), code),
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

    private void changeBottomChoise(int u) {
        bottomChoise = u;
        boolean messagesHide = findViewById(R.id.include).getVisibility() != View.VISIBLE;
        boolean extentionsHide = findViewById(R.id.include1).getVisibility() == View.INVISIBLE;
        boolean legendHide = findViewById(R.id.include2).getVisibility() != View.VISIBLE;
        Log.i(TAG, String.format("%s %s %s", u, messagesHide, extentionsHide));
        if (bottomChoise == 1 && messagesHide) {
            chat_is_show = false;
            openMessages(); //open
            openBuyDialog(true); //close
            openLegend(true); //close
        } else if (bottomChoise == 2 && extentionsHide) {
            closeMessages(); //close
            openBuyDialog(false); //open
            openLegend(true); //close
        } else if (bottomChoise == 3 && legendHide) {
            chat_is_show = false;
            closeMessages(); //close
            openBuyDialog(true); //close
            openLegend(false); //open
        } else {
            bottomChoise = 0;
            chat_is_show = false;
            closeMessages(); //close
            openBuyDialog(true); //close
            openLegend(true); //close
        }
    }

    @SuppressLint("ResourceType")
    private void openMessages() {

        ConstraintLayout chat = findViewById(R.id.include);
        if (chat.getVisibility() == View.VISIBLE) {
            closeMessages();
            chat_is_show = false;
        } else {
            (findViewById(R.id.floatingAdmButton)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.text_view_code)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_multi_chat)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            chat.setVisibility(View.VISIBLE);
            chat_is_show = true;
        }
    }

    public void closeMessages() {
        findViewById(R.id.include).setVisibility(View.GONE);
        (findViewById(R.id.button_multi_chat)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        ;
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
                .setPositiveButton(getResources().getString(R.string.ok_in_legend), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // nothing to do
                    }
                });
        return builder.create();
    }

    private void openBuyDialog(boolean off) {
        SeekBar seekBar = findViewById(R.id.seekBarRadius);
        seekBar.setOnSeekBarChangeListener(new RadiusListener());

        ConstraintLayout extensions = findViewById(R.id.include1);
        if (extensions.getVisibility() == View.VISIBLE || off) {
            extensions.setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_multi_params)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            extensions.setVisibility(View.VISIBLE);
            (findViewById(R.id.button_multi_params)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
    }

    private void openLegend(boolean off) {

        ConstraintLayout legends = findViewById(R.id.include2);
        LinearLayout linearLayout = findViewById(R.id.layout_for_gamers);
        linearLayout.removeAllViews();

        addAllPlayers(linearLayout);

        if (legends.getVisibility() == View.VISIBLE || off) {
            legends.setVisibility(View.INVISIBLE);
            (findViewById(R.id.button_multi_legend)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            legends.setVisibility(View.VISIBLE);
            (findViewById(R.id.button_multi_legend)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
    }

    public void closeLegend(View view) {
        ConstraintLayout legends = findViewById(R.id.include2);
        legends.setVisibility(View.INVISIBLE);
        (findViewById(R.id.button_multi_legend)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }
    private void addAllPlayers(LinearLayout linearLayout) {
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
                final Double latitude = gamer.latitude;
                final Double longitude = gamer.longitude;
                textView.setText(gamer.name);
                view.setBackgroundColor(gamer.color);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.move(
                                new CameraPosition(
                                        new Point(
                                                latitude,
                                                longitude
                                        ),
                                        20f,
                                        0.0f,
                                        0.0f),
                                new Animation(Animation.Type.SMOOTH, 0),
                                null);
                        changeBottomChoise(0);
                    }
                });

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
                                            String.format(getResources().getString(R.string.leave_user_from_game), targetName),
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

    private class RadiusListener implements SeekBar.OnSeekBarChangeListener {
        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            anotherRadius = progress + 1;
            TextView textAnotherRadius = findViewById(R.id.textRadiusVal);
            if (anotherRadius <= radius) {
                textAnotherRadius.setText(String.format(getResources().getString(R.string.new_radius), anotherRadius));
            } else {
                textAnotherRadius.setText(String.format(getResources().getString(R.string.new_radius_coins), anotherRadius, getCost()));
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    public void applyExtensions(View view) {
        if (radius >= anotherRadius) {
            Toast.makeText(this, String.format(getResources().getString(R.string.apply_extensions_less)), Toast.LENGTH_LONG).show();
        }
        ConnectionServer.getInstance().initChangeRadius(User.getName(), anotherRadius, getCost(), null);
        ConnectionServer.getInstance().connectChangeRadius(new ChangeCoinsCallbacks() {
            @Override
            public void successful(int money) {

                findViewById(R.id.include1).setVisibility(View.INVISIBLE);
                (findViewById(R.id.button_multi_params)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                radius = anotherRadius;
            }

            @Override
            public void notEnoughMoney() {
                Toast.makeText(FriendsModeActivity.this,
                        getResources().getString(R.string.you_have_not_enough_money),
                        Toast.LENGTH_LONG).show();
                findViewById(R.id.include1).setVisibility(View.INVISIBLE);
                (findViewById(R.id.button_multi_params)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            }
        }, null);
    }

    private Integer getCost() {
        return (int) (pow(anotherRadius, 2) - pow(radius, 2));
    }
}
