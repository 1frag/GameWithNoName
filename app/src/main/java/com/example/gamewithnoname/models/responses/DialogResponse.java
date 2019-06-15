package com.example.gamewithnoname.models.responses;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="dialog", strict=false)
public class DialogResponse {

    @Element(name="result")
    @Path("channel")
    private Integer result;

    @ElementList(name="message", inline=true)
    @Path("channel")
    private List<MessageResponse> messages;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }
}
