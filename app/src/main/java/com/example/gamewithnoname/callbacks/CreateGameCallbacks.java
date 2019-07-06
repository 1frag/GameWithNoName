package com.example.gamewithnoname.callbacks;

public interface CreateGameCallbacks {
    void aLotOfGames();
    void success(int key);
    void someProblem(Throwable t);
}
