package com.example.gamewithnoname.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.gamewithnoname.ServerConnection.ConnectionServer;
import com.example.gamewithnoname.ServerConnection.LoginCallbacks;
import com.example.gamewithnoname.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final String TAG = String.format("%s/%s",
            "HITS",
            "LoginDataSource");

    public Result<LoggedInUser> login(String username, int serverResult) {

        try {
            Log.i(TAG, String.format("server send: %s", serverResult));

            if (serverResult == 1) {
                LoggedInUser User =
                        new LoggedInUser(username);
                return new Result.Success<>(User);
            }
            if (serverResult == 0) {
                return new Result.Error(new IOException("Invalid name or password"));
            }
            return new Result.Error(new IOException("Error logging in"));

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
