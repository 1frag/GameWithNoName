package com.example.gamewithnoname;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.ServerCallbacks;
import com.example.gamewithnoname.data.model.LoggedInUser;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS",
            "LoginActivity");
    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_login);

        configSignUpButton();
        configSignInButton();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin) {
            Log.i(TAG, loginPreferences.getString("username", ""));
            Log.i(TAG, loginPreferences.getString("password", ""));
            beginLogin(
                    loginPreferences.getString("username", ""),
                    loginPreferences.getString("password", "")
            );
        }
    }

    public static void clearLoginOptions(){
        loginPrefsEditor.clear();
        loginPrefsEditor.commit();
    }

    private void configSignUpButton() {
        Button btn = findViewById(R.id.signUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStat = new Intent(
                        LoginActivity.this,
                        RegistrationActivity.class
                );
                startActivity(intentStat);
            }
        });
    }

    private void configSignInButton() {
        Button btn = findViewById(R.id.signIn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText) findViewById(R.id.username)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();
                if (dataIsVavid(name, password)) {
                    beginLogin(name, password);
                } else {
                    Toast.makeText(LoginActivity.this,
                            "не похоже даже на правду",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean dataIsVavid(String name, String password) {
        // todo: some check during text inputting
        return true;
    }

    private void beginLogin(final String username, final String password) {
        ConnectionServer connectionServer = new ConnectionServer();
        connectionServer.initLogin(username, password);
        connectionServer.connect(new ServerCallbacks() {
            @Override
            public void onSuccess(@NonNull String value) {
                Log.i(TAG, "ServerCallbacks -> onSuccess");
                int result = Integer.parseInt(value);
                if (result != 1) {
                    showLoginFailed(result);
                } else {
                    updateUiWithUser(username, password);
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.i(TAG, "ServerCallbacks -> onError");
                Log.i(TAG, throwable.getMessage());
                // todo: toast maybe or smth other?
            }
        });
    }

    private void updateUiWithUser(String name, String password) {
        String welcome = String.format("%s%s", getString(R.string.welcome), name);
        // TODO : initiate successful logged in experience
        Log.i(TAG, "initiate successful logged in experience");
        loginPrefsEditor.putBoolean("saveLogin", true);
        loginPrefsEditor.putString("username", name);
        loginPrefsEditor.putString("password", password);
        loginPrefsEditor.commit();

        new LoggedInUser(name, password);

        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "showLoginFailed");

        loginPrefsEditor.putBoolean("saveLogin", false);
        loginPrefsEditor.commit();

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,
                "Вы должны зарегистрироваться",
                Toast.LENGTH_LONG).show();
    }
}
