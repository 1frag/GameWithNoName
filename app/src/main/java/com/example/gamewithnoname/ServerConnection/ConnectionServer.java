package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gamewithnoname.callbacks.BeginGameCallbacks;
import com.example.gamewithnoname.callbacks.ChangeCoinsCallbacks;
import com.example.gamewithnoname.callbacks.CheckGWBCallbacks;
import com.example.gamewithnoname.callbacks.CheckGameCallbacks;
import com.example.gamewithnoname.callbacks.CreateGWBCallbacks;
import com.example.gamewithnoname.callbacks.CreateGameCallbacks;
import com.example.gamewithnoname.callbacks.GetMessagesCallbacks;
import com.example.gamewithnoname.callbacks.GetUsersCallbacks;
import com.example.gamewithnoname.callbacks.JoinGameCallbacks;
import com.example.gamewithnoname.callbacks.KickPlayerCallbacks;
import com.example.gamewithnoname.callbacks.KillRGCallbacks;
import com.example.gamewithnoname.callbacks.SendMessageCallbacks;
import com.example.gamewithnoname.callbacks.SignUpCallbacks;
import com.example.gamewithnoname.callbacks.UpdateStateCallbacks;
import com.example.gamewithnoname.models.responses.CheckGWBResponse;
import com.example.gamewithnoname.models.responses.CheckGameResponse;
import com.example.gamewithnoname.models.responses.DialogResponse;
import com.example.gamewithnoname.models.responses.GameStateResponse;
import com.example.gamewithnoname.callbacks.SignInCallbacks;
import com.example.gamewithnoname.models.responses.UserResponse;
import com.example.gamewithnoname.callbacks.SimpleCallbacks;
import com.example.gamewithnoname.models.responses.SimpleResponse;

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

    private final String TAG = "HITS/ConnectionServer";
    private final String BASE_URL = "https://ifrag.herokuapp.com";
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

    public void initChangeCoins(String token, Integer count) {
        call = serverAPIs.changeCoins(
                token,
                count
        );
    }

    public void initChangeRating(String token, Integer count) {
        call = serverAPIs.changeRating(
                token,
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

    public void initCreateGame(String token, int type) {
        call = serverAPIs.createGame(token, type);
    }

    public void initJoinGame(String token, String key) {
        call = serverAPIs.joinGame(token, key);
    }

    public void initUpdateMap(String token, double latit, double longit, int messages, int coins) {
        call = serverAPIs.updateMap(token, latit, longit, messages, coins);
    }

    public void initBeginGame(String token, Integer duration) {
        call = serverAPIs.beginGame(token, duration);
    }

    public void initKillRunGame(String token) {
        // todo: понять почему необходимо передать что-то кроме токена
        call = serverAPIs.killRunGame(token, 45); // 45 - это заглушка, чтобы работало
    }

    public void initSendMessage(String token, String text) {
        call = serverAPIs.sendMessage(token, text);
    }

    public void initGetNewMessages(String token, int flag) {
        call = serverAPIs.getNewMessages(token, flag);
    }

    public void initCheckGame(String token) {
        call = serverAPIs.checkGame(token);
    }

    public void initKickPlayer(String token, String target) {
        call = serverAPIs.kickPlayer(token, target);
    }

    public void initKillGWB(String name) {
        call = serverAPIs.killGWB(name);
    }

    public void initCheckGWB(String name) {
        call = serverAPIs.checkGWB(name);
    }

    public void initCreateGWB(
            String name, Integer alpha, Double speed,
            Double bla, Double blo, Double ela, Double elo
    ) {
        call = serverAPIs.createGWB(name, alpha, speed, bla, blo, ela, elo);
    }

    public void initUpdateGWB(String name, Integer first, Double latitude, Double longitude) {
        call = serverAPIs.updateGWB(name, first, latitude, longitude);
    }

    public void initGetMySpeedGWB(String name) {
        call = serverAPIs.getMySpeedGWB(name);
    }

    public void initChangeRadius(String token, Integer radius, Integer cost) {
        call = serverAPIs.changeRadius(token, radius, cost);
    }

    public void initInitGame(String token) {
        call = serverAPIs.initGame(token);
    }

    public void initGetTopUsers(String search, Boolean reg) {
        call = serverAPIs.getTopUsers(search, reg);
    }

    public void initChangeHints(String token, Boolean value) {
        call = serverAPIs.changeHints(token, value);
    }

    public void connectSimple(@Nullable final SimpleCallbacks callbacks) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null) {
                    callbacks.onSuccess(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
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
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
            }
        });
    }

    public void connectKillRG(final KillRGCallbacks callback) {
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    callback.success();
                } else {
                    callback.someProblem(new Throwable(
                            String.format(
                                    "code=%s; reason=%s",
                                    response.code(),
                                    response.message()
                            )
                    ));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
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
                        callback.success(response.body().getResult());
                    }
                }
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
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
            }
        });
    }

    public void connectKickPlayer(final KickPlayerCallbacks callbacks) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callbacks != null) {
                    callbacks.success();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callbacks != null)
                    callbacks.someProblem(t);
            }
        });
    }

    public void connectSignUp(final SignUpCallbacks callbacks) {
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                switch (response.raw().code()) {
                    case 201:
                        callbacks.success();
                        break;
                    case 211:
                        callbacks.nameAlreadyExists();
                    case 500:
                        callbacks.someProblem(new Throwable("Internal Server Error"));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
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

                    if (response.body().getStats() != null)
                        callback.gameOver(response.body().getStats());

                    if (response.body().getAuthor() != null)
                        callback.changeOwn(response.body().getAuthor());

                    if (response.body().getProgress() != null)
                        callback.changeProgress(response.body().getProgress());

                    if (response.body().getType_game() != null)
                        callback.changeTypeGame(response.body().getType_game());

                    if (response.body().getTimer() != null)
                        callback.changeTimer(response.body().getTimer());
                }
            }

            @Override
            public void onFailure(Call<GameStateResponse> call, Throwable t) {
                Log.i(TAG, "onFailure in GameStateResponse");
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void connectLogin(final SignInCallbacks callback) {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.raw().code() == 401) {
                    callback.permissionDenied();
                    return;
                }

                if (response.body() == null) {
                    Log.i(TAG, "Server return empty response");
                    callback.someProblem(null);
                    return;
                }

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
                        user.getDateSignUp(),
                        user.getHints(),
                        user.getToken()
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
                    if (value == -1) {
                        callback.notEnoughMoney();
                    } else {
                        callback.successful(value);
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

    public void connectCheckGWB(@Nullable final CheckGWBCallbacks callback) {
        call.enqueue(new Callback<CheckGWBResponse>() {

            @Override
            public void onResponse(Call<CheckGWBResponse> call, Response<CheckGWBResponse> response) {
                if (response.body() != null && callback != null) {
                    CheckGWBResponse r = response.body();
                    int value = r.getResult();
                    if (value == 2) {
                        callback.isFree();
                    } else {
                        callback.gameExist(
                                r.getAlpha(),
                                r.getSpeed(),
                                r.getTime(),
                                r.getStops(),
                                r.getBla(),
                                r.getBlo(),
                                r.getEla(),
                                r.getElo()
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckGWBResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }

        });
    }

    public void connectCreateGWB(final CreateGWBCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    callback.success();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }

        });
    }

    public void connectChangeRadius(final ChangeCoinsCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1)
                        callback.notEnoughMoney();
                    else
                        callback.successful(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }

        });
    }

    public void connectInitGame(final BeginGameCallbacks callback) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1) {
                        callback.youAreNotAuthor();
                    } else if (response.body().getResult() == -2) {
                        callback.notEnoughMan();
                    } else {
                        callback.success(response.body().getResult() / 60);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }

        });
    }

    public void connectGetTopUsers(final GetUsersCallbacks callback) {
        call.enqueue(new Callback<List<UserResponse>>() {

            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.body() != null) {
                    callback.success(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                callback.failed(t);
                Log.i(TAG, t.getMessage());
            }
        });
    }

}
