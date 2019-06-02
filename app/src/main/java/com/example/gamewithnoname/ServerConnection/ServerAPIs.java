package com.example.gamewithnoname.ServerConnection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerAPIs {
    /*
    Get request to fetch city weather.Takes in two parameter-city name and API key.
    */
    @GET("/sign_in")
    Call<ServerResponse> getResultLogin(
            @Query("name") String name,
            @Query("password") String password
    );
}
