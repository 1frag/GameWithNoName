package com.example.gamewithnoname.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckGWBResponse {
    @SerializedName("result")
    @Expose
    private Integer result;

    @SerializedName("alpha")
    @Expose
    private Integer alpha;

    @SerializedName("speed")
    @Expose
    private Double speed;

    @SerializedName("time")
    @Expose
    private Integer time;

    @SerializedName("bla")
    @Expose
    private Double bla;

    @SerializedName("blo")
    @Expose
    private Double blo;

    @SerializedName("ela")
    @Expose
    private Double ela;

    @SerializedName("elo")
    @Expose
    private Double elo;

    @SerializedName("stops")
    @Expose
    private Integer stops;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getAlpha() {
        return alpha;
    }

    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Double getBla() {
        return bla;
    }

    public void setBla(Double bla) {
        this.bla = bla;
    }

    public Double getBlo() {
        return blo;
    }

    public void setBlo(Double blo) {
        this.blo = blo;
    }

    public Double getEla() {
        return ela;
    }

    public void setEla(Double ela) {
        this.ela = ela;
    }

    public Double getElo() {
        return elo;
    }

    public void setElo(Double elo) {
        this.elo = elo;
    }

    public Integer getStops() {
        return stops;
    }

    public void setStops(Integer stops) {
        this.stops = stops;
    }
}
