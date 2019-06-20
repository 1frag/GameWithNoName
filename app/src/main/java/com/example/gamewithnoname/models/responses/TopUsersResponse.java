package com.example.gamewithnoname.models.responses;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

public class TopUsersResponse {

    @ElementList(name="user", inline=true)
    @Path("channel")
    private List<UserResponse> users;

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }
}
