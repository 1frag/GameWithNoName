package com.example.gamewithnoname.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name="rss", strict=false)
public class GameStateResponse {

    @Element(name="state")
    @Path("channel")
    private Integer state;

    @ElementList(name="gamer", inline=true)
    @Path("channel")
    private List<GamersResponse> gamers;

    @ElementList(name="coins", inline=true)
    @Path("channel")
    private List<PointsResponse> coins;

    @ElementList(name="stats", inline=true)
    @Path("channel")
    private StatisticsResponse stats;

    @ElementList(name="message", inline=true)
    @Path("channel")
    private List<MessageResponse> messages;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("author")
    @Expose
    private Boolean author;

    @SerializedName("progress")
    @Expose
    private Integer progress;

    @SerializedName("type_game")
    @Expose
    private Integer type_game;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<GamersResponse> getGamers() {
        return gamers;
    }

    public void setGamers(List<GamersResponse> gamers) {
        this.gamers = gamers;
    }

    public List<PointsResponse> getCoins() {
        return coins;
    }

    public void setCoins(List<PointsResponse> coins) {
        this.coins = coins;
    }

    public StatisticsResponse getStats() {
        return stats;
    }

    public void setStats(StatisticsResponse stats) {
        this.stats = stats;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }

    public Boolean getAuthor() {
        return author;
    }

    public void setAuthor(Boolean author) {
        this.author = author;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getType_game() {
        return type_game;
    }

    public void setType_game(Integer type_game) {
        this.type_game = type_game;
    }
}