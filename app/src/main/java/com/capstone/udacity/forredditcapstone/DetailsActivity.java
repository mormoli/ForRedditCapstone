package com.capstone.udacity.forredditcapstone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.capstone.udacity.forredditcapstone.model.CommentList;
import com.capstone.udacity.forredditcapstone.model.CommentReader;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.TheRedditApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String BASE_OAUTH_URL = "https://oauth.reddit.com";
    private String userAccessToken;
    private List<CommentList> commentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        userAccessToken = sharedPreferences.getString("accessToken", "");
        if(getIntent().hasExtra("postId")){
            String subredditName = getIntent().getStringExtra("subredditName");
            String postId = getIntent().getStringExtra("postId");
            getPostComments(subredditName, postId);
        }
    }

    public void getPostComments(String subredditName, String postId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, String> map = new HashMap<>();
        map.put("limit", "25");

        Call<CommentReader> call = theRedditApi.getPostComments(authorization, subredditName, postId, map);

        call.enqueue(new Callback<CommentReader>() {
            @Override
            public void onResponse(@NonNull Call<CommentReader> call, @NonNull Response<CommentReader> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                if(commentList == null) commentList = new ArrayList<>();
                if(response.code() == 200) {
                    //Log.d(TAG, " author : " + response.body().get(1).getData().getChildren().get(1).getData().getAuthor());
                    //https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
                    Log.d(TAG, " Server Response Body: " + response.body().toString());
                    //https://codereview.stackexchange.com/questions/203507/saving-reddit-posts-and-comments-from-json-into-arraylists
                    //populateUI()
                }

            }

            @Override
            public void onFailure(@NonNull Call<CommentReader> call, @NonNull Throwable t) {
                Log.e(TAG, "Retrofit Error : " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
