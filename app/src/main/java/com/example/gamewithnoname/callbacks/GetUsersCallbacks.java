package com.example.gamewithnoname.callbacks;

import com.example.gamewithnoname.models.responses.TopUsersResponse;
import com.example.gamewithnoname.models.responses.UserResponse;

import java.util.List;

public interface GetUsersCallbacks {
    void success(List<UserResponse> userResponses);
    void failed(Throwable t);
}
