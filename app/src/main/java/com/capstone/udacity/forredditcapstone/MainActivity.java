package com.capstone.udacity.forredditcapstone;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.Child;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.UserInfo;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.HomePageAdapter;
import com.capstone.udacity.forredditcapstone.utils.TheRedditApi;

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


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    @BindView(R.id.homepage_list_view)
    RecyclerView recyclerView;
    HomePageAdapter homePageAdapter;
    private List<Child> childList;
    private String userAccessToken, userRefreshToken;
    private Parcelable recyclerViewState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        //get tokens from shared preferences if value exist or not null
        if(sharedPreferences.getString("accessToken", null) != null) {
            userAccessToken = sharedPreferences.getString("accessToken", null);
            userRefreshToken = sharedPreferences.getString("refreshToken", null);
        }

        if (!isNetworkConnected()){ //while schedule job check's for connectivity for older api using this method
            showMessageOnError();
        } else {
            if (getIntent() != null && getIntent().hasExtra("access")) {
                Log.d(TAG, " getting user tokens. ");
                userAccessToken = sharedPreferences.getString("accessToken", null);
                userRefreshToken = sharedPreferences.getString("refreshToken", null);
            }

            if (TextUtils.isEmpty(userAccessToken) && TextUtils.isEmpty(userRefreshToken)) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("user", "new user");
                startActivity(intent);
            } else {
                //retrieve data from user's home page
                getHomePage(userAccessToken);
                //get username and save to shared preferences for later use.
                if(TextUtils.isEmpty(sharedPreferences.getString("username", null))) getUserInfo();
            }
        }
    }
    //@see 'https://stackoverflow.com/questions/48527171/detect-connectivity-change-in-android-7-and-above-when-app-is-killed-in-backgrou/48666854#48666854'
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob(){
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)//set to false or true depends on usage. could be battery killer..
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_homepage:
                getHomePage(userAccessToken);
                return true;
            case R.id.settings_subreddits:
                //retrieve subreddit list in activity
                return true;
            case R.id.settings_favorites:
                //retrieve favorite list
                return true;
            case R.id.settings_logout:
                //revoke token
                return true;
            case R.id.settings_search:
                //get search credentials and open view or show toast message if not exists.
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
    //https://www.getpostman.com
    //Postman app used to create returning values
    public void getHomePage(final String userAccessToken) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        //put over18 value in map --> must be off in order to app Safe For Work!!
        Map<String, String> map = new HashMap<>();
        map.put("limit", "25");
        map.put("sort", "new");
        map.put("include_over_18", "off");
        Call<SubredditList> call = theRedditApi.getHomePage(authorization, map);

        call.enqueue(new Callback<SubredditList>() {
            @Override
            public void onResponse(@NonNull Call<SubredditList> call, @NonNull Response<SubredditList> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                if(childList == null) childList = new ArrayList<>();
                if(response.code() == 200) {
                    childList.addAll(response.body().getData().getChildren());
                    Log.d(TAG, " array title: " + childList.get(0).getData().getTitle());
                    Log.d(TAG, " data size: " + childList.size());
                    populateUI(childList);
                } else { //probably error code is 401 --> try refresh the token then call method again
                    Log.d(TAG, " response code: " + response.code());
                    getAccessToken();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubredditList> call, @NonNull Throwable t) {
                Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }

    public void populateUI(final List<Child> childList){
        homePageAdapter = new HomePageAdapter(childList, new HomePageAdapter.ButtonsListener(){

            @Override
            public void onHideButtonClick(View view, int position) {
                //hide post and request to server
            }

            @Override
            public void onSaveButtonClick(View view, int position) {
                //save post with room database also send request save to the reddit
            }

            @Override
            public void onLayoutClicked(int position) {
                //open detailed post view
                String subredditName = childList.get(position).getData().getSubredditName();
                String postId = childList.get(position).getData().getId();
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("subredditName", subredditName);
                intent.putExtra("postId", postId);
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(homePageAdapter);
    }
    private void getUserInfo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;

        Call<UserInfo> call = theRedditApi.getUserInfo(authorization);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                Log.d(TAG, " username: " + response.body().getUserName());
                Log.d(TAG, " id: " + response.body().getUserId());
                Log.d(TAG, " over18: " + response.body().isOver18());
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
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
                .addHeader("User-Agent", "For Reddit Capstone")
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
                    getHomePage(userAccessToken);
                    Log.d(TAG, "Access token: " + userAccessToken);
                    Log.d(TAG, "Refresh token: " + userRefreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
     * Job scheduler used to check connectivity because its for api level >= 21 leaving this method on activity.
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
        //restoring scrolling position
        recyclerViewState = savedInstanceState.getParcelable("scroll_state");
    }
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving scrolling position of recycler view
        if(recyclerView != null && childList != null){
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable("scroll_state", recyclerViewState);
        }
    }
}
