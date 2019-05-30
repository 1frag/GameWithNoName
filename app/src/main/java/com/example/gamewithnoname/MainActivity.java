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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS",
            getClass().getSimpleName());
    Double nameFirst = 3.5;
    Double nameSecond = 47.92;

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
        Fragment map = new MapMainMenu();
        transaction.replace(R.id.mapHolder, map);
        transaction.commit();

    }



    public void processButtonPressing(View view) {
        switch (view.getId()) {
            case R.id.buttonStart: {
                Intent intentStart = new Intent(MainActivity.this, MapInGame.class);
                intentStart.putExtra("nameFirst", nameFirst);
                intentStart.putExtra("nameSecond", nameSecond);
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
