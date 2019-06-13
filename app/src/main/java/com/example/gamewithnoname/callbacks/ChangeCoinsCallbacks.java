package com.example.gamewithnoname.callbacks;

public interface ChangeCoinsCallbacks {
    void successful(int money);
    void badQuery();
    void userDoesNotExist();
    void notEnoughMoney();
}
