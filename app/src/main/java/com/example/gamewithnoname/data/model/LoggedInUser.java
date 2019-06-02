package com.example.gamewithnoname.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String Name;

    public LoggedInUser(String Name) {
        this.Name = Name;
    }

    public String getDisplayName() {
        return Name;
    }
}
