package com.example.gamewithnoname.callbacks;

public interface JoinGameCallbacks {
    void invalidLink();
    void gameIsStarted();
    void success(Integer key);
    void someProblem(Throwable t);
}
