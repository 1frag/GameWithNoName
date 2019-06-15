package com.example.gamewithnoname.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.GetMessagesCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.models.LoggedInUser;
import com.example.gamewithnoname.models.responses.MessageResponse;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DialogMessages implements Dialog.OnShowListener {

    private Dialog dialog;
    private Timer mTimer;
    private TimerTask task;
    private static final String TAG = "HITS/DialogMessages";

    @Override
    public void onShow(final DialogInterface dialogInterface) {

        dialog = (Dialog) dialogInterface;
        dialog.setContentView(R.layout.layout_messages);

        configBtnSend();
        mTimer = new Timer();
        initTimerTask();
        mTimer.schedule(task, 0, 1000);
    }

    private void initTimerTask() {
        task = new TimerTask() {
            @Override
            public void run() {
                ConnectionServer.getInstance().initGetNewMessages(
                        LoggedInUser.getName()
                );

                ConnectionServer.getInstance().connectGetMessages(new GetMessagesCallbacks() {
                    @Override
                    public void success(List<MessageResponse> messages) {
                        Log.i(TAG, "success");
                    }

                    @Override
                    public void problem(int value) {
                        Log.i(TAG, String.format("problem %s", value));
                    }
                });
            }
        };
    }

    private void configBtnSend() {
        Button btnSend = dialog.findViewById(R.id.buttonSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = dialog.findViewById(R.id.writeMessage);
                String text = editText.getText().toString();
                editText.setText("");

                ConnectionServer.getInstance().initSendMessage(
                        LoggedInUser.getName(),
                        text
                );

                ConnectionServer.getInstance().connectSendMessage(
                        new SendMessageCallbacks() {
                            @Override
                            public void sended() {
                                Toast.makeText(dialog.getContext(),
                                        "You message is sended successful",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someProblem(int code) {
                                Toast.makeText(dialog.getContext(),
                                        String.format("problem is %s", code),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                );

            }
        });
    }
}
