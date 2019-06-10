package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.NonNull;

import com.example.gamewithnoname.ServerConnection.Gamers.GamersResponse;

import java.util.List;

public interface UpdateStateCallbacks {
    void onSuccess(
            @NonNull Integer value,
            @NonNull List<GamersResponse> points
    );
}
