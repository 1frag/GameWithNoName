package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gamewithnoname.callbacks.ChangeCoinsCallbacks;
import com.example.gamewithnoname.callbacks.CheckGameCallbacks;
import com.example.gamewithnoname.callbacks.GetMessagesCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.callbacks.UpdateStateCallbacks;
import com.example.gamewithnoname.models.responses.CheckGameResponse;
import com.example.gamewithnoname.models.responses.DialogResponse;
import com.example.gamewithnoname.models.responses.GameStateResponse;
import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.callbacks.LoginCallbacks;
import com.example.gamewithnoname.models.responses.MessageResponse;
import com.example.gamewithnoname.models.responses.UserResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.callbacks.SimpleCallbacks;
import com.example.gamewithnoname.models.responses.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.gamewithnoname.utils.Constants.CREATOR;
import static com.example.gamewithnoname.utils.Constants.JOINER;

public class ConnectionServer {

    private static final ConnectionServer server = new ConnectionServer();

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

    public static ConnectionServer getInstance() {
        return server;
    }

    private ConnectionServer() {
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

    public void initChangeCoins(String name, Integer count) {
        call = serverAPIs.changeCoins(
                name,
                count
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

    public void initCreateGame(String name, double latit, double longit, int duration, int type) {
        call = serverAPIs.createGame(name, latit, longit, duration, type);
    }

    public void initJoinGame(String name, double latit, double longit, String key) {
        call = serverAPIs.joinGame(name, latit, longit, key);
    }

    public void initUpdateMap(String name, double latit, double longit) {
        call = serverAPIs.updateMap(name, latit, longit);
    }

    public void initBeginGame(String name) {
        call = serverAPIs.beginGame(name);
    }

    public void initKillRunGame(String name) {
        call = serverAPIs.killRunGame(name);
    }

    public void initSendMessage(String name, String text) {
        call = serverAPIs.sendMessage(name, text);
    }

    public void initGetNewMessages(String name) {
        call = serverAPIs.getNewMessages(name);
    }

    public void initCheckGame(String name) {
        call = serverAPIs.checkGame(name);
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

    public void connectCheckGame(@Nullable final CheckGameCallbacks callback) {
        call.enqueue(new Callback<CheckGameResponse>() {

            @Override
            public void onResponse(Call<CheckGameResponse> call, Response<CheckGameResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = response.body().getResult() % 100;
                    int type = (response.body().getResult() > 100 ? CREATOR : JOINER);
                    if (value == 11) callback.inRun(response.body().getLink(), type);
                    if (value == 12) callback.inWait(response.body().getLink(), type);
                    if (value == 13) callback.inFree();
                }
            }

            @Override
            public void onFailure(Call<CheckGameResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectCheckGame");
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
                    if (response.body().getLink() != null) {
                        callbacks.updateLink(response.body().getLink());
                    }
                    Log.i(TAG, response.body().getState().toString());
                    switch (response.body().getState()) {
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
                    switch (value) {
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
                if (callback != null) {
                    callback.errorConnection();
                }
            }

        });
    }

    public void connectChangeCoins(@Nullable final ChangeCoinsCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = Integer.parseInt(response.body().getResult());
                    switch (value) {
                        case -2:
                            callback.badQuery();
                            break;
                        case -3:
                            callback.userDoesNotExist();
                            break;
                        case -4:
                            callback.notEnoughMoney();
                            break;
                        default:
                            callback.successful(value);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectChangeCoins");
            }

        });
    }

    public void connectSendMessage(@Nullable final SendMessageCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = Integer.parseInt(response.body().getResult());
                    if (value == 1) {
                        callback.sended();
                    } else {
                        callback.someProblem(value);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectSendMessage");
            }

        });
    }

    public void connectGetMessages(@Nullable final GetMessagesCallbacks callback) {
        call.enqueue(new Callback<DialogResponse>() {

            @Override
            public void onResponse(Call<DialogResponse> call, Response<DialogResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = response.body().getResult();
                    if (value == 1) {
                        callback.success(response.body().getMessages());
                    } else {
                        callback.problem(value);
                    }
                }
            }

            @Override
            public void onFailure(Call<DialogResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectGetMessages");
            }
        });
    }


}
