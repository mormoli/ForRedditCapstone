package com.capstone.udacity.forredditcapstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.capstone.udacity.forredditcapstone.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that takes user to reddit server to log in to their account.
 */
public class LoginActivity extends AppCompatActivity{
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private boolean isUserLogged = false;
    private String accessToken = "";
    private String refreshToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button mSignIn = findViewById(R.id.sign_in_button);
        //final url to request sign in and app permissions
        final String url = Constants.OAUTH_URL + "?client_id=" + Constants.CLIENT_ID +
                "&response_type=code" + "&state=" + Constants.STATE +
                "&redirect_uri=" + Constants.REDIRECT_URI + "&duration=permanent" +
                "&scope=" + Constants.SCOPE;
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "login process started.");
                //Start sign in progress
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();
        //getting user access token or handling error cases while getting token.
        //since multiple activity calls happens during application usage making sure intent started from this activity
        if (getIntent() != null && !getIntent().hasExtra("user")){
            Log.d(TAG, "calling uri");
            Uri uri = getIntent().getData();
            if (uri != null && uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                Log.e(TAG, "An error has occurred: " + error);
            } else {
                assert uri != null;
                String state = uri.getQueryParameter("state");
                assert state != null;
                if (state.equals(Constants.STATE)){
                    String code = uri.getQueryParameter("code");
                    getAccessToken(code);
                }
            }
        }
    }
    //@see 'https://progur.com/2016/10/how-to-use-reddit-oauth2-in-android-apps.html'
    private void getAccessToken(String code){
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "getAccessToken called.");
        String authString = Constants.CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "For Reddit Capstone")
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(Constants.ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=authorization_code&code=" + code +
                "&redirect_uri=" + Constants.REDIRECT_URI))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, " getAccessToken error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();

                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    accessToken = data.optString("access_token");
                    refreshToken = data.optString("refresh_token");
                    //saving tokens in to shared preferences
                    sharedPreferences = getApplicationContext().getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("accessToken", accessToken);
                    editor.putString("refreshToken", refreshToken);
                    editor.apply();
                    isUserLogged = true;
                    Log.d(TAG, "Access token: " + accessToken);
                    Log.d(TAG, "Refresh token: " + refreshToken);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("access", "granted");
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

