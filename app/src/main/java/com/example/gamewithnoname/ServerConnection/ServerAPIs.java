package com.example.gamewithnoname.ServerConnection;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerAPIs {

    @GET("/sign_in")
    Call<ServerResponse> getResultSignIn(
            @Query("name") String name,
            @Query("password") String password
    );

    @GET("/sign_up")
    Call<ServerResponse> getResultSignUp(
            @Query("name") String name,
            @Query("password") String password,
            @Query("birthday") Date birthday,
            @Query("sex") Integer sex
    );

    @GET("/get_event")
    Call<ServerResponse> getResultEvent();

    @GET("/put_online")
    Call<ServerResponse> putOnline(
            @Query("name") String name
    );

    @GET("/put_my_position")
    Call<ServerResponse> putMyPosition(
            @Query("name") String name,
            @Query("latit") double latit,
            @Query("tongit") double tongit
    );

}
