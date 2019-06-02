package com.example.gamewithnoname.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String mName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String name) {
        this.mName = name;
    }

    String getName() {
        return mName;
    }
}
