package com.example.gamewithnoname.callbacks;

public interface LoginCallbacks {
    void onSuccess(String name, Integer coins, Integer rating);
    void permissionDenied();
    void errorConnection();
}
