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
import com.example.gamewithnoname.ServerConnection.Simple.SimpleCallbacks;

import static java.lang.Character.isDigit;

public class RegistrationActivity extends AppCompatActivity {

    private final String TAG = String.format("%s/%s",
            "HITS",
            "RegistrationActivity");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // todo: disable signUp if ... (smth conditions)

        Button signUp = findViewById(R.id.buttonReg);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ((EditText)findViewById(R.id.editTextName)).getText().toString();
                final String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                final String confirm = ((EditText)findViewById(R.id.editTextConfirmPassword)).getText().toString();
                final String birth = getDateBirth();
                final Integer sex = sexHandler();
                switch (dataValid(name, password, confirm, birth, sex)) {
                    case 1: {
                        beginRegistration(name, password, birth, sex);
                        break;
                    }
                    case 2: {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.activity_registration_notequal_passwords,
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    case 3: {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.activity_registration_small_login,
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    case 4: {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.activity_registration_incorrect_login,
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    case 5: {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.activity_registration_small_password,
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.registration_activity_data_not_valid,
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                }

            }
        });
    }

    private String getDateBirth() {
        return ((EditText)findViewById(R.id.editTextBirth))
                .getText()
                .toString();
    }

    private int dataValid(String name, String password, String confirm, String bith, Integer sex) {
        if (!password.equals(confirm)) return 2;
        if (name.length() < 6) return 3;
        if (isDigit(name.charAt(0))) return 4;
        if (password.length() < 6) return 5;
        return 1;
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
                                   final String birth,
                                   final Integer sex) {
        ConnectionServer connectionServer = new ConnectionServer();
        connectionServer.initRegistration(username, password, birth, sex);
        connectionServer.connectSimple(new SimpleCallbacks() {
                    @Override
                    public void onSuccess(@NonNull String value) {
                        Log.i(TAG, "SimpleCallbacks -> gamersUpdate");
                        int result = Integer.parseInt(value);
                        // если result 1 -- значит успешно
                        // если result 0 -- неизвестная ошибка
                        // если result 2 -- логин уже был такой
                        Log.i(TAG, String.format("result Reg: %s", result));
                        if (result == 1) {
                            finish();
                        }
                        else if (result == 0) {
                            Toast.makeText(RegistrationActivity.this,
                                    R.string.main_activity_error,
                                    Toast.LENGTH_LONG).show();
                        }
                        else if (result == 2) {
                            Toast.makeText(RegistrationActivity.this,
                                    R.string.registration_activity_name_problem,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "SimpleCallbacks -> onError");
                        Log.i(TAG, throwable.getMessage());
                        Toast.makeText(RegistrationActivity.this,
                                R.string.main_activity_error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
