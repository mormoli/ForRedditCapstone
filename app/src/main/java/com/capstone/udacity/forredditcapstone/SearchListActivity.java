package com.capstone.udacity.forredditcapstone;

import android.content.Intent;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.search.SearchData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.SearchFragmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SearchListActivity extends AppCompatActivity implements ResponseReceiver.OnResponse{
    @BindView(R.id.search_list_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = SearchListActivity.class.getSimpleName();
    private String userAccessToken, userRefreshToken;
    private SharedPreferences sharedPreferences;
    private Parcelable recyclerViewState;
    private List<SearchData> searchData;
    private ResponseReceiver mReceiver;
    private String actionText;
    private int mPosition;
    private SearchFragmentAdapter searchFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        userAccessToken = sharedPreferences.getString("accessToken", null);
        userRefreshToken = sharedPreferences.getString("refreshToken", null);

        if(getIntent() != null && getIntent().hasExtra("searchData")){
            searchData = getIntent().getParcelableArrayListExtra("searchData");
            searchFragmentAdapter = new SearchFragmentAdapter(searchData);
            searchFragmentAdapter.setOnClick(new SearchFragmentAdapter.OnItemClicked() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(TAG, " button clicked: " + ((Button) view).getText().toString());
                    Log.d(TAG, " subreddit name : " + searchData.get(position).getDisplayNamePrefixed());
                    String name = ((Button) view).getText().toString();
                    if(name.equals("subscribe")){
                        String action = "sub";
                        actionText = "Subscribed";
                        Intent intent = new Intent(getApplicationContext(), RedditPostService.class);
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("accessToken", userAccessToken);
                        intent.putExtra("action", action);
                        intent.putExtra("srName", searchData.get(position).getFullName());
                        intent.setAction(Constants.API_SUBSCRIBE);
                        startService(intent);
                        mPosition = position;
                    } else {
                        String action = "unsub";
                        actionText = "Unsubscribed";
                        Intent intent = new Intent(getApplicationContext(), RedditPostService.class);
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("accessToken", userAccessToken);
                        intent.putExtra("action", action);
                        intent.putExtra("srName", searchData.get(position).getFullName());
                        intent.setAction(Constants.API_SUBSCRIBE);
                        startService(intent);
                        mPosition = position;
                    }
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(searchFragmentAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
    }

    /*
     * Method that refresh's the given token
     * If you request permanent access, then you will need to refresh the tokens after 1 hour.
     * */
    private void getAccessToken(){
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
        if(savedInstanceState != null){
            searchData = savedInstanceState.getParcelableArrayList("searchData");
            recyclerViewState = savedInstanceState.getParcelable("scroll_state");
        }
    }
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(recyclerView != null && searchData != null){
            outState.putParcelableArrayList("searchData", (ArrayList<? extends Parcelable>) searchData);
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable("scroll_state", recyclerViewState);
        }
    }

    @Override
    public void onResponseReceived(int resultCode, Bundle resultData) {
        if(resultCode == 200){
            Toast.makeText(this, actionText + " successfully.", Toast.LENGTH_SHORT).show();
            if(actionText.equals("Subscribed")) {
                searchData.get(mPosition).setUserIsSubscriber(true);
                //searchFragmentAdapter.notifyDataSetChanged();
                searchFragmentAdapter.notifyItemChanged(mPosition);
            }else{
                searchData.get(mPosition).setUserIsSubscriber(false);
                //searchFragmentAdapter.notifyDataSetChanged();
                searchFragmentAdapter.notifyItemChanged(mPosition);
            }
        } else if(resultCode == 401){
            //try to refresh token.
            Toast.makeText(this,getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
            getAccessToken();
        } else {
            //403 or something else happened, warn user.
            Toast.makeText(this,getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
        }
    }
}
