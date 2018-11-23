package com.capstone.udacity.forredditcapstone;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.util.Log;
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

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private String userAccessToken, userRefreshToken, subredditName, postId;
    private String customizedComments, customizedPoints;
    private SharedPreferences sharedPreferences;
    private Parcelable recyclerViewState;
    private PostData postData;
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
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save button used
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

        call.enqueue(new Callback<List<CommentList>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommentList>> call, @NonNull Response<List<CommentList>> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                if(commentList == null) commentList = new ArrayList<>();
                if(response.code() == 200) {
                    //Log.d(TAG, " author : " + response.body().get(1).getData().getChildren().get(1).getData().getAuthor());
                    Log.d(TAG, " Server Response Body: " + response.body().toString());
                    Log.d(TAG, " Comment Author: " + response.body().get(1).getData().getChildren().get(0).getData().getAuthor());
                    Log.d(TAG, " Comment :" + response.body().get(1).getData().getChildren().get(0).getData().getBody());
                    Log.d(TAG, " all comments size: " +response.body().get(1).getData().getChildren().size());
                    for(int i=0; i < response.body().get(1).getData().getChildren().size(); i++ )
                        commentList.add(response.body().get(1).getData().getChildren().get(i).getData());
                    //populating list view
                    populateListView();
                } else {
                    Log.d(TAG, " response code: " + response.code());
                    if(refreshCount < 2) getAccessToken();
                    else Toast.makeText(getApplicationContext(), getString(R.string.token_refresh_error), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<CommentList>> call, @NonNull Throwable t) {
                Log.e(TAG, "Retrofit Error : " + t.getMessage());
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
                //save comment to favorite database also send post to server in order to save user saved data.
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

    /*
     * Method that refresh's the given token
     * If you request permanent access, then you will need to refresh the tokens after 1 hour.
     * */
    private void getAccessToken(){
        refreshCount++;
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "getAccessToken called.");
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
                Log.e(TAG, " getAccessToken error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                assert response.body() != null;
                String json = response.body().string();

                JSONObject data = null;

                try {
                    data = new JSONObject(json);
                    //get new access token
                    userAccessToken = data.optString("access_token");
                    //replace value in shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("accessToken", userAccessToken);
                    //editor.putString("refreshToken", userRefreshToken);
                    editor.apply();
                    getPostComments();
                    Log.d(TAG, "Access token: " + userAccessToken);
                    Log.d(TAG, "Refresh token: " + userRefreshToken);
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
        }
    }
}
