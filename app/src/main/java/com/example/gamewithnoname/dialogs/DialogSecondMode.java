package com.example.gamewithnoname.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.activities.FriendsModeActivity;
import com.example.gamewithnoname.callbacks.SimpleCallbacks;
import com.example.gamewithnoname.models.LoggedInUser;
import com.example.gamewithnoname.utils.UserLocation;

import static com.example.gamewithnoname.utils.Constants.CREATOR;
import static com.example.gamewithnoname.utils.Constants.JOINER;
import static com.example.gamewithnoname.utils.Constants.WAIT_GAME;
import static java.lang.Integer.parseInt;

public class DialogSecondMode implements Dialog.OnShowListener {

    private final static String TAG = "HITS/DialogSecondMode";
    private Dialog dialog;
    private int time;
    // todo: there is strings for transfer to string.xml

    @Override
    public void onShow(DialogInterface dialogInterface) {

        Log.i(TAG, "onShow");
        dialog = (Dialog) dialogInterface;
        dialog.setContentView(R.layout.alert_second_mode);

        configBtnCreateGame();
        configBtnJoinGame();

    }

    private void configBtnJoinGame() {
        Button btn = dialog.findViewById(R.id.btn_join);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(dialog.getContext(),
                        FriendsModeActivity.class);
                SimpleCallbacks initCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        switch (value) {
                            case "2":
                                Toast.makeText(dialog.getContext(),
                                        "query is incorrect",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case "3":
                                Toast.makeText(dialog.getContext(),
                                        "Ошибка аутентификации",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case "4":
                                Toast.makeText(dialog.getContext(),
                                        "Ссылка некорректна",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            case "5":
                                Toast.makeText(dialog.getContext(),
                                        "Игра уже началась",
                                        Toast.LENGTH_SHORT).show();
                                return;
                        }
                        intent.putExtra("stage", WAIT_GAME);
                        intent.putExtra("type", JOINER);
                        dialog.getContext().startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Toast.makeText(dialog.getContext(),
                                "problem with internet",
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onError --> joinButton");
                    }
                };
                ConnectionServer.getInstance().initJoinGame(
                        LoggedInUser.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude(),
                        ((EditText) dialog.findViewById(R.id.editText3)).getText().toString()
                );
                ConnectionServer.getInstance().connectSimple(initCallbacks);
            }
        });
    }

    private void configBtnCreateGame() {
        Button btn = dialog.findViewById(R.id.btn_create);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    EditText editText = dialog.findViewById(R.id.text_alert_2m);
                    String s = editText.getText().toString();
                    time = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    Toast.makeText(dialog.getContext(),
                            "Input valid time!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                final Intent intent = new Intent(dialog.getContext(),
                        FriendsModeActivity.class);
                SimpleCallbacks initCallbacks = new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        if (value.equals("2")) {
                            Toast.makeText(dialog.getContext(),
                                    "2. Query is incorrect",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else if (value.equals("3")) {
                            Toast.makeText(dialog.getContext(),
                                    "Ошибка аутентификации",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent.putExtra("stage", WAIT_GAME);
                        intent.putExtra("type", CREATOR);
                        dialog.getContext().startActivity(intent);
                        dialog.cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Toast.makeText(dialog.getContext(),
                                "problem with internet",
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onError --> configCreateMode");
                    }
                };

                RadioButton radioButton = dialog.findViewById(R.id.radio_button_in_team);
                int type = (radioButton.isChecked() ? 1 : 2);

                ConnectionServer.getInstance().initCreateGame(
                        LoggedInUser.getName(),
                        UserLocation.imHere.getLatitude(),
                        UserLocation.imHere.getLongitude(),
                        time,
                        type
                );
                ConnectionServer.getInstance().connectSimple(initCallbacks);

            }
        });

    }
}
