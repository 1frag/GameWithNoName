package com.example.gamewithnoname.models.responses;

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

    @ElementList(name="point", inline=true)
    @Path("channel")
    private List<PointsResponse> points;

    @ElementList(name="stats", inline=true)
    @Path("channel")
    private StatisticsResponse stats;

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


    public List<PointsResponse> getPoints() {
        return points;
    }

    public void setPoints(List<PointsResponse> points) {
        this.points = points;
    }

    public StatisticsResponse getStats() {
        return stats;
    }

    public void setStats(StatisticsResponse stats) {
        this.stats = stats;
    }
}