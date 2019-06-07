package com.example.gamewithnoname.ServerConnection;

import android.support.annotation.Nullable;
import android.util.Log;

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
    private String result = "-1";
    private ServerAPIs serverAPIs;
    private Call call;

    public ConnectionServer(){
        // prepare to connect
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        serverAPIs = retrofit.create(ServerAPIs.class);

    }

    public void initLogin(String name, String password){
        call = serverAPIs.getResultSignIn(
                name,
                password
        );
    }

    public void initRegistration(String name, String password, String birthday, Integer sex){
        call = serverAPIs.getResultSignUp(
                name,
                password,
                birthday,
                sex
        );
    }

    public void initEventHandler(){
        call = serverAPIs.getResultEvent();
    }

    public void initPutOnline(String name){
        call = serverAPIs.putOnline(name);
    }

    public void initCreateGame(String name, double latit, double longit){
        call = serverAPIs.createGame(name, latit, longit);
    }

    public void initJoinGame(String name, double latit, double longit, String key){
        call = serverAPIs.joinGame(name, latit, longit, key);
    }

    public void connect(@Nullable final SimpleCallbacks callbacks) {
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
                    Log.i(TAG, ((SimpleResponse) response.body()).getResult());
                    result = ((SimpleResponse) response.body()).getResult();
                }

                if (callbacks != null){
                    callbacks.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i(TAG, "error!");
                if (callbacks != null){
                    callbacks.onError(t);
                }
            }
        });
    }

    public void connect(@Nullable final PointsCallbacks callbacks) {
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
                    Log.i(TAG, ((SimpleResponse) response.body()).getResult());
                    result = ((SimpleResponse) response.body()).getResult();
                }

                if (callbacks != null){
                    callbacks.onSuccess(result);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.i(TAG, "error!");
                if (callbacks != null){
                    callbacks.onError(t);
                }
            }
        });
    }

}
