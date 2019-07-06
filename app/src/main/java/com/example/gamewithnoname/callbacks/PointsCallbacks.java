package com.example.gamewithnoname.callbacks;

import android.support.annotation.NonNull;

import com.example.gamewithnoname.models.responses.PointsResponse;

import java.util.List;

public interface PointsCallbacks {
    void onSuccess(
            @NonNull Integer value,
            @NonNull List<PointsResponse> points
    );
    void onError(@NonNull Throwable throwable);
}
