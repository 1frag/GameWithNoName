package com.example.gamewithnoname.callbacks;

public interface CheckGameCallbacks {
    void inRun(String link, Integer type);
    void inWait(String link, Integer type);
    void inFree();
}
