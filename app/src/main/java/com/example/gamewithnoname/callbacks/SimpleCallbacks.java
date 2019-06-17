package com.example.gamewithnoname.callbacks;

import android.support.annotation.NonNull;

public interface SimpleCallbacks {
    void onSuccess(@NonNull Integer value);
    void onError(@NonNull Throwable throwable);
}
