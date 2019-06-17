package com.example.gamewithnoname.callbacks;

public interface SignUpCallbacks {
    void success();
    void nameAlreadyExists();
    void someProblem(Throwable t);
}
