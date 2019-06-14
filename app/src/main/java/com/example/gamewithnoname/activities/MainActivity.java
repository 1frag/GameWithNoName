package com.example.gamewithnoname.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.utils.UserLocation;
import com.example.gamewithnoname.callbacks.LoginCallbacks;
import com.example.gamewithnoname.models.LoggedInUser;
import com.yandex.mapkit.MapKitFactory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS", "MainActivity");
    private static final int REQUEST_LOCATION = 123;

    private static SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("4431f62e-4cef-4ce6-b1d5-07602abde3fd"); // todo: remove pls
        setContentView(R.layout.activity_main);
    }

    private void beginLogin(final String username, final String password) {
        ConnectionServer.getInstance().initLogin(username, password);
        ConnectionServer.getInstance().connectLogin(new LoginCallbacks() {

            @Override
            public void onSuccess(String name, Integer coins, Integer rating) {
                findViewById(R.id.buttonCatchBot).setEnabled(true);
                findViewById(R.id.buttonWithFriends).setEnabled(true);
                findViewById(R.id.buttonStatistics).setEnabled(true);
                findViewById(R.id.buttonFriends).setEnabled(true);
                findViewById(R.id.buttonInfo).setEnabled(true);
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                permissionsChecker(false);
                ((TextView) findViewById(R.id.textUsername)).setText(name);
                ((TextView) findViewById(R.id.textCoins)).setText(coins.toString());
                ((TextView) findViewById(R.id.textRating)).setText(rating.toString());
                new LoggedInUser(name, password);
            }

            @Override
            public void permissionDenied() {
                permissionsChecker(true);
            }

            @Override
            public void errorConnection() {
                permissionsChecker(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        findViewById(R.id.buttonCatchBot).setEnabled(false);
        findViewById(R.id.buttonWithFriends).setEnabled(false);
        findViewById(R.id.buttonStatistics).setEnabled(false);
        findViewById(R.id.buttonFriends).setEnabled(false);
        findViewById(R.id.buttonInfo).setEnabled(false);
        Boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        if (saveLogin) {
            Log.i(TAG, loginPreferences.getString("username", ""));
            Log.i(TAG, loginPreferences.getString("password", ""));
            beginLogin(
                    loginPreferences.getString("username", ""),
                    loginPreferences.getString("password", "")
            );
        } else {
            permissionsChecker(true);
        }

    }

    public void permissionsChecker(boolean login) {
        ArrayList<String> allPermissions = new ArrayList<>();

        allPermissions.add(Manifest.permission.INTERNET);
        allPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        allPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        for (int i = 0; i < allPermissions.size(); i++) {
            int res = ContextCompat.checkSelfPermission
                    (this, allPermissions.get(i));
            if (res != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{allPermissions.get(i)},
                        REQUEST_LOCATION);
            }
        }

        if (login)
            loginUser();
        UserLocation.SetUpLocationListener(this);

    }

    private void loginUser() {
        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intentLogin, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String result = data.getStringExtra("resultLogin");
        if (result == null || !result.equals("1")) {
            // Человек так и не залогинился!!
            Toast.makeText(this,
                    R.string.login_activity_you_need_reg,
                    Toast.LENGTH_LONG).show();
            loginUser(); // пока не залогинишься так и будешь
        }
    }

    public void processButtonPressing(View view) {
        switch (view.getId()) {
            case R.id.buttonCatchBot: {
                Intent intentStart = new Intent(MainActivity.this, ParametersActivity.class);
                startActivity(intentStart);
                break;
            }
            case R.id.buttonStatistics: {
                Intent intentStat = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intentStat);
                break;
            }
            case R.id.buttonFriends: {
                Intent intentFriends = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intentFriends);
                break;
            }
            case R.id.buttonInfo: {
                Intent intentInfo = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intentInfo);
                break;
            }
            case R.id.buttonAutho: {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
                Intent authoIntent = new Intent(this, LoginActivity.class);
                startActivity(authoIntent);
                break;
            }
            case R.id.buttonWithFriends: {
                Intent fmIntent = new Intent(this, FriendsModeActivity.class);
                startActivity(fmIntent);
                break;
            }
            default:
                break;
        }
    }
}
