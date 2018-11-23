package com.capstone.udacity.forredditcapstone;

import android.app.SearchManager;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.UserInfo;
import com.capstone.udacity.forredditcapstone.model.search.SearchData;
import com.capstone.udacity.forredditcapstone.model.search.SearchList;
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
    private ArrayList<PostData> childList;
    private List<SearchData> searchData;
    private String userAccessToken, userRefreshToken;
    private Parcelable recyclerViewState;
    private int refreshCount = 0;
    private String searchString;
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

            if (savedInstanceState == null && TextUtils.isEmpty(userAccessToken)) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("user", "new user");
                startActivity(intent);
            } else if(savedInstanceState != null){
                childList = savedInstanceState.getParcelableArrayList("homepage");
                populateUI(childList);
                /*recyclerView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                //At this point the layout is complete and the
                                //dimensions of recyclerView and any child views are known.
                                //Remove listener after changed RecyclerView's height to prevent infinite loop
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                } else {
                                    recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                }
                            }
                        });*/
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
        //@see 'https://developer.android.com/guide/topics/search/search-dialog#java'
        final SearchView searchView = (SearchView) menu.findItem(R.id.settings_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length() >= 3){
                    searchString = s;
                    Log.d(TAG, " Search String: " + searchString);
                    getSearchResults();
                } else {
                    Toast.makeText(getApplicationContext(), "Search text must be minimum of 3 characters.", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
        return true;
    }
    /*
    * Method that returns subreddit search results
    * */
    public void getSearchResults(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, Object> map = new HashMap<>();
        map.put("q", searchString);
        map.put("limit", "10");
        map.put("include_over_18", "off");
        Call<SearchList> call = theRedditApi.getSearchResults(authorization, map);

        call.enqueue(new Callback<SearchList>() {
            @Override
            public void onResponse(@NonNull Call<SearchList> call, @NonNull Response<SearchList> response) {
                Log.d(TAG, " server response: " + response.toString());

                if(response.code() == 200){
                    assert response.body() != null;
                    if(response.body().getData().getChildren().size() > 0) {
                        if(searchData == null) searchData = new ArrayList<>();
                        for(int i=0; i<response.body().getData().getChildren().size(); i++)
                            searchData.add(response.body().getData().getChildren().get(i).getSearchData());
                        Log.d(TAG, " DATA : " + searchData.get(0).toString());
                    } else{
                        Toast.makeText(getApplicationContext(), " Search keyword: " + searchString + " not found!", Toast.LENGTH_SHORT).show();
                    }
                    //Log.d(TAG, " if result not found : " + response.body().toString());
                    //Log.d(TAG, " subredditname : " + response.body().getData().getChildren().get(1).getSearchData().getDisplayName());
                    //Log.d(TAG, " description : " + response.body().getData().getChildren().get(1).getSearchData().getPublicDescription());
                    //Log.d(TAG, " returned query size: " + response.body().getData().getChildren().size());
                } else {
                    Log.d(TAG, "returned code : " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchList> call, @NonNull Throwable t) {
                Log.e(TAG, " retrofit error: " + t.getMessage());
            }
        });
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
        map.put("sort", "top");
        map.put("include_over_18", "off");
        Call<SubredditList> call = theRedditApi.getHomePage(authorization, map);

        call.enqueue(new Callback<SubredditList>() {
            @Override
            public void onResponse(@NonNull Call<SubredditList> call, @NonNull Response<SubredditList> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                if(childList == null) childList = new ArrayList<>();
                if(response.code() == 200) {
                    for(int i=0; i<response.body().getData().getChildren().size(); i++)
                        childList.add(response.body().getData().getChildren().get(i).getData());
                    //Log.d(TAG, " array title: " + childList.get(0).getData().getTitle());
                    Log.d(TAG, " data size: " + childList.size());
                    populateUI(childList);
                } else { //probably error code is 401 --> try refresh the token then call method again
                    Log.d(TAG, " response code: " + response.code());
                    if(refreshCount < 2) getAccessToken();
                    else Toast.makeText(getApplicationContext(), getString(R.string.token_refresh_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubredditList> call, @NonNull Throwable t) {
                Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }
    /*
    * Method that populates recycler view of activity with reddit's post data*/
    public void populateUI(final List<PostData> childList){
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
            public void onLayoutClicked(int position, PostData postData, String ups, String comments) {
                //open detailed post view
                String subredditName = childList.get(position).getSubredditName();
                String postId = childList.get(position).getId();
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("postData", postData);
                intent.putExtra("points", ups);
                intent.putExtra("comments", comments);
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
    /*
    * Method that retrieves logged user info like name - id etc.
    * adding username to the shared preferences for future call about hide/save/subscribe options
    * */
    private void getUserInfo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        Map<String, String> map = new HashMap<>();
        map.put("scope", "identity");

        Call<UserInfo> call = theRedditApi.getUserInfo(authorization, map);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                Log.d(TAG, " Access Token : " + userAccessToken);
                Log.d(TAG, " username: " + response.body().getUserName());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", response.body().getUserName()).apply();
                Log.d(TAG, " id: " + response.body().getUserId());
                Log.d(TAG, " over18: " + response.body().isOver18());
                Log.d(TAG, " response string: " +response.body().toString());
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
        if(savedInstanceState != null)
            recyclerViewState = savedInstanceState.getParcelable("scroll_state");
        //childList = savedInstanceState.getParcelableArrayList("homepage");
    }
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the response for the homepage
        //saving list requires casting --> (ArrayList<? extends Parcelable>)
        //instead of casting, changed list attribute childList element to ArrayList<>
        outState.putParcelableArrayList("homepage", childList);
        //saving scrolling position of recycler view
        if(recyclerView != null && childList != null){
            //int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable("scroll_state", recyclerViewState);
            //outState.putInt("scrollPosition", lastVisiblePosition);
        }
    }
}
