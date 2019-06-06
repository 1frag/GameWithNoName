package com.example.gamewithnoname;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.gamewithnoname.maps.MapInGame;

public class OnlineMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configFollow();
    }

    private void configFollow() {
        Button btn = findViewById(R.id.follow_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGame = new Intent(OnlineMode.this, MapInGame.class);
                intentGame.putExtra("typeGame", 1); // type=1 -> online
                startActivity(intentGame);
            }
        });
    }

}
