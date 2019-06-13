package com.example.gamewithnoname.callbacks;

import android.support.annotation.NonNull;

public interface SimpleCallbacks {
    void onSuccess(@NonNull String value);
    void onError(@NonNull Throwable throwable);
}
