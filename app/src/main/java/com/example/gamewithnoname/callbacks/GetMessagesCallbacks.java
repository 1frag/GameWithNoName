package com.example.gamewithnoname.callbacks;

import com.example.gamewithnoname.models.responses.MessageResponse;

import java.util.List;

public interface GetMessagesCallbacks {
    void success(List<MessageResponse> messages);
    void problem(int value);
}
