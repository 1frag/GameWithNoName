package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.NonNull;

import com.example.gamewithnoname.ServerConnection.Gamers.GamersResponse;
import com.example.gamewithnoname.ServerConnection.Points.PointsResponse;
import com.example.gamewithnoname.ServerConnection.Statistics.StatisticsResponse;

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
}
