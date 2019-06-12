package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gamewithnoname.ServerConnection.Gamers.GameStateResponse;
import com.example.gamewithnoname.ServerConnection.Gamers.GamersResponse;
import com.example.gamewithnoname.ServerConnection.Login.LoginCallbacks;
import com.example.gamewithnoname.ServerConnection.Login.UserResponse;
import com.example.gamewithnoname.ServerConnection.Points.PointsResponse;
import com.example.gamewithnoname.ServerConnection.Points.PointsCallbacks;
import com.example.gamewithnoname.ServerConnection.Simple.SimpleCallbacks;
import com.example.gamewithnoname.ServerConnection.Simple.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionServer {

    private final String TAG = String.format("%s/%s",
            "HITS",
            "ConnectionServer");
    private final String BASE_URL = "https://nameless-tundra-47166.herokuapp.com";
    private String simpleStringResult = "-1";
    private Integer simpleIntegerResult = -1;
    private ArrayList<PointsResponse> pointResult = new ArrayList<>();
    private ArrayList<GamersResponse> gamersResult = new ArrayList<>();
    private ServerAPIs serverAPIs;
    private Call call;

    public ConnectionServer() {
        // prepare to connectSimple
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        serverAPIs = retrofit.create(ServerAPIs.class);

    }

    public void initLogin(String name, String password) {
        call = serverAPIs.getResultSignIn(
                name,
                password
        );
    }

    public void initRegistration(String name, String password, String birthday, Integer sex) {
        call = serverAPIs.getResultSignUp(
                name,
                password,
                birthday,
                sex
        );
    }

    public void initCreateGame(String name, double latit, double longit) {
        call = serverAPIs.createGame(name, latit, longit);
    }

    public void initJoinGame(String name, double latit, double longit, String key) {
        call = serverAPIs.joinGame(name, latit, longit, key);
    }

    public void initUpdateMap(String name, String key, double latit, double longit) {
        call = serverAPIs.updateMap(name, key, latit, longit);
    }

    public void initUpdateCoins(String key) {
        call = serverAPIs.updateCoins(key);
    }

    public void initBeginGame(String name, String key, Integer duration) {
        call = serverAPIs.beginGame(name, key, duration);
    }

    public void initKillRunGame(String name, String key) {
        call = serverAPIs.killRunGame(name, key);
    }

    public void initGetMoney(String name) {
        call = serverAPIs.getMoney(name);
    }

    public void initGetRating(String name) {
        call = serverAPIs.getRating(name);
    }

    public void connectSimple(@Nullable final SimpleCallbacks callbacks) {
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
                    simpleStringResult = ((SimpleResponse) response.body()).getResult();
                }

                if (callbacks != null) {
                    callbacks.onSuccess(simpleStringResult);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i(TAG, "error!");
                if (callbacks != null) {
                    callbacks.onError(t);
                }
            }
        });
    }

    public void connectPoints(@Nullable final PointsCallbacks callbacks) {
        call.enqueue(new Callback<List<PointsResponse>>() {
            @Override
            public void onResponse(Call<List<PointsResponse>> call, Response<List<PointsResponse>> response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
//                    Log.i(TAG, "Successful");
                    if (response.body().size() == 0) {
                        Log.i(TAG, "Сервер вернул что-то не то");
                        // в тестовом режиме можно вернуть это колбаку,
                        // хотя бы узнаем кто послал такой запрос
                        if (callbacks != null)
                            callbacks.onSuccess(-1, new ArrayList<PointsResponse>());
                        return;
                    }
                    int j = 1;
                    if (response.body().get(0).getLatitude() == -1) {
                        simpleIntegerResult = response.body().get(0).getLongitude().intValue();
                    } else {
                        simpleIntegerResult = 1;
                        j = 0;
                    }
                    pointResult.clear();
                    for (int i = j; i < response.body().size(); i++) {
                        pointResult.add(response.body().get(i));
                    }
                }

                if (callbacks != null) {
                    callbacks.onSuccess(simpleIntegerResult, pointResult);
                }
            }

            @Override
            public void onFailure(Call<List<PointsResponse>> call, Throwable t) {
                Log.i(TAG, "error!");
                if (callbacks != null) {
                    callbacks.onError(t);
                }
            }
        });
    }

    public void connectUpdateState(@Nullable final UpdateStateCallbacks callbacks) {
        call.enqueue(new Callback<GameStateResponse>() {

            @Override
            public void onResponse(Call<GameStateResponse> call, Response<GameStateResponse> response) {
                if (response.body() != null && callbacks != null) {
                    if (response.body().getState() == -1) {
                        Log.i(TAG, "This it");
                    }
                    Log.i(TAG, response.body().getState().toString());
                    switch (response.body().getState()){
                        case 1:
                            // update gamers
                            callbacks.gamersUpdate(
                                    response.body().getGamers()
                            );
                            break;
                        case -1:
                            // update coins
                            callbacks.coinsUpdate(
                                    response.body().getPoints()
                            );
                            break;
                        case -2:
                            // game over, pick statistics
                            callbacks.gameOver(
                                    response.body().getStats()
                            );
                            break;
                    }

                }
            }

            @Override
            public void onFailure(Call<GameStateResponse> call, Throwable t) {
                Log.i(TAG, "onFailure in GameStateResponse");
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void connectLogin(@Nullable final LoginCallbacks callback) {
        call.enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = response.body().getResult();
                    switch (value){
                        case 1:
                            callback.onSuccess(
                                    response.body().getName(),
                                    response.body().getMoney(),
                                    response.body().getRating()
                            );
                            break;
                        case 0:
                            callback.permissionDenied();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (callback != null){
                    callback.errorConnection();
                }
            }

        });
    }

}
