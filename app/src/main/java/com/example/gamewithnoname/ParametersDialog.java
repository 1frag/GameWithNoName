package com.example.gamewithnoname;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.gamewithnoname.maps.MapInGame;

public class ParametersDialog extends AppCompatActivity {
    private Double latit;
    private Double longit;

    private Double speed;
    private Double deviation;
    private Double time;
    private Double shortestDistance;
    private Double changedDistance;
    private Boolean oncomingSensitivity;
    private TextView textSpeed;
    private TextView textTime;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_dialog);

        latit = getIntent().getExtras().getDouble("latitude");
        longit = getIntent().getExtras().getDouble("longitude");

        speed = 5.0; //будет передаваться с сервера когда-нибудь (начальное значение)
        deviation = 1.0; //и это
        shortestDistance = 1000.0; //с предыдущего активити

        textSpeed = findViewById(R.id.textSpeedValue);
        textSpeed.setText(String.format("%.1f km/h", speed));

        time = shortestDistance * deviation / (speed * 1000 / 60);
        textTime = findViewById(R.id.timeApproximate);
        textTime.setText(String.format("Approximate time is %.1f min", time));

        changedDistance = shortestDistance * deviation;

        SeekBar speedSeekBar = findViewById(R.id.seekBarSpeed);
        speedSeekBar.setOnSeekBarChangeListener(new speedListener());

        SeekBar deviationSeekBar = findViewById(R.id.seekBarChange);
        deviationSeekBar.setOnSeekBarChangeListener(new deviationListener());

        Switch sw = findViewById(R.id.switchSensitivity);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    oncomingSensitivity = true;
                } else {
                    oncomingSensitivity = false;
                }
            }
        });
    }
    private class speedListener implements SeekBar.OnSeekBarChangeListener {

        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // Log the progress
            //Log.d("DEBUG", "Progress is: "+progress);
            //set textView's text
            Double value = (double) progress;
            value = 3.0 + value / 10.0;
            speed = value;
            time = shortestDistance * deviation / (speed * 1000 / 60);
            textTime.setText(String.format("Approximate time is %.1f min", time));
            textSpeed.setText(String.format("%.1f km/h", value));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    private class deviationListener implements SeekBar.OnSeekBarChangeListener {

        @SuppressLint("DefaultLocale")
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // Log the progress
            //Log.d("DEBUG", "Progress is: "+progress);
            //set textView's text
            double temp = (double) progress;
            deviation = 1.0 + temp / 10.0;
            time = shortestDistance * deviation / (speed * 1000 / 60);
            changedDistance = shortestDistance * deviation;
            textTime.setText(String.format("Approximate time is %.1f min", time));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    public void nextActivity (View view) {
        Intent intentStart = new Intent(this, MapInGame.class);
        intentStart.putExtra("finish_latitude", latit);
        intentStart.putExtra("finish_longitude", longit);
        // todo: generation point of bot's start, put them in:
        intentStart.putExtra("start_latitude", 56.489233);
        intentStart.putExtra("start_longitude", 84.979591);
        intentStart.putExtra("oncomingSensitivity", oncomingSensitivity);
        intentStart.putExtra("distance", changedDistance);

        startActivity(intentStart);
    }
}
