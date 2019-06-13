package com.example.gamewithnoname.models.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "stats", strict = false)
public class StatisticsResponse {

    @Element(name = "coins")
    private Integer coins;

    @Element(name = "rating")
    private Integer rating;


    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}