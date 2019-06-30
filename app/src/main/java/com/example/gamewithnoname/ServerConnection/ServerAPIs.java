package com.example.gamewithnoname.ServerConnection;

import com.example.gamewithnoname.callbacks.GetUsersCallbacks;
import com.example.gamewithnoname.models.responses.CheckGWBResponse;
import com.example.gamewithnoname.models.responses.CheckGameResponse;
import com.example.gamewithnoname.models.responses.DialogResponse;
import com.example.gamewithnoname.models.responses.GameStateResponse;
import com.example.gamewithnoname.models.responses.MessageResponse;
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
            @Query("type") int type
    );

    @GET("/join_game")
    Call<SimpleResponse> joinGame(
            @Query("name") String name,
            @Query("string_invite") String key
    );

    @GET("/update_state_game")
    Call<GameStateResponse> updateMap(
            @Query("name") String name,
            @Query("latitude") double latit,
            @Query("longitude") double tongit,
            @Query("messages") int messages,
            @Query("coins") int coins
    );

    @GET("/begin_game")
    Call<SimpleResponse> beginGame(
            @Query("name") String name,
            @Query("duration") Integer duration
    );

    @GET("/kill_run_game")
    Call<SimpleResponse> killRunGame(
            @Query("name") String name
    );

    @GET("/change_coins")
    Call<SimpleResponse> changeCoins(
            @Query("name") String name,
            @Query("count") Integer count
    );

    @GET("/change_rating")
    Call<SimpleResponse> changeRating(
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

    @GET("/get_messages")
    Call<DialogResponse> getNewMessages(
            @Query("name") String name,
            @Query("flag") Integer flag
    );

    @GET("/kick_player")
    Call<SimpleResponse> kickPlayer(
            @Query("target") String target
    );

    @GET("/kill_gwb")
    Call<SimpleResponse> killGWB(
            @Query("name") String name
    );

    @GET("/check_gwb")
    Call<CheckGWBResponse> checkGWB(
            @Query("name") String name
    );

    @GET("/create_gwb")
    Call<SimpleResponse> createGWB(
            @Query("name") String name,
            @Query("alpha") Integer alpha,
            @Query("speed") Double speed,
            @Query("bla") Double bla,
            @Query("blo") Double blo,
            @Query("ela") Double ela,
            @Query("elo") Double elo
    );

    @GET("/update_gwb")
    Call<SimpleResponse> updateGWB(
            @Query("name") String name,
            @Query("first") Integer first,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude
    );

    @GET("/get_my_speed_gwb")
    Call<SimpleResponse> getMySpeedGWB(
            @Query("name") String name
    );

    @GET("/change_radius")
    Call<SimpleResponse> changeRadius(
            @Query("name") String name,
            @Query("radius") Integer radius,
            @Query("cost") Integer cost
    );

    @GET("/init_game")
    Call<SimpleResponse> initGame(
            @Query("name") String name
    );

    @GET("/get_top_users")
    Call<List<UserResponse>> getTopUsers(
            @Query("search") String search,
            @Query("reg") Boolean reg
    );

    @GET("/change_show_hints")
    Call<SimpleResponse> changeHints(
            @Query("name") String name,
            @Query("value") Boolean value
    );

}
