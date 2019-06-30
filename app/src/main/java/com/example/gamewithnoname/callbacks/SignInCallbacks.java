package com.example.gamewithnoname.callbacks;

import android.support.annotation.Nullable;

import com.example.gamewithnoname.models.responses.UserResponse;

public interface SignInCallbacks {
    void baseSettingsAccount(String name, String password);
    void capital(Integer money, Integer rating);
    void statsData(Integer mileage);
    void otherSettingsAccount(Integer sex,
                              String birthday,
                              String dateSignUp,
                              Boolean hints);
    void success(UserResponse userResponse);
    void permissionDenied();
    void someProblem(Throwable t);
}
