package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.NonNull;

import java.util.List;

public interface PointsCallbacks {
    void onSuccess(@NonNull List<PointResponse> value);
    void onError(@NonNull Throwable throwable);
}
