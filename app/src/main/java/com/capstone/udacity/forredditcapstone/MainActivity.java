package com.capstone.udacity.forredditcapstone;

import android.app.SearchManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.database.Converters;
import com.capstone.udacity.forredditcapstone.database.DataViewModel;
import com.capstone.udacity.forredditcapstone.database.Post;
import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.UserInfo;
import com.capstone.udacity.forredditcapstone.model.favorites.Favorites;
import com.capstone.udacity.forredditcapstone.model.favorites.FavoritesData;
import com.capstone.udacity.forredditcapstone.model.search.SearchData;
import com.capstone.udacity.forredditcapstone.model.search.SearchList;
import com.capstone.udacity.forredditcapstone.model.subreddits.SubList;
import com.capstone.udacity.forredditcapstone.model.subreddits.SubListData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.HomePageAdapter;
import com.capstone.udacity.forredditcapstone.utils.TheRedditApi;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

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


public class MainActivity extends AppCompatActivity implements ResponseReceiver.OnResponse,
        ConnectivityReceiver.ConnectivityReceiverListener{
    //private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    @BindView(R.id.homepage_list_view)
    RecyclerView recyclerView;
    @BindView(R.id.main_activity_layout)
    CoordinatorLayout mCoordinatorLayout;

    private HomePageAdapter homePageAdapter;
    private List<PostData> childList;
    private List<SearchData> searchData;
    private List<Post> posts;
    private SearchView searchView;
    private String userAccessToken, userRefreshToken;
    private int refreshCount = 0;
    private String searchString;
    private ResponseReceiver mReceiver;
    private int mPosition;
    private String action;
    private DataViewModel mDataViewModel;
    private String fullName;
    private Parcelable recyclerViewState;
    private Menu mOptionsMenu;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        //AnalyticsApplication application = (AnalyticsApplication) getApplication();
        //mTracker = application.getDefaultTracker();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize Admob
        // This needs to be done only once, ideally at app launch.
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        //get tokens from shared preferences if value exist or not null
        if(sharedPreferences.getString("accessToken", null) != null) {
            userAccessToken = sharedPreferences.getString("accessToken", null);
            userRefreshToken = sharedPreferences.getString("refreshToken", null);
        }
        //setting receiver object for intent service class
        mReceiver = new ResponseReceiver(new Handler());
        mReceiver.setReceiver(this);
        //initialize recycler view and adapter
        populateUI(new ArrayList<PostData>());

        //view model object for database operations e.g Live data observe.
        mDataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        mDataViewModel.getAllPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                if(posts == null || posts.size() == 0 ){
                    // No data in database
                    if(!TextUtils.isEmpty(userAccessToken))getHomePage(userAccessToken);
                    //Log.d(TAG, "database empty first initialization.");
                } else {
                    homePageAdapter.setPosts(posts);
                    restoreLayoutManagerPosition();
                }
            }
        });

        if (getIntent() != null && getIntent().hasExtra("access")) {
            //Log.d(TAG, " getting user tokens. ");
            userAccessToken = sharedPreferences.getString("accessToken", null);
            userRefreshToken = sharedPreferences.getString("refreshToken", null);
        }

        if (savedInstanceState == null && TextUtils.isEmpty(userAccessToken)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("user", "new user");
            startActivity(intent);
        } else {
            //if(posts == null)
            //get username and save to shared preferences for later use.
            if(TextUtils.isEmpty(sharedPreferences.getString("username", null))) getUserInfo();
        }


    }
    /*
    * Method that restores scrolling state of recyclerview after adapter data populated.
    * */
    @SuppressWarnings("ConstantConditions")
    private void restoreLayoutManagerPosition(){
        if(recyclerView != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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
                getSubredditList();
                return true;
            case R.id.settings_favorites:
                //retrieve favorite list
                getUserFavorites();
                return true;
            case R.id.settings_logout:
                //revoke token
                Logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
    * Method that clears user tokens in shared preferences and opens login activity for user change.
    * Note: reddit app revoke token not used because in future will add tokens in to database for
    * faster change of users..
    * */
    public void Logout(){
        //SharedPreferences object:
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        //Delete all the shared preferences:
        preferencesEditor.clear();
        //Apply the changes:
        preferencesEditor.apply();
        //open login activity for user change
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("user", "new user");
        startActivity(intent);
    }
    /*
    * Method that retrieves user saved favorite list post/comments
    * from reddit oauth end point: /user/{username}/saved
    * */
    public void getUserFavorites(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;

        Map<String, String> map = new HashMap<>();
        map.put("include_over_18", "off");//query saved data only if content is safe for work.
        String username = sharedPreferences.getString("username", "");

        Call<Favorites> call = theRedditApi.getUserSavedData(authorization, username, map);
        call.enqueue(new Callback<Favorites>() {
            @Override
            public void onResponse(@NonNull Call<Favorites> call, @NonNull Response<Favorites> response) {
                //Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                List<FavoritesData> favoritesData = new ArrayList<>();
                if(response.code() == 200) {//Server response OK
                    if(response.body().getData().getFavoritesList().size() > 0){//User has saved data
                        for(int i=0; i<response.body().getData().getFavoritesList().size(); i++)//save data to list
                            favoritesData.add(response.body().getData().getFavoritesList().get(i).getData());
                        //open favorites activity to show data.
                        openFavoritesView(favoritesData);
                    } else {//User has no data to show !
                        Toast.makeText(getApplicationContext(), getString(R.string.no_favorites_data_error), Toast.LENGTH_SHORT).show();
                    }
                } else if(response.code() == 401){
                    //try to refresh token.
                    Toast.makeText(getApplicationContext(),getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
                    getAccessToken("refresh");
                } else {
                    //403 or something else happened, warn user.
                    Toast.makeText(getApplicationContext(),getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Favorites> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;
        // Retrieve the SearchView and plug it into SearchManager
        //@see 'https://developer.android.com/guide/topics/search/search-dialog#java'
        searchView = (SearchView) menu.findItem(R.id.settings_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.length() >= 3){
                    searchString = s;
                    //Log.d(TAG, " Search String: " + searchString);
                    getSearchResults();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.search_text_error), Toast.LENGTH_LONG).show();
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
    //Method that opens user saved favorite list view.
    public void openFavoritesView(List<FavoritesData> favoritesData){
        Intent intent = new Intent(this, FavoritesActivity.class);
        intent.putParcelableArrayListExtra("favoritesData", (ArrayList<? extends Parcelable>) favoritesData);
        startActivity(intent);
    }
    // method that opens search view
    public void openSearchListView(){
        Intent intent = new Intent(this, SearchListActivity.class);
        intent.putParcelableArrayListExtra("searchData", (ArrayList<? extends Parcelable>) searchData);
        startActivity(intent);
    }
    // method that opens subreddit list view
    public void openSubredditListView(List<SubListData> subListData){
        Intent intent = new Intent(this, SubredditListActivitiy.class);
        intent.putParcelableArrayListExtra("listData", (ArrayList<? extends Parcelable>) subListData);
        startActivity(intent);
    }
    /*
    * Method that retrieves user subscribed subreddit list
    * */
    public void getSubredditList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        //default limit 25
        Map<String, String> map = new HashMap<>();
        map.put("scope", "mysubreddits");
        map.put("include_over_18", "off");
        final List<SubListData> subListData = new ArrayList<>();
        Call<SubList> call = theRedditApi.getSubredditList(authorization, map);
        call.enqueue(new Callback<SubList>() {
            @Override
            public void onResponse(@NonNull Call<SubList> call, @NonNull Response<SubList> response) {
                //Log.d(TAG, " server response: " + response.toString());
                //Log.d(TAG, " server code: " + response.code());
                if (response.code() == 200){
                    assert response.body() != null;
                    //Log.d(TAG, "response body string: "+ response.body().toString());
                    if(response.body().getData().getChildren().size() > 0) {
                        if(subListData.size() > 0) subListData.clear();
                        for (int i=0; i<response.body().getData().getChildren().size(); i++) {
                            subListData.add(response.body().getData().getChildren().get(i).getData());
                        }
                        openSubredditListView(subListData);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.subreddit_list_error), Toast.LENGTH_SHORT).show();
                    }
                } else if(response.code() == 401){
                    //try to refresh token.
                    Toast.makeText(getApplicationContext(),getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
                    getAccessToken("refresh");
                } else {
                    //403 or something else happened, warn user.
                    Toast.makeText(getApplicationContext(),getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubList> call, @NonNull Throwable t) {
                //Log.e(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }
    /*
    * Method that returns subreddit search results
    * */
    public void getSearchResults(){
        searchView.setQuery("", false); //clear the text
        //searchView.setIconified(true);//close the search view
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
                //Log.d(TAG, " server response: " + response.toString());

                if(response.code() == 200){
                    assert response.body() != null;
                    if(response.body().getData().getChildren().size() > 0) {
                        if(searchData == null) searchData = new ArrayList<>();
                        if(searchData.size() > 0) searchData.clear();
                        for(int i=0; i<response.body().getData().getChildren().size(); i++)
                            searchData.add(response.body().getData().getChildren().get(i).getSearchData());
                        //Log.d(TAG, " DATA : " + searchData.get(0).toString());
                        //initSearchFragment();
                        openSearchListView();
                    } else{
                        Toast.makeText(getApplicationContext(), getString(R.string.search_keyword_text) + searchString + " "+ getString(R.string.not_found_message), Toast.LENGTH_SHORT).show();
                    }
                } else if(response.code() == 401){
                    //try to refresh token.
                    Toast.makeText(getApplicationContext(),getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
                    getAccessToken("refresh");
                } else {
                    //403 or something else happened, warn user.
                    Toast.makeText(getApplicationContext(),getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchList> call, @NonNull Throwable t) {
                //Log.e(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }
    /*
    * Method that sends usage statistics to firebase
    * */
    public void sendFirebaseAnalytics(){
        //send firebase analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "homepage");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "user home");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "reddit user home");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
    //https://www.getpostman.com
    //Postman app used to create returning values
    public void getHomePage(final String userAccessToken) {
        //clear data in database
        mDataViewModel.deleteAllPosts();

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
                //Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                if(childList == null) childList = new ArrayList<>();
                if(response.code() == 200) {
                    if(childList.size() > 0) childList.clear();
                    for(int i=0; i<response.body().getData().getChildren().size(); i++)
                        childList.add(response.body().getData().getChildren().get(i).getData());
                    //Log.d(TAG, " array title: " + childList.get(0).getData().getTitle());
                    //Log.d(TAG, " data size: " + childList.size());
                    populateUI(childList);//populate main activity screen with post data.
                    populateDB(childList);//populate database with reddit posts data.
                    sendFirebaseAnalytics();
                } else { //probably error code is 401 --> try refresh the token then call method again
                    //Log.d(TAG, " response code: " + response.code());
                    if(refreshCount < 2) getAccessToken("homepage");
                    else Toast.makeText(getApplicationContext(), getString(R.string.token_refresh_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubredditList> call, @NonNull Throwable t) {
                //Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }
    /*
    * Method that populates database with post data.
    * */
    public void populateDB(List<PostData> childList){
        //Log.d(TAG, "populate database method calls");
        if(posts == null) posts = new ArrayList<>();
        if(posts.size() > 0) posts.clear();
        for(int i=0; i<childList.size(); i++){
            posts.add(Converters.fromRetrofitPojoToRoom(childList.get(i)));
        }
        mDataViewModel.insertAll(posts);
    }
    /*
    * Method that populates recycler view of activity with reddit's post data
    * */
    public void populateUI(final List<PostData> childList){
        /*final boolean favoriteListHasData = mDataViewModel.getAllFavorites().getValue() != null
                && mDataViewModel.getAllFavorites().getValue().size() > 0;*/
        homePageAdapter = new HomePageAdapter(childList, new HomePageAdapter.ButtonsListener(){

            @Override
            public void onHideButtonClick(View view, int position) {
                //hide post and request to server with intent service class
                if(childList != null && childList.size() > 0)
                    fullName = childList.get(position).getFullName();
                else{
                    if(mDataViewModel.getAllPosts().getValue() != null)
                        fullName = mDataViewModel.getAllPosts().getValue().get(position).getFullname();
                }
                Intent intent = new Intent(getApplicationContext(), RedditPostService.class);
                intent.putExtra("receiver", mReceiver);
                intent.putExtra("accessToken", userAccessToken);
                intent.putExtra("name", fullName);
                intent.setAction(Constants.API_HIDE);
                startService(intent);
                action = "hided";
                mPosition = position;
            }

            @Override
            public void onSaveButtonClick(View view, int position) {
                //save post with room database also send request save to the reddit with intent service class
                if(childList != null && childList.size() > 0)
                    fullName = childList.get(position).getFullName();
                else {
                    if(mDataViewModel.getAllPosts().getValue() != null)
                        fullName = mDataViewModel.getAllPosts().getValue().get(position).getFullname();
                }
                Intent intent = new Intent(getApplicationContext(), RedditPostService.class);
                intent.putExtra("receiver", mReceiver);
                intent.putExtra("accessToken", userAccessToken);
                intent.putExtra("name", fullName);
                intent.setAction(Constants.API_SAVE);
                startService(intent);
                action = "saved";
                mPosition = position;
            }

            @Override
            public void onLayoutClicked(int position, PostData postData, String ups, String comments) {
                //open details activity with user selected post and comments
                String subredditName = null, postId = null;
                if(childList != null && childList.size() > 0) { //resource from network available.
                    subredditName = childList.get(position).getSubredditName();
                    postId = childList.get(position).getId();
                } else { // get items from database.
                    if(mDataViewModel.getAllPosts().getValue() != null) {
                        subredditName = mDataViewModel.getAllPosts().getValue().get(position).getSubredditName();
                        postId = mDataViewModel.getAllPosts().getValue().get(position).getId();
                    }
                }
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
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
    }
    /*
    * Method that retrieves logged user info like username - id etc.
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
                //Log.d(TAG, " server response: " + response.toString());
                assert response.body() != null;
                //Log.d(TAG, " Access Token : " + userAccessToken);
                //Log.d(TAG, " username: " + response.body().getUserName());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", response.body().getUserName()).apply();
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {
                //Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
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
                    if(callerMethod.equals("homepage"))getHomePage(userAccessToken);
                    //Log.d(TAG, "Access token: " + userAccessToken);
                    //Log.d(TAG, "Refresh token: " + userRefreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
            recyclerViewState = savedInstanceState.getParcelable("scroll_state");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(recyclerView != null){
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable("scroll_state", recyclerViewState);
        }
    }

    /*
    * Method that retrieves server response code and take actions about em.
    * */
    @Override
    public void onResponseReceived(int resultCode, Bundle resultData) {
        //Log.d(TAG, " resultCode: " + resultCode);
        //Log.d(TAG, " resultData: " + resultData.getString("data"));
        if(resultCode == 200){
            Toast.makeText(this, getString(R.string.post_text)+ action + " " + getString(R.string.action_text_success), Toast.LENGTH_SHORT).show();
            if(action.equals("saved")){
                //save action: save post to database
                if(childList != null && childList.size() > 0){ //add data from network resource to data
                    mDataViewModel.insert(Converters.fromPostDataToFavorites(childList.get(mPosition)));
                    //update app widget from saved data
                    Intent intent = new Intent(this, RedditPostService.class);
                    intent.putExtra("widgetHeader", childList.get(mPosition).getAuthor());
                    intent.putExtra("widgetBody", childList.get(mPosition).getTitle());
                    intent.putExtra("widgetOnClick", "https://www.reddit.com" + childList.get(mPosition).getPermalink());
                    intent.setAction(Constants.UPDATE_ACTION);
                    sendBroadcast(intent);
                } else { //update app widget with data from database
                    if(mDataViewModel.getAllPosts().getValue() != null) {
                        String header = mDataViewModel.getAllPosts().getValue().get(mPosition).getHeader();
                        String body = mDataViewModel.getAllPosts().getValue().get(mPosition).getTitle();
                        String permalink = "https://www.reddit.com" + mDataViewModel.getAllPosts().getValue().get(mPosition).getPermalink();
                        Intent intent = new Intent(this, RedditAppWidget.class);
                        intent.putExtra("widgetHeader", header);
                        intent.putExtra("widgetBody", body);
                        intent.putExtra("widgetOnClick", permalink);
                        intent.setAction(Constants.UPDATE_ACTION);
                        sendBroadcast(intent);
                    }
                }
            } else {
                //hide action: remove item and notify data changed.
                if(childList != null && childList.size() > 0) { // if list retrieved from network not null and has posts
                    childList.remove(mPosition);
                    homePageAdapter.notifyItemRemoved(mPosition);
                    homePageAdapter.notifyItemRangeChanged(mPosition, childList.size());
                } else { // delete item from database.
                    mDataViewModel.deletePostByName(fullName);
                    homePageAdapter.notifyItemRemoved(mPosition);
                    homePageAdapter.notifyDataSetChanged();
                }
            }
        } else if(resultCode == 401){
            //try to refresh token.
            Toast.makeText(this,getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
            getAccessToken("refresh");
        } else {
            //403 or something else happened, warn user.
            Toast.makeText(this,getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.no_network_message), Snackbar.LENGTH_INDEFINITE);
        if(!isConnected){//Internet is disconnected or no active network found !
            //disable menu items and search bar
            mOptionsMenu.findItem(R.id.settings_homepage).setVisible(false);
            mOptionsMenu.findItem(R.id.settings_subreddits).setVisible(false);
            mOptionsMenu.findItem(R.id.settings_favorites).setVisible(false);
            mOptionsMenu.findItem(R.id.settings_logout).setVisible(false);
            searchView.setEnabled(false);
            //show snackbar to the user about internet is not connected.
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        } else { // internet is connected
            //enable options menu items and search bar
            mOptionsMenu.findItem(R.id.settings_homepage).setVisible(true);
            mOptionsMenu.findItem(R.id.settings_subreddits).setVisible(true);
            mOptionsMenu.findItem(R.id.settings_favorites).setVisible(true);
            mOptionsMenu.findItem(R.id.settings_logout).setVisible(true);
            searchView.setEnabled(true);
            snackbar.dismiss();
        }
    }
}
