package com.example.gamewithnoname;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.gamewithnoname.fragments_maps.MapInGame;
import com.google.android.gms.maps.model.LatLng;

public class parametersDialog extends AppCompatActivity {
    private Double latit;
    private Double longit;

    private Double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_dialog);

        latit = getIntent().getExtras().getDouble("latitude");
        longit = getIntent().getExtras().getDouble("longitude");

        speed = 5.0;
    }

    public void nextActivity (View view) {
        Intent intentStart = new Intent(this, MapInGame.class);
        intentStart.putExtra("latitude", latit);
        intentStart.putExtra("longitude", longit);
        startActivity(intentStart);
    }
}
