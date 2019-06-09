package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.NonNull;

import java.util.List;

public interface PointsCallbacks {
    void onSuccess(
            @NonNull Integer value,
            @NonNull List<PointResponse> points
    );
    void onError(@NonNull Throwable throwable);
}
