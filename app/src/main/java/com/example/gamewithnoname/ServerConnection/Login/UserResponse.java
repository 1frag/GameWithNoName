package com.example.gamewithnoname.ServerConnection.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("result")
    @Expose
    private Integer result;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("money")
    @Expose
    private Integer money;

    @SerializedName("rating")
    @Expose
    private Integer rating;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
