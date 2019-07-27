package com.example.gamewithnoname.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.GetUsersCallbacks;
import com.example.gamewithnoname.models.responses.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private String search = "";
    private Boolean reg = false;
    private final static String TAG = "HITS/FriendsActivity";
    private ArrayList<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        views.add(findViewById(R.id.textFindUser));
        views.add(findViewById(R.id.checkRegs));
        views.add(findViewById(R.id.btn_find_user));

        sendQuery();
        findViewById(R.id.btn_find_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQuery();
            }
        });

    }

    private void sendQuery() {
        initParams();
        ConnectionServer.getInstance().initGetTopUsers(search, reg, views);
        ConnectionServer.getInstance().connectGetTopUsers(new GetUsersCallbacks() {
            @Override
            public void success(List<UserResponse> userResponses) {
                LinearLayout linearLayout = findViewById(R.id.layoutForNames);
                linearLayout.removeAllViews();
                for (UserResponse user : userResponses) {
                    LinearLayout newView = new LinearLayout(FriendsActivity.this);
                    getLayoutInflater().inflate(
                            R.layout.layout_model_human,
                            newView);
                    linearLayout.addView(newView);
                    ((TextView)newView.findViewById(R.id.textFriendsUsername)).setText(user.getName());
                    ((TextView)newView.findViewById(R.id.textFriendsRating)).setText(user.getRating().toString());
                    ((TextView)newView.findViewById(R.id.textFriendsKm)).setText(user.getMileage().toString());

                }
            }

            @Override
            public void failed(Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        }, views);
    }

    private void initParams() {
        search = ((TextView) findViewById(R.id.textFindUser)).getText().toString();
        reg = ((CheckBox) findViewById(R.id.checkRegs)).isChecked();
    }

    public void backToMain(View view) {
        finish();
    }
}
