package com.capstone.udacity.forredditcapstone;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.capstone.udacity.forredditcapstone.database.Converters;
import com.capstone.udacity.forredditcapstone.database.DataViewModel;
import com.capstone.udacity.forredditcapstone.database.Favorite;
import com.capstone.udacity.forredditcapstone.model.favorites.FavoritesData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.FavoritesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity{
    @BindView(R.id.favorites_list_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = FavoritesActivity.class.getSimpleName();
    private String userAccessToken, userRefreshToken;
    private SharedPreferences sharedPreferences;
    private List<FavoritesData> favoritesData;
    private List<Favorite> favorites;
    private FavoritesAdapter favoritesAdapter;
    private DataViewModel mDataViewModel;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        userAccessToken = sharedPreferences.getString("accessToken", null);
        userRefreshToken = sharedPreferences.getString("refreshToken", null);

        if(getIntent() != null && getIntent().hasExtra("favoritesData")) {
            favoritesData = getIntent().getParcelableArrayListExtra("favoritesData");
            //recycler view adapter empty init.
            favoritesAdapter = new FavoritesAdapter(favoritesData, new FavoritesAdapter.LayoutClickListener() {
                @Override
                public void OnLayoutClicked(int position) {
                    if (favorites != null && favorites.size() > 0) {
                        String url = "https://www.reddit.com" + favorites.get(position).getPermalink();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } else {
                        String url = "https://www.reddit.com" + favoritesData.get(position).getPermalink();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        //also set in widget
                        if(favoritesData != null && favoritesData.size() > 0 ){
                            Intent widgetIntent = new Intent(getApplicationContext(), RedditAppWidget.class);
                            if(!TextUtils.isEmpty(favoritesData.get(position).getBody())){//body exist so the data is t1 : comment
                                String header = favoritesData.get(position).getAuthor() + " " + getTimeAgo(favoritesData.get(position).getCreatedUTC());
                                widgetIntent.putExtra("widgetHeader", header);
                                widgetIntent.putExtra("widgetBody", favoritesData.get(position).getBody());
                                widgetIntent.putExtra("widgetOnClick", favoritesData.get(position).getPermalink());
                            } else { // body not exist so the data is t3 : post
                                String header = favoritesData.get(position).getTitle() + " ( " + favoritesData.get(position).getDomain() + ")";
                                widgetIntent.putExtra("widgetHeader", header);
                                String body = "submitted " + getTimeAgo(favoritesData.get(position).getCreatedUTC()) + " * by "
                                        + favoritesData.get(position).getAuthor() + " " + favoritesData.get(position).getSubredditNamePrefixed();
                                widgetIntent.putExtra("widgetBody", body);
                                widgetIntent.putExtra("widgetOnClick", favoritesData.get(position).getPermalink());
                            }
                            widgetIntent.setAction(Constants.UPDATE_ACTION);
                            sendBroadcast(widgetIntent);
                        }
                        /*Intent widgetIntent = new Intent(getApplicationContext(), RedditAppWidget.class);
                        String header = mDataViewModel.getAllFavorites().getValue().get(position).getAuthor();
                        String body = mDataViewModel.getAllFavorites().getValue().get(position).getTitle();
                        String permalink = "https://www.reddit.com" + mDataViewModel.getAllFavorites().getValue().get(position).getPermalink();
                        widgetIntent.putExtra("widgetHeader", header);
                        widgetIntent.putExtra("widgetBody", body);
                        widgetIntent.putExtra("widgetOnClick", permalink);
                        widgetIntent.setAction(Constants.UPDATE_ACTION);
                        sendBroadcast(widgetIntent);*/
                    }
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoritesAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //initializing data view model and observer
        mDataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        mDataViewModel.getAllFavorites().observe(this, new Observer<List<Favorite>>(){
            @Override
            public void onChanged(@Nullable List<Favorite> favorites) {
                if(favorites == null || favorites.size() == 0 ){
                    // No data in database
                    Log.d(TAG, "database empty first initialization.");
                    populateDB();
                } else {
                    Log.d(TAG, "database set triggered.");
                    favoritesAdapter.setFavorites(favorites);
                }
            }
        });
    }
    /*
    * Method that populates Room Favorite database
    * */
    public void populateDB(){
        Log.d(TAG, "populate database method calls");
        if( favoritesData != null && favoritesData.size() > 0 ){
            if(favorites == null) favorites = new ArrayList<>();
            for(int i=0; i<favoritesData.size(); i++){
                favorites.add(Converters.fromFavoritePojoToRoom(favoritesData.get(i)));
            }
            mDataViewModel.insertAllFavorites(favorites);
        }
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
}
