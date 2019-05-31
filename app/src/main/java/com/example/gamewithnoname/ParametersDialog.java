package com.example.gamewithnoname;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.gamewithnoname.maps.MapInGame;

public class ParametersDialog extends AppCompatActivity {
    private Double latit;
    private Double longit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_dialog);

        latit = getIntent().getExtras().getDouble("latitude");
        longit = getIntent().getExtras().getDouble("longitude");
    }

    public void nextActivity (View view) {
        Intent intentStart = new Intent(this, MapInGame.class);
        intentStart.putExtra("finish_latitude", latit);
        intentStart.putExtra("finish_longitude", longit);
        // todo: generation point of bot's start, put them in:
        intentStart.putExtra("start_latitude", latit+0.002);
        intentStart.putExtra("start_longitude", longit-0.003);
        startActivity(intentStart);
    }
}
