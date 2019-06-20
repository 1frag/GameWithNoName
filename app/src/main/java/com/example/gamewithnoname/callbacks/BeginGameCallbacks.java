package com.example.gamewithnoname.callbacks;

public interface BeginGameCallbacks {
    void youAreNotAuthor();
    void notEnoughMan();
    void success();
    void errorTime(int minMinutes);
    void someProblem(Throwable t);
}
