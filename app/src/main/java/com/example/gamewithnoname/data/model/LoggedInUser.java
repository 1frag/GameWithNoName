package com.example.gamewithnoname.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private static String Name;
    private static String Password;

    public LoggedInUser(String Name, String Password) {
        LoggedInUser.Name = Name;
        LoggedInUser.Password = Password;
    }

    public static String getName() {
        return Name;
    }

    public static String getPassword() {
        return Password;
    }
}
