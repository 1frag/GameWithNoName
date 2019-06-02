package com.example.gamewithnoname.ui.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.gamewithnoname.R;

public class RegistrationActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS",
            "RegistrationActivity");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // todo: disable signUp if ... (smth conditions)
        // todo: request to server (again Retrofit =( )
    }
}
