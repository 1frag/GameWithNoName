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

    public String fetchServerResult(String name, String password, @Nullable final LoginCallbacks callbacks) {
//        //Obtain an instance of Retrofit by calling the static method.
//        Retrofit retrofit = NetworkClient.getRetrofitClient();
//        /*
//        The main purpose of Retrofit is to create HTTP calls from the Java interface based on the annotation associated with each method. This is achieved by just passing the interface class as parameter to the create method
//        */
//        ServerAPIs serverAPIs = retrofit.create(ServerAPIs.class);
//        /*
//        Invoke the method corresponding to the HTTP request which will return a Call object. This Call object will used to send the actual network request with the specified parameters
//        */

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ServerAPIs serverAPIs = retrofit.create(ServerAPIs.class);

        Call<ServerResponse> call = serverAPIs.getResultLogin(
                name,
                password
        );

        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
//                    ServerResponse serverResponse = response.body();
                    Log.i(TAG, ((ServerResponse) response.body()).getResult());
                    result = ((ServerResponse) response.body()).getResult();
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
        return result;
    }

}
