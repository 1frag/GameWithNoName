package com.example.gamewithnoname;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.SimpleCallbacks;
import com.example.gamewithnoname.data.model.LoggedInUser;
import com.yandex.mapkit.MapKitFactory;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AppResultsReceiver.Receiver {

    private final String TAG = String.format("%s/%s",
            "HITS",
            getClass().getSimpleName());

    private static final int REQUEST_LOCATION = 123;

    private Timer mTimer;
    private SimpleCallbacks simpleCallbacks;
    private ConnectionServer connectionServer;
    private Integer resultServerCallbacks = -1;
    private TextView textUsername;

    public static AppResultsReceiver mainReceiver; // it's super collback

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("4431f62e-4cef-4ce6-b1d5-07602abde3fd"); // todo: remove pls
        setContentView(R.layout.activity_main);

        textUsername = findViewById(R.id.textUsername);

        // todo: catch all problems with permission
        permissionsChecker();

    }

    public void startBackgroundConnect() {

        mTimer = new Timer();

        connectionServer = new ConnectionServer();

        simpleCallbacks = new SimpleCallbacks() {
            @Override
            public void onSuccess(@NonNull String value) {

//                Log.i(TAG, "SimpleCallbacks -> onSuccess");
                resultServerCallbacks = Integer.parseInt(value);

                MainActivity.this.runOnUiThread(new Runnable() {
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
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
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
                connectionServer.connectSimple(simpleCallbacks);
            }
        };

//        mTimer.schedule(timerTask, 1000, 1000);

    }

    public void permissionsChecker() {
        ArrayList<String> allPermissions = new ArrayList<>();

        allPermissions.add(Manifest.permission.INTERNET);
        allPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        allPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        allPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        allPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        for (int i = 0; i < allPermissions.size(); i++) {
            int res = ContextCompat.checkSelfPermission
                    (this, allPermissions.get(i));
            if (res != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "permissionsChecker for i");
                ActivityCompat.requestPermissions(this,
                        new String[]{allPermissions.get(i)},
                        REQUEST_LOCATION);
            }
        }

        loginUser();
        UserLocation.SetUpLocationListener(this);
        startBackgroundConnect();

    }

    private void loginUser() {

        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intentLogin, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
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
                Intent intentStart = new Intent(MainActivity.this, ParametersDialog.class);
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
                LoginActivity.clearLoginOptions();
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

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case Constants.STATUS_RUNNING :
                /**/
                Log.i(TAG, "qweqwe");
                break;
            case Constants.STATUS_FINISHED :
                Log.i(TAG, "asdsasd");
                Toast.makeText(this, "Service finished with data: "
                        + data.getString(Constants.RECEIVER_DATA), Toast.LENGTH_SHORT).show();
                break;
        }
        Log.i(TAG, String.format("valiiiiiid: %s", resultCode));
    }
}
