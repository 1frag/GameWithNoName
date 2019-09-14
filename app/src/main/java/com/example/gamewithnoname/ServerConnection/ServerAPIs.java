package com.example.gamewithnoname.ServerConnection;

import com.example.gamewithnoname.models.responses.CheckGWBResponse;
import com.example.gamewithnoname.models.responses.CheckGameResponse;
import com.example.gamewithnoname.models.responses.DialogResponse;
import com.example.gamewithnoname.models.responses.GameStateResponse;
import com.example.gamewithnoname.models.responses.UserResponse;
import com.example.gamewithnoname.models.responses.SimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerAPIs {

    @GET("/tmp_name/sign_in/")
    Call<UserResponse> getResultSignIn(
            @Query("name") String name,
            @Query("password") String password
    );

    @FormUrlEncoded
    @POST("/tmp_name/sign_up/")
    Call getResultSignUp(
            @Field("name") String name,
            @Field("password") String password,
            @Field("birthday") String birthday,
            @Field("sex") Integer sex
    );

    @FormUrlEncoded
    @POST("/tmp_name/create_game/")
    Call<SimpleResponse> createGame(
            @Header("Token") String token,
            @Field("type") int type
    );

    @FormUrlEncoded
    @POST("/tmp_name/join_game/")
    Call<SimpleResponse> joinGame(
            @Header("Token") String token,
            @Field("string_invite") String key
    );

    @GET("/tmp_name/update_state_game/")
    Call<GameStateResponse> updateMap(
            @Header("Token") String token,
            @Query("latitude") double latit,
            @Query("longitude") double tongit,
            @Query("messages") int messages,
            @Query("coins") int coins
    );

    @FormUrlEncoded
    @POST("/tmp_name/begin_game/")
    Call<SimpleResponse> beginGame(
            @Header("Token") String token,
            @Field("duration") Integer duration
    );

    @GET("/tmp_name/kill_run_game/")
    Call<SimpleResponse> killRunGame(
            @Header("Token") String token
    );

    @FormUrlEncoded
    @POST("/tmp_name/change_coins/")
    Call<SimpleResponse> changeCoins(
            @Header("Token") String token,
            @Field("count") Integer count
    );

    @FormUrlEncoded
    @POST("/tmp_name/change_rating/")
    Call<SimpleResponse> changeRating(
            @Header("Token") String token,
            @Field("count") Integer count
    );

    @FormUrlEncoded
    @POST("/tmp_name/send_message/")
    Call<SimpleResponse> sendMessage(
            @Header("Token") String token,
            @Body String text
    );

    @GET("/tmp_name/check_game/")
    Call<CheckGameResponse> checkGame(
            @Header("Token") String token
    );

    @GET("/tmp_name/get_messages/")
    Call<DialogResponse> getNewMessages(
            @Header("Token") String token,
            @Query("flag") Integer flag
    );

    @GET("/tmp_name/kick_player/")
    Call<SimpleResponse> kickPlayer(
            @Header("Token") String token,
            @Query("target") String target
    );

    @GET("/tmp_name/kill_gwb/")
    Call<SimpleResponse> killGWB(
            @Query("name") String name
    );

    @GET("/tmp_name/check_gwb/")
    Call<CheckGWBResponse> checkGWB(
            @Query("name") String name
    );

    @GET("/tmp_name/create_gwb/")
    Call<SimpleResponse> createGWB(
            @Query("name") String name,
            @Query("alpha") Integer alpha,
            @Query("speed") Double speed,
            @Query("bla") Double bla,
            @Query("blo") Double blo,
            @Query("ela") Double ela,
            @Query("elo") Double elo
    );

    @GET("/tmp_name/update_gwb/")
    Call<SimpleResponse> updateGWB(
            @Query("name") String name,
            @Query("first") Integer first,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

    @GET("/tmp_name/get_my_speed_gwb/")
    Call<SimpleResponse> getMySpeedGWB(
            @Query("name") String name
    );

    @GET("/tmp_name/change_radius/")
    Call<SimpleResponse> changeRadius(
            @Header("Token") String token,
            @Query("radius") Integer radius,
            @Query("cost") Integer cost
    );

    @GET("/tmp_name/init_game/")
    Call<SimpleResponse> initGame(
            @Header("Token") String token
    );

    @GET("/tmp_name/get_top_users/")
    Call<List<UserResponse>> getTopUsers(
            @Query("search") String search,
            @Query("reg") Boolean reg
    );

    @GET("/tmp_name/change_show_hints/")
    Call<SimpleResponse> changeHints(
            @Header("Token") String token,
            @Query("value") Boolean value
    );

}
