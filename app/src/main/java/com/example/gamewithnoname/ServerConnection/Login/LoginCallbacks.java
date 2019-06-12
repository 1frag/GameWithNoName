package com.example.gamewithnoname.ServerConnection.Login;

public interface LoginCallbacks {
    void onSuccess(String name, Integer coins, Integer rating);
    void permissionDenied();
    void errorConnection();
}
