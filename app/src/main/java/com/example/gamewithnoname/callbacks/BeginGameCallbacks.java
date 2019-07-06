package com.example.gamewithnoname.callbacks;

public interface BeginGameCallbacks {
    void youAreNotAuthor();
    void notEnoughMan();
    void success(int response);
    void someProblem(Throwable t);
}
