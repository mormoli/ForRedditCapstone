package com.capstone.udacity.forredditcapstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.CommentData;
import com.capstone.udacity.forredditcapstone.model.CommentList;
import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.utils.CommentsAdapter;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.TheRedditApi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity implements ResponseReceiver.OnResponse{
    //private static final String TAG = DetailsActivity.class.getSimpleName();
    private String userAccessToken, userRefreshToken, subredditName, postId;
    private String customizedComments, customizedPoints;
    private SharedPreferences sharedPreferences;
    private Parcelable recyclerViewState;
    private PostData postData;
    private ResponseReceiver mReceiver;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    //view elements
    @BindView(R.id.details_list_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.card_header_text)
    TextView cardHeader;
    @BindView(R.id.post_title_text)
    TextView postTitle;
    @BindView(R.id.thumbnail_image_view)
    ImageView postImage;
    @BindView(R.id.card_subtitle_text)
    TextView postSubText;
    @BindView(R.id.points_text_view)
    TextView postPoints;
    @BindView(R.id.comments_text_view)
    TextView postCommentSize;
    @BindView(R.id.hide_button)
    ImageButton hideButton;
    @BindView(R.id.save_button)
    ImageButton saveButton;
    private List<CommentData> commentList;
    private int refreshCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        userAccessToken = sharedPreferences.getString("accessToken", null);
        userRefreshToken = sharedPreferences.getString("refreshToken", null);

        //setting receiver object for intent service class
        mReceiver = new ResponseReceiver(new Handler());
        mReceiver.setReceiver(this);
        //set data from intent
        if(getIntent().hasExtra("postId") && savedInstanceState == null){
            //PostData class return all objects for header
            postData = getIntent().getParcelableExtra("postData");
            //getting points and comments
            customizedPoints = getIntent().getStringExtra("points");
            customizedComments = getIntent().getStringExtra("comments");
            //populating header view
            populateHeader(postData);
            //subredditname and id for querying comment data from reddit
            subredditName = getIntent().getStringExtra("subredditName");
            postId = getIntent().getStringExtra("postId");
            //query with post id and subreddit name in order to get comments.
            getPostComments();
        }
    }
    /*
    * Method that populates post card header elements
    * */
    public void populateHeader(PostData postData){
         cardHeader.setText(postData.getSubredditNamePrefixed());
         //if post title text exist show or visibility gone
         if(!TextUtils.isEmpty(postData.getTitle())){
             postTitle.setText(postData.getTitle());
         }
         else postTitle.setVisibility(View.GONE);
         //if post has image show image or visibility gone
         if(!TextUtils.isEmpty(postData.getImageDetailURL())){
             Picasso.get()
                     .load(postData.getImageDetailURL())
                     .into(postImage);
         } else postImage.setVisibility(View.GONE);
         //if post has sub text show or visibility gone
        if(!TextUtils.isEmpty(postData.getSelftext())) postSubText.setText(postData.getSelftext());
        else postSubText.setVisibility(View.GONE);
        postPoints.setText(customizedPoints);
        postCommentSize.setText(customizedComments);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide button used
                Toast.makeText(getApplicationContext(), "Please use home page to save/hide posts.", Toast.LENGTH_SHORT).show();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save button used
                Toast.makeText(getApplicationContext(), "Please use home page to save/hide posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPostComments(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, String> map = new HashMap<>();
        map.put("limit", "25");

        Call<List<CommentList>> call = theRedditApi.getPostComments(authorization, subredditName, postId, map);
        //returning only root comments in other word just post related comments not all comments of comments ...
        call.enqueue(new Callback<List<CommentList>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommentList>> call, @NonNull Response<List<CommentList>> response) {
                //Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                if(commentList == null) commentList = new ArrayList<>();
                if(commentList.size() > 0 ) commentList.clear();
                if(response.code() == 200) {
                    //Log.d(TAG, " author : " + response.body().get(1).getData().getChildren().get(1).getData().getAuthor());
                    if(response.body().get(1).getData().getChildren().size() > 0) {
                        //Log.d(TAG, " Server Response Body: " + response.body().toString());
                        //Log.d(TAG, " Comment Author: " + response.body().get(1).getData().getChildren().get(0).getData().getAuthor());
                        //Log.d(TAG, " Comment :" + response.body().get(1).getData().getChildren().get(0).getData().getBody());
                        // size() - 1 last item is null.
                        for (int i = 0; i < response.body().get(1).getData().getChildren().size() - 1; i++)
                            commentList.add(response.body().get(1).getData().getChildren().get(i).getData());
                        //populating list view
                        populateListView();
                    }
                } else {
                    //Log.d(TAG, " response code: " + response.code());
                    if(refreshCount < 2) getAccessToken("comments");
                    else Toast.makeText(getApplicationContext(), getString(R.string.token_refresh_error), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<CommentList>> call, @NonNull Throwable t) {
                //Log.e(TAG, "Retrofit Error : " + t.getMessage());
            }
        });
    }
    /*
    * Method that populates list view
    * */
    public void populateListView(){
        CommentsAdapter commentsAdapter = new CommentsAdapter(commentList, new CommentsAdapter.CommentListener() {
            @Override
            public void onSaveButtonClicked(View view, int position) {
                //send post to server in order to save user saved data.
                String fullName = commentList.get(position).getFullName();
                Intent intent = new Intent(getApplicationContext(), RedditPostService.class);
                intent.putExtra("receiver", mReceiver);
                intent.putExtra("accessToken", userAccessToken);
                intent.putExtra("name", fullName);
                intent.setAction(Constants.API_SAVE);
                startService(intent);
                //set latest saved comment to the app widget
                Intent widgetIntent = new Intent(getApplicationContext(), RedditAppWidget.class);
                String header = commentList.get(position).getAuthor() + " " + getTimeAgo(commentList.get(position).getCreatedUTC());
                widgetIntent.putExtra("widgetHeader", header);
                widgetIntent.putExtra("widgetBody", commentList.get(position).getBody());
                widgetIntent.putExtra("widgetOnClick", commentList.get(position).getPermalink());
                widgetIntent.setAction(Constants.UPDATE_ACTION);
                sendBroadcast(widgetIntent);
            }

            @Override
            public void onLayoutClicked(int position) {
                //future job : implement methods / attributes to reply selected comment and send data
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(commentsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    public static String getTimeAgo(long time){
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    /*
     * Method that refresh's the given token
     * If you request permanent access, then you will need to refresh the tokens after 1 hour.
     * */
    private void getAccessToken(final String callerMethod){
        refreshCount++;
        OkHttpClient client = new OkHttpClient();
        //Log.d(TAG, "getAccessToken called.");
        String authString = Constants.CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "android:com.capstone.udacity.forredditcapstone:v1.0 (by /u/mormoli)")
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(Constants.ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=refresh_token&refresh_token=" + userRefreshToken +
                                "&redirect_uri=" + Constants.REDIRECT_URI))
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                //Log.e(TAG, " getAccessToken error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();

                JSONObject data;

                try {
                    data = new JSONObject(json);
                    //get new access token
                    userAccessToken = data.optString("access_token");
                    //replace value in shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("accessToken", userAccessToken);
                    //editor.putString("refreshToken", userRefreshToken);
                    editor.apply();
                    if(callerMethod.equals("comments"))getPostComments();
                    //Log.d(TAG, "Access token: " + userAccessToken);
                    //Log.d(TAG, "Refresh token: " + userRefreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerViewState != null)
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            postData = savedInstanceState.getParcelable("postData");
            customizedPoints = savedInstanceState.getString("points");
            customizedComments = savedInstanceState.getString("comments");
            populateHeader(postData);
            commentList = savedInstanceState.getParcelableArrayList("commentList");
            populateListView();
            recyclerViewState = savedInstanceState.getParcelable("scroll_state");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(recyclerView != null && commentList != null){
            outState.putParcelableArrayList("commentList", (ArrayList<? extends Parcelable>) commentList);
            outState.putParcelable("postData", postData);
            outState.putString("points", customizedPoints);
            outState.putString("comments", customizedComments);
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable("scroll_state", recyclerViewState);
        }
    }

    @Override
    public void onResponseReceived(int resultCode, Bundle resultData) {
        if(resultCode == 200){
            //save comment to database and show message to the user.
            Toast.makeText(this, "Comment saved successfully.", Toast.LENGTH_SHORT).show();
        } else if(resultCode == 401){
            //try to refresh token.
            Toast.makeText(this,getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
            getAccessToken("refresh");
        } else {
            //403 or something else happened, warn user.
            Toast.makeText(this,getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
        }
    }
}
