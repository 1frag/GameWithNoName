package com.example.gamewithnoname;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gamewithnoname.fragments_maps.MapInGame;
import com.example.gamewithnoname.fragments_maps.MapMainMenu;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS",
            getClass().getSimpleName());
    private MapMainMenu map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionsHandler.requestMultiplePermissions(this);
        UserLocation.SetUpLocationListener(this);
        setContentView(R.layout.activity_main);

        // todo: catch all problem with permission

//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                8);

        configureMap();

    }

    private void configureMap() {
        Log.i(TAG, "configureMap");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        map = new MapMainMenu();
        transaction.replace(R.id.mapHolder, map);
        transaction.commit();

    }

    public void processButtonPressing(View view) {
        switch (view.getId()) {
            case R.id.buttonStart: {
                Intent intentStart = new Intent(MainActivity.this, MapInGame.class);
                LatLng finish = map.getFinishMarker();
                if(finish == null){
                    Toast.makeText(getApplicationContext(),
                            "Touch to set finish",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                intentStart.putExtra("latitude", finish.latitude);
                intentStart.putExtra("longitude", finish.longitude);
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
            default: break;
        }
    }
}
