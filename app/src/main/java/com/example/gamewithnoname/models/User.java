package com.example.gamewithnoname.models;

import android.support.annotation.Nullable;

import com.example.gamewithnoname.models.responses.UserResponse;


public class User {

    private static String name;
    private static String password;
    private static String birthday;
    private static String dateSignUp;
    private static Integer sex;
    private static Integer money;
    private static Integer rating;
    private static Integer mileage;

    public User(@Nullable String name, @Nullable String password,
                @Nullable String birthday, @Nullable String dateSignUp,
                @Nullable Integer sex, @Nullable Integer money,
                @Nullable Integer rating, @Nullable Integer mileage) {
        User.name = name;
        User.password = password;
        User.birthday = birthday;
        User.dateSignUp = dateSignUp;
        User.sex = sex;
        User.money = money;
        User.rating = rating;
        User.mileage = mileage;
    }

    public User(UserResponse userResponse){
        User.name = userResponse.getName();
        User.password = userResponse.getPassword();
        User.birthday = userResponse.getBirthday();
        User.dateSignUp = userResponse.getDateSignUp();
        User.sex = userResponse.getSex();
        User.money = userResponse.getMoney();
        User.rating = userResponse.getRating();
        User.mileage = userResponse.getMileage();
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }

    public static String getBirthday() {
        return birthday;
    }

    public static void setBirthday(String birthday) {
        User.birthday = birthday;
    }

    public static String getDateSignUp() {
        return dateSignUp;
    }

    public static void setDateSignUp(String date_sign_up) {
        User.dateSignUp = date_sign_up;
    }

    public static Integer getSex() {
        return sex;
    }

    public static void setSex(Integer sex) {
        User.sex = sex;
    }

    public static Integer getMoney() {
        return money;
    }

    public static void setMoney(Integer money) {
        User.money = money;
    }

    public static Integer getRating() {
        return rating;
    }

    public static void setRating(Integer rating) {
        User.rating = rating;
    }

    public static Integer getMileage() {
        return mileage;
    }

    public static void setMileage(Integer mileage) {
        User.mileage = mileage;
    }
}
