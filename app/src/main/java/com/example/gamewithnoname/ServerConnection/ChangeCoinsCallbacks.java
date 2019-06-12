package com.example.gamewithnoname.ServerConnection;

public interface ChangeCoinsCallbacks {
    void successful(int money);
    void badQuery();
    void userDoesNotExist();
    void notEnoughMoney();
}
