package com.capstone.udacity.forredditcapstone;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.TheRedditApi;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RedditPostService extends IntentService {
    private static final String TAG = RedditPostService.class.getSimpleName();
    private ResultReceiver mReceiver;
    public RedditPostService(){
        super(TAG);
        //@see 'https://developer.android.com/reference/android/app/IntentService#setIntentRedelivery(boolean)'
        setIntentRedelivery(false);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        String fullName, userAccessToken;
        if(intent.getAction() != null){
            switch (intent.getAction()){
                case Constants.API_HIDE :
                    mReceiver = intent.getParcelableExtra("receiver");
                    fullName = intent.getStringExtra("name");
                    userAccessToken = intent.getStringExtra("accessToken");
                    onHideClicked(userAccessToken, fullName);
                    break;
                case Constants.API_SAVE :
                    mReceiver = intent.getParcelableExtra("receiver");
                    fullName = intent.getStringExtra("name");
                    userAccessToken = intent.getStringExtra("accessToken");
                    onSaveClicked(userAccessToken, fullName);
                    break;
                case Constants.API_SUBSCRIBE :
                    mReceiver = intent.getParcelableExtra("receiver");
                    userAccessToken = intent.getStringExtra("accessToken");
                    String action = intent.getStringExtra("action");
                    String srName = intent.getStringExtra("srName");
                    onConfirmClicked(userAccessToken, action, false, srName);
                    break;
            }
        }
    }
    /*
    * Method that handles user hide button click on reddit posts
    * */
    public void onHideClicked(String userAccessToken, String fullName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, String> map = new HashMap<>();
        map.put("id", fullName);
        Call<ResponseBody> responseBodyCall = theRedditApi.onHideClicked(authorization, map);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, " server response: " + response.toString());
                Log.d(TAG, " response code: " + response.code());
                Bundle bundle = new Bundle();
                bundle.putString("data", response.toString());
                mReceiver.send(response.code(), bundle);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "(HIDE) retrofit error: " + t.getMessage());
            }
        });
    }
    /*
    * Method that handles user save button click on reddit post or comments
    * */
    public void onSaveClicked(String userAccessToken, String fullName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, String> map = new HashMap<>();
        map.put("id", fullName);
        Call<ResponseBody> responseBodyCall = theRedditApi.onSaveClicked(authorization, map);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, " server response: " + response.toString());
                Log.d(TAG, " response code: " + response.code());
                Bundle bundle = new Bundle();
                bundle.putString("data", response.toString());
                mReceiver.send(response.code(), bundle);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "(SAVE) retrofit error: " + t.getMessage());
            }
        });
    }
    /*
    * Method that handles user subscribe or unsubscribe action to subreddit.
    * e.g sub, false, subredditname
    * */
    public void onConfirmClicked(String userAccessToken,String action, boolean skipDefaults, String srName){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, String> map = new HashMap<>();
        Call<ResponseBody> responseBodyCall = theRedditApi.onConfirmClicked(authorization, action, skipDefaults, srName);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, " server response: " + response.toString());
                Log.d(TAG, " response code: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "(CONFIRM) retrofit error: " + t.getMessage());
            }
        });
    }
}
