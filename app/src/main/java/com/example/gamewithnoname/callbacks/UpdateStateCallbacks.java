package com.example.gamewithnoname.callbacks;

import android.support.annotation.NonNull;

import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.models.responses.StatisticsResponse;

import java.util.List;

public interface UpdateStateCallbacks {
    void gamersUpdate(
            @NonNull List<GamersResponse> gamers
    );
    void coinsUpdate(
            @NonNull List<PointsResponse> coins
    );
    void gameOver(
            @NonNull StatisticsResponse stats
    );
    void updateLink(
            @NonNull String link
    );
}
