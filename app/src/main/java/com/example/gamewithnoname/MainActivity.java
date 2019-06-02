package com.example.gamewithnoname;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.maps.MapMainMenu;
import com.example.gamewithnoname.ui.login.LoginActivity;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;

public class MainActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS",
            getClass().getSimpleName());
    private MapMainMenu map;
    private static final int REQUEST_LOCATION = 123;
    private final Point TARGET_LOCATION = new Point(59.945933, 30.320045);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionsChecker();
        setContentView(R.layout.activity_main);

        // todo: catch all problem with permission

//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                8);

        configureMap();

    }

    private void permissionsChecker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            UserLocation.SetUpLocationListener(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
            permissionsChecker();
        }
    }

    private void configureMap() {
        Log.i(TAG, "configureMap");
        MapKitFactory.setApiKey("4431f62e-4cef-4ce6-b1d5-07602abde3fd");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        map = new MapMainMenu();
        transaction.replace(R.id.mapHolder, map);
        transaction.commit();

    }

    public void processButtonPressing(View view) {
        switch (view.getId()) {
            case R.id.buttonStart: {
                Intent intentStart = new Intent(MainActivity.this, ParametersDialog.class);
                Point finish = map.getFinishMarker();
                if (finish == null) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.main_activity_toast_no_finish),
                            Toast.LENGTH_LONG).show();
                    break;
                }
                intentStart.putExtra("latitude", finish.getLatitude());
                intentStart.putExtra("longitude", finish.getLongitude());
                // todo: detect invalid finish
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
                Intent authoIntent = new Intent(this, LoginActivity.class);
                startActivity(authoIntent);
                break;
            }
            default:
                break;
        }
    }
}
