package com.example.gamewithnoname.ServerConnection.Gamers;

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

    @ElementList(name="item", inline=true)
    @Path("channel")
    private List<GamersResponse> gamers;

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


}