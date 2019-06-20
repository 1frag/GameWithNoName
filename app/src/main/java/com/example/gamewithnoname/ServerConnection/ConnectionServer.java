package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

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
import com.example.gamewithnoname.models.responses.GamersResponse;
import com.example.gamewithnoname.callbacks.SignInCallbacks;
import com.example.gamewithnoname.models.responses.TopUsersResponse;
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

    private void changeEnable(@Nullable ArrayList<View> views, boolean m){
        if (views == null) return;
        for (View view : views){
            view.setEnabled(m);
        }
    }

    public void initSignIn(String name, String password, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.getResultSignIn(
                name,
                password
        );
    }

    public void initChangeCoins(String name, Integer count, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.changeCoins(
                name,
                count
        );
    }

    public void initChangeRating(String name, Integer count, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.changeRating(
                name,
                count
        );
    }

    public void initSignUp(String name, String password, String birthday,
                           Integer sex, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.getResultSignUp(
                name,
                password,
                birthday,
                sex
        );
    }

    public void initCreateGame(String name, int type,
                               @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.createGame(name, type);
    }

    public void initJoinGame(String name, String key, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.joinGame(name, key);
    }

    public void initUpdateMap(String name, double latit, double longit,
                              int messages, int coins, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.updateMap(name, latit, longit, messages, coins);
    }

    public void initBeginGame(String name, Integer duration, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.beginGame(name, duration);
    }

    public void initKillRunGame(String name, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.killRunGame(name);
    }

    public void initSendMessage(String name, String text, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.sendMessage(name, text);
    }

    public void initGetNewMessages(String name, int flag, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.getNewMessages(name, flag);
    }

    public void initCheckGame(String name, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.checkGame(name);
    }

    public void initKickPlayer(String target, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.kickPlayer(target);
    }

    public void initKillGWB(String name, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.killGWB(name);
    }

    public void initCheckGWB(String name, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.checkGWB(name);
    }

    public void initCreateGWB(String name, Integer alpha, Double speed,
                              Double bla, Double blo,
                              Double ela, Double elo, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.createGWB(name, alpha, speed, bla, blo, ela, elo);
    }

    public void initUpdateGWB(String name, Integer first, Double latitude,
                              Double longitude, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.updateGWB(name, first, latitude, longitude);
    }

    public void initGetMySpeedGWB(String name, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.getMySpeedGWB(name);
    }

    public void initChangeRadius(String name, Integer radius, Integer cost, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.changeRadius(name, radius, cost);
    }

    public void initInitGame(String name, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.initGame(name);
    }

    public void initGetTopUsers(String search, Boolean reg, @Nullable ArrayList<View> views) {
        changeEnable(views, false);
        call = serverAPIs.getTopUsers(search, reg);
    }

    private void reportStatusCode(int code, String fun) {
        if (code != 200) {
            Log.i(TAG, String.format("code != 200 :: %s", fun));
        }
    }

    public void connectSimple(@Nullable final SimpleCallbacks callbacks, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callbacks != null) {
                    callbacks.onSuccess(response.body().getResult());
                }
                reportStatusCode(response.code(), "connectSimple");
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                changeEnable(views, true);
            }
        });
    }

    public void connectCreateGame(@Nullable final CreateGameCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
                changeEnable(views, true);
            }
        });
    }

    public void connectKillRG(@Nullable final KillRGCallbacks callback, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    callback.success();
                }
                reportStatusCode(response.code(), "connectSimple");
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
                changeEnable(views, true);
            }
        });
    }

    public void connectBeginGame(@Nullable final BeginGameCallbacks callback, @Nullable final ArrayList<View> views) {
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
                reportStatusCode(response.code(), "connectSimple");
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
                changeEnable(views, true);
            }
        });
    }

    public void connectJoinGame(@Nullable final JoinGameCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callback != null)
                    callback.someProblem(t);
                changeEnable(views, true);
            }
        });
    }

    public void connectKickPlayer(final KickPlayerCallbacks callbacks, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callbacks != null) {
                    callbacks.success();
                }
                reportStatusCode(response.code(), "connectKickPlayer");
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callbacks != null)
                    callbacks.someProblem(t);
                changeEnable(views, true);
            }
        });
    }

    public void connectSignUp(@Nullable final SignUpCallbacks callbacks, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callbacks != null) {
                    if (response.body().getResult() == 1)
                        callbacks.success();
                    else
                        callbacks.nameAlreadyExists();
                }
                reportStatusCode(response.code(), "connectSignUp");
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                if (callbacks != null)
                    callbacks.someProblem(t);
                changeEnable(views, true);
            }
        });
    }

    public void connectCheckGame(@Nullable final CheckGameCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<CheckGameResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectCheckGame");
                // todo: add someProblem
                changeEnable(views, true);
            }
        });
    }

    public void connectUpdateState(@Nullable final UpdateStateCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<GameStateResponse> call, Throwable t) {
                Log.i(TAG, "onFailure in GameStateResponse");
                Log.i(TAG, t.getMessage());
                changeEnable(views, true);
            }
        });
    }

    public void connectLogin(@Nullable final SignInCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (callback != null) {
                    callback.someProblem(t);
                    Log.i(TAG, String.format("connectLogin.onFailure %s", t.getMessage()));
                }
                changeEnable(views, true);
            }
        });
    }

    public void connectChangeCoins(@Nullable final ChangeCoinsCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectChangeCoins");
                changeEnable(views, true);
            }

        });
    }

    public void connectSendMessage(@Nullable final SendMessageCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectSendMessage");
                changeEnable(views, true);
            }

        });
    }

    public void connectGetMessages(@Nullable final GetMessagesCallbacks callback, @Nullable final ArrayList<View> views) {
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<DialogResponse> call, Throwable t) {
                Log.i(TAG, "onFailure --> connectGetMessages");
                changeEnable(views, true);
            }
        });
    }

    public void connectCheckGWB(@Nullable final CheckGWBCallbacks callback, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<CheckGWBResponse>() {

            @Override
            public void onResponse(Call<CheckGWBResponse> call, Response<CheckGWBResponse> response) {
                if (response.body() != null && callback != null) {
                    CheckGWBResponse r = response.body();
                    int value = r.getResult();
                    if (value == 2){
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
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<CheckGWBResponse> call, Throwable t) {
                changeEnable(views, true);
            }

        });
    }

    public void connectCreateGWB(final CreateGWBCallbacks callback, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    callback.success();
                }
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                changeEnable(views, true);
            }

        });
    }

    public void connectChangeRadius(final ChangeCoinsCallbacks callback, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1)
                        callback.notEnoughMoney();
                    else
                        callback.successful(response.body().getResult());
                }
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                changeEnable(views, true);
            }

        });
    }

    public void connectInitGame(final BeginGameCallbacks callback, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && callback != null) {
                    if (response.body().getResult() == -1) {
                        callback.youAreNotAuthor();
                    } else if (response.body().getResult() == -2) {
                        callback.notEnoughMan();
                    } else {
                        callback.success(response.body().getResult());
                    }
                }
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                changeEnable(views, true);
            }

        });
    }

    public void connectGetTopUsers(final GetUsersCallbacks callback, @Nullable final ArrayList<View> views) {
        call.enqueue(new Callback<List<UserResponse>>() {


            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.body() != null && callback != null) {
                    callback.success(response.body());
                }
                changeEnable(views, true);
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                callback.failed(t);
                changeEnable(views, true);
            }
        });
    }

}
