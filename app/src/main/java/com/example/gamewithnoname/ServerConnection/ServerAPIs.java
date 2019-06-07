package com.example.gamewithnoname.ServerConnection;

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

}
