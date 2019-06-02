package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.NonNull;

public interface ServerCallbacks {
    void onSuccess(@NonNull String value);
    void onError(@NonNull Throwable throwable);
}
