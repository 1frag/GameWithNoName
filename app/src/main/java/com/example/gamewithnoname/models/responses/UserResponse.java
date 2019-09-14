package com.example.gamewithnoname.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Root;

public class UserResponse {

    @SerializedName("result")
    @Expose
    private Integer result;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("birthday")
    @Expose
    private String birthday;

    @SerializedName("date_sign_up")
    @Expose
    private String date_sign_up;

    @SerializedName("sex")
    @Expose
    private Integer sex;

    @SerializedName("money")
    @Expose
    private Integer money;

    @SerializedName("rating")
    @Expose
    private Integer rating;

    @SerializedName("mileage")
    @Expose
    private Integer mileage;

    @SerializedName("hints")
    @Expose
    private Boolean hints;

    @SerializedName("token")
    @Expose
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDateSignUp() {
        return date_sign_up;
    }

    public void setDate_sign_up(String date_sign_up) {
        this.date_sign_up = date_sign_up;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Boolean getHints() {
        return hints;
    }

    public void setHints(Boolean hints) {
        this.hints = hints;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
