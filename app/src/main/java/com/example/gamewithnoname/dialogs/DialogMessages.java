package com.example.gamewithnoname.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.GetMessagesCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.models.User;
import com.example.gamewithnoname.models.responses.MessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DialogMessages implements Dialog.OnShowListener, Dialog.OnCancelListener {

    private Dialog dialog;
    private Timer mTimer;
    private TimerTask task;
    private int first_time;
    private static final String TAG = "HITS/DialogMessages";

    @Override
    public void onShow(final DialogInterface dialogInterface) {

        dialog = (Dialog) dialogInterface;
        dialog.setContentView(R.layout.layout_messages);

        configBtnSend();
        mTimer = new Timer();
        initTimerTask();
        first_time = 1;
        mTimer.schedule(task, 0, 1000);
    }

    private void initTimerTask() {
        task = new TimerTask() {
            @Override
            public void run() {
                ArrayList<View> viewsToDisable = null;
                ConnectionServer.getInstance().initGetNewMessages(
                        User.getName(),
                        first_time,
                        viewsToDisable
                );
                first_time = 0;
                ConnectionServer.getInstance().connectGetMessages(new GetMessagesCallbacks() {
                    @Override
                    public void success(List<MessageResponse> messages) {
//                        Log.i(TAG, "success");
                        Log.i(TAG, "get in");
                        addMessages(messages);
                    }

                    @Override
                    public void problem(int value) {
                        Log.i(TAG, String.format("problem %s", value));
                    }
                }, viewsToDisable);
            }
        };
    }

    private void addMessages(List<MessageResponse> messages) {
        LinearLayout linearLayout = dialog.findViewById(R.id.layout_for_messages);
        for (final MessageResponse message : messages) {
            Log.i(TAG, "mes add");
            LinearLayout newView = new LinearLayout(
                    dialog.getContext());
            dialog.getLayoutInflater().inflate(
                    R.layout.layout_multi_message,
                    newView);
            linearLayout.addView(newView);
            ((TextView) newView.findViewById(R.id.textMessage)).setText(message.getText());
            newView.findViewById(R.id.imageWriter).setBackgroundColor(message.getColor() + 0xff000000);
            newView.findViewById(R.id.imageWriter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(dialog.getContext(),
                            String.format("Это написал %s", message.getFrom()),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void configBtnSend() {
        Button btnSend = dialog.findViewById(R.id.buttonSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "qwe");
                EditText editText = dialog.findViewById(R.id.writeMessage);
                String text = editText.getText().toString();
                editText.setText("");
                ArrayList<View> viewsToDisable = null;

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
                                Toast.makeText(dialog.getContext(),
                                        "You message is sended successful",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someProblem(int code) {
                                // todo: звук сообщение не ушло или просто сказать как-то
                                //  человеку что проблемка и его сообщение не получил никто =(
                                Log.i(TAG, String.format("some problem %s", code));
                                Toast.makeText(dialog.getContext(),
                                        String.format("problem is %s", code),
                                        Toast.LENGTH_LONG).show();
                            }
                        }, viewsToDisable
                );

            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mTimer.cancel();
        mTimer.purge();
    }
}
