package com.example.gamewithnoname.ServerConnection.Points;

import android.support.annotation.NonNull;

import java.util.List;

public interface PointsCallbacks {
    void onSuccess(
            @NonNull Integer value,
            @NonNull List<PointsResponse> points
    );
    void onError(@NonNull Throwable throwable);
}
