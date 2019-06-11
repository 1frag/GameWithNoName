package com.example.gamewithnoname.ServerConnection;

import com.example.gamewithnoname.ServerConnection.Gamers.GameStateResponse;
import com.example.gamewithnoname.ServerConnection.Points.PointsResponse;
import com.example.gamewithnoname.ServerConnection.Simple.SimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerAPIs {

    @GET("/sign_in")
    Call<SimpleResponse> getResultSignIn(
            @Query("name") String name,
            @Query("password") String password
    );

    @GET("/sign_up")
    Call<SimpleResponse> getResultSignUp(
            @Query("name") String name,
            @Query("password") String password,
            @Query("birthday") String birthday,
            @Query("sex") Integer sex
    );

    @GET("/get_event")
    Call<SimpleResponse> getResultEvent();

    @GET("/put_online")
    Call<SimpleResponse> putOnline(
            @Query("name") String name
    );

    @GET("/create_game")
    Call<SimpleResponse> createGame(
            @Query("name") String name,
            @Query("latitude") double latit,
            @Query("longitude") double tongit
    );

    @GET("/join_game")
    Call<SimpleResponse> joinGame(
            @Query("name") String name,
            @Query("latitude") double latit,
            @Query("longitude") double tongit,
            @Query("string_invite") String key
    );

    @GET("/update_map_in_game")
    Call<GameStateResponse> updateMap(
            @Query("name") String name,
            @Query("string_invite") String string_invite,
            @Query("latitude") double latit,
            @Query("longitude") double tongit
    );

    @GET("/begin_game")
    Call<SimpleResponse> beginGame(
            @Query("name") String name,
            @Query("string_invite") String string_invite,
            @Query("duration") Integer duration
    );

    @GET("/get_positions_coins")
    Call<List<PointsResponse>> updateCoins(
            @Query("string_invite") String string_invite
    );

    @GET("/kill_run_game")
    Call<SimpleResponse> killRunGame(
            @Query("name") String name,
            @Query("string_invite") String string_invite
    );

    @GET("/get_money")
    Call<SimpleResponse> getMoney(
            @Query("name") String name
    );

    @GET("/get_rating")
    Call<SimpleResponse> getRating(
            @Query("name") String name
    );

}
