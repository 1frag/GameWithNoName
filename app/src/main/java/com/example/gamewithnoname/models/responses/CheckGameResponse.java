package com.example.gamewithnoname.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckGameResponse {

    @SerializedName("result")
    @Expose
    private Integer result;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("own")
    @Expose
    private Integer own;

    @SerializedName("run")
    @Expose
    private Integer run;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getRun() {
        return run;
    }

    public void setRun(Integer run) {
        this.run = run;
    }

    public Integer getOwn() {
        return own;
    }

    public void setOwn(Integer own) {
        this.own = own;
    }
}
