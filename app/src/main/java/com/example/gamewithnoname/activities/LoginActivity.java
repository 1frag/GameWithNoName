package com.example.gamewithnoname.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamewithnoname.R;
import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.callbacks.SignInCallbacks;
import com.example.gamewithnoname.models.User;
import com.example.gamewithnoname.models.responses.UserResponse;

import java.util.ArrayList;

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
            beginLogin(
                    loginPreferences.getString("username", ""),
                    loginPreferences.getString("password", "")
            );
        }
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
                final String name = ((EditText) findViewById(R.id.edittext_username)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();
                if (dataIsValid(name, password)) {
                    beginLogin(name, password);
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.login_activity_incorrect_data,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean dataIsValid(String name, String password) {
        if (name.equals("") || password.equals("")) return false;
        else return true;
    }

    private void beginLogin(final String username, final String password) {
        final ArrayList<View> viewsToDisable = null;
        ConnectionServer.getInstance().initSignIn(username, password, viewsToDisable);
        ConnectionServer.getInstance().connectLogin(new SignInCallbacks() {

            @Override
            public void baseSettingsAccount(String name, String password) {
                User.setName(name);
                User.setPassword(password);
                rememberUser(name, password);
            }

            @Override
            public void capital(Integer money, Integer rating) {
                User.setMoney(money);
                User.setRating(rating);
            }

            @Override
            public void statsData(Integer mileage) {
                User.setMoney(mileage);
            }

            @Override
            public void otherSettingsAccount(Integer sex, String birthday,
                                             String dateSignUp, Boolean hints) {
                User.setMoney(sex);
                User.setBirthday(birthday);
                User.setDateSignUp(dateSignUp);
                User.setHints(hints);
            }

            @Override
            public void success(UserResponse userResponse) {
                String welcome = String.format(getResources().getString(R.string.welcome), userResponse.getName());
                Log.i(TAG, "initiate successful logged in experience");
                Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void permissionDenied() {
                showLoginFailed();
            }

            @Override
            public void someProblem(Throwable t) {
                Log.i(TAG, t.getMessage());
                showFailedWithConnection();
            }
        }, viewsToDisable);
    }

    private void showFailedWithConnection() {
        Toast.makeText(this,
                getResources().getString(R.string.problem_internet),
                Toast.LENGTH_SHORT).show();
    }

    private void rememberUser(String name, String password) {

        loginPrefsEditor.putBoolean("saveLogin", true);
        loginPrefsEditor.putString("username", name);
        loginPrefsEditor.putString("password", password);
        loginPrefsEditor.commit();

    }

    private void showLoginFailed() {
        Toast.makeText(getApplicationContext(),
                R.string.registration_activity_data_not_valid,
                Toast.LENGTH_SHORT).show();
        Log.i(TAG, "showLoginFailed");

        loginPrefsEditor.putBoolean("saveLogin", false);
        loginPrefsEditor.commit();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
