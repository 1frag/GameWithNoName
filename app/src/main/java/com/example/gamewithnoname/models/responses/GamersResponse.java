package com.example.gamewithnoname.models.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "gamer", strict = false)
public class GamersResponse {

    @Element(name = "name")
    private String name;

    @Element(name = "latitude")
    private Double latitude;

    @Element(name = "longitude")
    private Double longitude;

    @Element(name = "color")
    private Integer color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}