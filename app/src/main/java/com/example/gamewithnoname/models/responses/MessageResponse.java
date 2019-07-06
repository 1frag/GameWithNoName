package com.example.gamewithnoname.models.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "message", strict = false)
public class MessageResponse {

    @Element(name = "text")
    private String text;

    @Element(name = "from")
    private String from;

    @Element(name = "color")
    private Integer color;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
