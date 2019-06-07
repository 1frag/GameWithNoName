package com.example.gamewithnoname;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.PointsCallbacks;
import com.example.gamewithnoname.ServerConnection.SimpleCallbacks;
import com.example.gamewithnoname.data.model.LoggedInUser;

import java.util.Timer;
import java.util.TimerTask;

public class FriendsModeActivity extends Activity {

    private Timer mTimer;
    private PointsCallbacks gameCallbacks;
    private SimpleCallbacks initCallbacks;
    private ConnectionServer connectionServer;
    private String inviteString;
    private int choise = 0;
    private Integer resultServerCallbacks = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_mode);

//        mainGameLoop(); todo: put it after:
        connectionServer = new ConnectionServer();
        configCreateMode();
        configJoinGame();

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
                        ((EditText)findViewById(R.id.editTextCode)).getText().toString()
                );
                connectionServer.connect(initCallbacks);
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
                connectionServer.connect(initCallbacks);
            }
        });

    }

    public void mainGameLoop() {

        mTimer = new Timer();

        gameCallbacks = new PointsCallbacks() {
            @Override
            public void onSuccess(@NonNull String value) {

                resultServerCallbacks = Integer.parseInt(value);

                FriendsModeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MainActivity.this,
//                                resultServerCallbacks.toString(),
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                FriendsModeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FriendsModeActivity.this,
                                R.string.main_activity_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectionServer.initCreateGame(
                        LoggedInUser.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude()
                );
                connectionServer.connect(gameCallbacks);
            }
        };

        mTimer.schedule(timerTask, 1000, 1000);
    }

}
