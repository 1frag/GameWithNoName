package com.example.gamewithnoname.ServerConnection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PointResponse {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
