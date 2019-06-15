package com.example.gamewithnoname.models.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "message", strict = false)
public class MessageResponse {

    @Element(name = "text")
    private String text;

    @Element(name = "from")
    private String from;

    @Element(name = "date")
    private String date;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
