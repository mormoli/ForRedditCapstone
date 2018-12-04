package com.capstone.udacity.forredditcapstone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.capstone.udacity.forredditcapstone.database.Favorite;
import com.capstone.udacity.forredditcapstone.model.favorites.FavoritesData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.FavoritesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity {
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

        if(getIntent() != null && getIntent().hasExtra("favoritesData")){
            favoritesData = getIntent().getParcelableArrayListExtra("favoritesData");
            //favoritesAdapter = new FavoritesAdapter();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(favoritesAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
