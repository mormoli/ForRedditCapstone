package com.capstone.udacity.forredditcapstone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.Child;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.HomePageAdapter;
import com.capstone.udacity.forredditcapstone.utils.TheRedditApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://www.reddit.com";
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.homepage_list_view)
    RecyclerView recyclerView;
    HomePageAdapter homePageAdapter;
    private List<Child> childList;
    private String userAccessToken, userRefreshToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);

        if (!isNetworkConnected()){
            showMessageOnError();
        } else {
            if (getIntent() != null && getIntent().hasExtra("access")) {
                Log.d(TAG, " getting user tokens. ");
                userAccessToken = sharedPreferences.getString("accessToken", null);
                userRefreshToken = sharedPreferences.getString("refreshToken", null);
            }

            if (savedInstanceState == null && sharedPreferences.getString("accessToken", null) == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("user", "new user");
                startActivity(intent);
            } else {
                getHomePage(sharedPreferences.getString("accessToken", ""));
                homePageAdapter = new HomePageAdapter(childList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(homePageAdapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_homepage:
                getHomePage(userAccessToken);
                return true;
            case R.id.settings_subreddits:
                return true;
            case R.id.settings_favorites:
                return true;
            case R.id.settings_logout:
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        /*final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/
        return true;
    }

    public void getHomePage(String userAccessToken) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "Authorization: Bearer " + userAccessToken;
        //put over18 value in map --> must be false in order to app Safe For Work!!
        Map<String, String> map = new HashMap<>();
        map.put("limit", "25");
        Call<SubredditList> call = theRedditApi.getHomePage(authorization, map);

        call.enqueue(new Callback<SubredditList>() {
            @Override
            public void onResponse(@NonNull Call<SubredditList> call, @NonNull Response<SubredditList> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                Log.d(TAG, " post: " + response.body().getData().getChildren().get(0).getData().getTitle());
                if(childList == null) childList = new ArrayList<>();
                childList.addAll(response.body().getData().getChildren());
            }

            @Override
            public void onFailure(@NonNull Call<SubredditList> call, @NonNull Throwable t) {
                Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }

    /*
     * Method that checks all internet providers
     *
     * Android getAllNetworkInfo() is Deprecated.
     * @see "https://stackoverflow.com/questions/32242384/android-getallnetworkinfo-is-deprecated-what-is-the-alternative"
     * @return internet connection status.
     * */
    @SuppressWarnings("ConstantConditions") //may produce null exception on method !
    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for(Network network : networks){
                networkInfo = connectivityManager.getNetworkInfo(network);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                    return true;
                }
            }
        } else {
            if(connectivityManager != null){
                //noinspection deprecation
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if(networkInfos != null){
                    for(NetworkInfo networkInfo : networkInfos){
                        if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    /*
     *   Toast
     *   show related message to the user.
     * */
    private void showMessageOnError() {
        //finish();
        Toast.makeText(this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //restoring tokens
        userAccessToken = savedInstanceState.getString("accessToken");
        userRefreshToken = savedInstanceState.getString("refreshToken");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving tokens
        outState.putString("accessToken", userAccessToken);
        outState.putString("refreshToken", userRefreshToken);
    }
}
