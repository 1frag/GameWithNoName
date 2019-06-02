package com.example.gamewithnoname.ServerConnection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
            @Query("password") String password
    );

}
