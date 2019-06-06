package com.example.gamewithnoname;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.ServerCallbacks;

import java.util.Date;

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

        Button signUp = findViewById(R.id.buttonReg);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText)findViewById(R.id.editTextName)).getText().toString();
                final String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                final String confirm = ((EditText)findViewById(R.id.editTextConfirmPassword)).getText().toString();
                final Date birth = getDateBirth();
                final Integer sex = sexHandler();
                if (dataValid(name, password, confirm, birth, sex)){
                    beginRegistration(name, password, birth, sex);
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            "Data is not valid",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private Date getDateBirth() {
        return new Date(123123); // todo: catch date from editText
    }

    private boolean dataValid(String name, String password, String confirm, Date bith, Integer sex) {
        if (!password.equals(confirm)) return false;
        // todo: add smth conditions
        return true;
    }

    private Integer sexHandler() {
        if(((RadioButton)findViewById(R.id.radioButtonFemale)).isChecked()){
            return 1;
        } else if (((RadioButton)findViewById(R.id.radioButtonMale)).isChecked()){
            return -1;
        } else {
            return 0;
        }
    }

    private void beginRegistration(final String username,
                                   final String password,
                                   final Date birth,
                                   final Integer sex) {
        ConnectionServer connectionServer = new ConnectionServer();
        connectionServer.initRegistration(username, password, birth, sex);
        connectionServer.connect(new ServerCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        Log.i(TAG, "ServerCallbacks -> onSuccess");
                        int result = Integer.parseInt(value);
                        Log.i(TAG, String.format("result Reg: %s", result));
                        // todo: smth actions!!!
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "ServerCallbacks -> onError");
                        Log.i(TAG, throwable.getMessage());
                        // todo: toast maybe or smth other?
                    }
                });
    }
}
