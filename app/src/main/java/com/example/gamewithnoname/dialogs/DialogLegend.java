package com.example.gamewithnoname.dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.activities.FriendsModeActivity;
import com.example.gamewithnoname.callbacks.KickPlayerCallbacks;

import java.util.ArrayList;

import static com.example.gamewithnoname.utils.Constants.CREATOR;

public class DialogLegend implements DialogInterface.OnShowListener{

    @Override
    public void onShow(DialogInterface dialog) {
//        AlertDialog alertDialog = (AlertDialog) dialog;
//        LinearLayout linearLayout = alertDialog.findViewById(R.id.layout_for_add);
//
//        if (dataLegend != null) {
//            for (FriendsModeActivity.Gamer gamer : dataLegend) {
//                LinearLayout newView = new LinearLayout(
//                        FriendsModeActivity.this);
//                getLayoutInflater().inflate(
//                        R.layout.layout_multi_name,
//                        newView);
//                linearLayout.addView(newView);
//                final Integer color = gamer.color;
//                final String targetName = gamer.name;
//                final TextView textView = newView.findViewById(R.id.textView);
//                final View view = newView.findViewById(R.id.imageViewLegend);
//                final ImageButton kickPlayer = newView.findViewById(R.id.imageButtonKickPlayer);
//                textView.setText(gamer.name);
//                view.setBackgroundColor(gamer.color);
//
//                if (type_game == 0 && own == CREATOR) {
//
//                    kickPlayer.setVisibility(View.VISIBLE);
//                    kickPlayer.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            final KickPlayerCallbacks callback = new KickPlayerCallbacks() {
//                                @Override
//                                public void success() {
//                                    textView.setEnabled(false);
//                                    kickPlayer.setEnabled(false);
//                                    view.setBackgroundColor(color - 0xcc000000);
//
//                                    Toast.makeText(FriendsModeActivity.this,
//                                            String.format("Пользователь %s был успешно отстранён из игры", targetName),
//                                            Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void someProblem(Throwable t) {
//                                    Log.i(TAG, t.getMessage());
//                                }
//                            };
//
//                            final ArrayList<View> viewsToDisable = null;
//                            ConnectionServer.getInstance().initKickPlayer(targetName, viewsToDisable);
//                            ConnectionServer.getInstance().connectKickPlayer(callback, viewsToDisable);
//                        }
//                    });
//
//                } else {
//                    kickPlayer.setVisibility(View.INVISIBLE);
//                    kickPlayer.setOnClickListener(null);
//                }
//            }
//        }
    }

}
