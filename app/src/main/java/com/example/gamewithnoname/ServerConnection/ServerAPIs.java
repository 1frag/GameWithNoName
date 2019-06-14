package com.example.gamewithnoname.ServerConnection;

import com.example.gamewithnoname.models.responses.CheckGameResponse;
import com.example.gamewithnoname.models.responses.GameStateResponse;
import com.example.gamewithnoname.models.responses.UserResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.models.responses.SimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerAPIs {

    @GET("/sign_in")
    Call<UserResponse> getResultSignIn(
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

    @GET("/change_coins")
    Call<SimpleResponse> changeCoins(
            @Query("name") String name,
            @Query("count") Integer count
    );

    @GET("/send_message")
    Call<SimpleResponse> sendMessage(
            @Query("name") String name,
            @Query("text") String text
    );

    @GET("/check_game")
    Call<CheckGameResponse> checkGame(
            @Query("name") String name
    );

}
