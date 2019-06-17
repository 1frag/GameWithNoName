package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gamewithnoname.callbacks.BeginGameCallbacks;
import com.example.gamewithnoname.callbacks.ChangeCoinsCallbacks;
import com.example.gamewithnoname.callbacks.CheckGameCallbacks;
import com.example.gamewithnoname.callbacks.CreateGameCallbacks;
import com.example.gamewithnoname.callbacks.GetMessagesCallbacks;
import com.example.gamewithnoname.callbacks.JoinGameCallbacks;
import com.example.gamewithnoname.callbacks.KillRGCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.callbacks.SignUpCallbacks;
import com.example.gamewithnoname.callbacks.UpdateStateCallbacks;
import com.example.gamewithnoname.models.responses.CheckGameResponse;
import com.example.gamewithnoname.models.responses.DialogResponse;
import com.example.gamewithnoname.models.responses.GameStateResponse;
import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.callbacks.SignInCallbacks;
import com.example.gamewithnoname.models.responses.UserResponse;
import com.example.gamewithnoname.models.responses.PointsResponse;
import com.example.gamewithnoname.callbacks.SimpleCallbacks;
import com.example.gamewithnoname.models.responses.SimpleResponse;

import java.util.ArrayList;

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

    public void initSignIn(String name, String password) {
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

    public void initSignUp(String name, String password, String birthday, Integer sex) {
        call = serverAPIs.getResultSignUp(
                name,
                password,
                birthday,
                sex
        );
    }

    public void initCreateGame(String name, int duration, int type) {
        call = serverAPIs.createGame(name, duration, type);
    }

    public void initJoinGame(String name, String key) {
        call = serverAPIs.joinGame(name, key);
    }

    public void initUpdateMap(String name, double latit, double longit, int messages, int coins) {
        call = serverAPIs.updateMap(name, latit, longit, messages, coins);
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

    public void initGetNewMessages(String name, int flag) {
        call = serverAPIs.getNewMessages(name, flag);
    }

    public void initCheckGame(String name) {
        call = serverAPIs.checkGame(name);
    }

    private void reportStatusCode(int code, String fun) {
        if (code != 200) {
            Log.i(TAG, String.format("code != 200 :: %s", fun));
        }
    }

    public void connectSimple(@Nullable final SimpleCallbacks callbacks) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callbacks != null) {
                    callbacks.onSuccess(response.body().getResult());
                }
                reportStatusCode(response.code(), "connectSimple");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {

            }
        });
    }

    public void connectCreateGame(@Nullable final CreateGameCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1)
                        callback.aLotOfGames();
                    else
                        callback.success(response.body().getResult());
                }
                reportStatusCode(response.code(), "connectSimple");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
            }
        });
    }

    public void connectKillRG(@Nullable final KillRGCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    callback.success();
                }
                reportStatusCode(response.code(), "connectSimple");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
            }
        });
    }

    public void connectBeginGame(@Nullable final BeginGameCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1) {
                        callback.youAreNotAuthor();
                    } else if (response.body().getResult() == -2) {
                        callback.notEnoughMan();
                    } else if (response.body().getResult() == 1) {
                        callback.success();
                    }
                }
                reportStatusCode(response.code(), "connectSimple");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
            }
        });
    }

    public void connectJoinGame(@Nullable final JoinGameCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1)
                        callback.invalidLink();
                    else if (response.body().getResult() == -2)
                        callback.gameIsStarted();
                    else
                        callback.success(response.body().getResult());
                }
                reportStatusCode(response.code(), "connectSimple");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
            }
        });
    }

    public void connectSignUp(@Nullable final SignUpCallbacks callbacks) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callbacks != null) {
                    if (response.body().getResult() == 1)
                        callbacks.success();
                    else
                        callbacks.nameAlreadyExists();
                }
                reportStatusCode(response.code(), "connectSimple");
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callbacks != null)
                    callbacks.someProblem(t);
            }
        });
    }

    public void connectCheckGame(@Nullable final CheckGameCallbacks callback) {
        call.enqueue(new Callback<CheckGameResponse>() {

            @Override
            public void onResponse(Call<CheckGameResponse> call, Response<CheckGameResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = response.body().getResult();
                    if (value == 1) {
                        callback.inFree();
                        return;
                    }
                    String link = response.body().getLink();
                    int type = (response.body().getOwn() == 1 ? CREATOR : JOINER);
                    int is_run = response.body().getRun();

                    if (is_run == 1) {
                        callback.inRun(link, type);
                    } else {
                        callback.inWait(response.body().getLink(), type);
                    }

                }
            }

            @Override
            public void onFailure(Call<CheckGameResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectCheckGame");
                // todo: add someProblem
            }
        });
    }

    public void connectUpdateState(@Nullable final UpdateStateCallbacks callback) {
        call.enqueue(new Callback<GameStateResponse>() {

            @Override
            public void onResponse(Call<GameStateResponse> call, Response<GameStateResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getGamers() != null)
                        callback.gamersUpdate(response.body().getGamers());

                    if (response.body().getMessages() != null)
                        callback.messagesUpdate(response.body().getMessages());

                    if (response.body().getCoins() != null)
                        callback.coinsUpdate(response.body().getCoins());

                    if (response.body().getLink() != null)
                        callback.linkUpdate(response.body().getLink());

                    if (response.body().getState() != null)
                        callback.gameOver(response.body().getStats());
                }
            }

            @Override
            public void onFailure(Call<GameStateResponse> call, Throwable t) {
                Log.i(TAG, "onFailure in GameStateResponse");
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void connectLogin(@Nullable final SignInCallbacks callback) {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = response.body().getResult();
                    if (value == -1) {
                        callback.permissionDenied();
                        return;
                    }
                    UserResponse user = response.body();
                    callback.baseSettingsAccount(
                            user.getName(),
                            user.getPassword()
                    );
                    callback.otherSettingsAccount(
                            user.getSex(),
                            user.getBirthday(),
                            user.getDateSignUp()
                    );
                    callback.capital(
                            user.getMoney(),
                            user.getRating()
                    );
                    callback.statsData(
                            user.getMileage()
                    );
                    callback.success(user);
                }
                reportStatusCode(response.code(), "connectLogin");
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (callback != null) {
                    callback.someProblem(t);
                    Log.i(TAG, String.format("connectLogin.onFailure %s", t.getMessage()));
                }
            }
        });
    }

    public void connectChangeCoins(@Nullable final ChangeCoinsCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    int value = response.body().getResult();
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
                    int value = response.body().getResult();
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
