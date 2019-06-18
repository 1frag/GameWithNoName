package com.example.gamewithnoname.callbacks;

import android.support.annotation.NonNull;

import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.models.responses.MessageResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.models.responses.StatisticsResponse;

import java.util.List;

public interface UpdateStateCallbacks {
    void gamersUpdate(List<GamersResponse> gamers);
    void coinsUpdate(List<PointsResponse> coins);
    void messagesUpdate(List<MessageResponse> messages);
    void gameOver(StatisticsResponse stats);
    void linkUpdate(String link);
    void changeOwn(Boolean isAuthor);
    void changeProgress(Integer isRun);
    void changeTypeGame(Integer type);
    void changeTimer(Integer time);
}
