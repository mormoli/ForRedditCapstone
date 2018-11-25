package com.capstone.udacity.forredditcapstone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.capstone.udacity.forredditcapstone.model.search.SearchData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.SearchFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListActivity extends AppCompatActivity {
    @BindView(R.id.search_list_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = SearchListActivity.class.getSimpleName();
    private String userAccessToken, userRefreshToken;
    private SharedPreferences sharedPreferences;
    private Parcelable recyclerViewState;
    private List<SearchData> searchData;

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
            SearchFragmentAdapter searchFragmentAdapter = new SearchFragmentAdapter(searchData);
            searchFragmentAdapter.setOnClick(new SearchFragmentAdapter.OnItemClicked() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(TAG, " button clicked: " + ((Button) view).getText().toString());
                    Log.d(TAG, " subreddit name : " + searchData.get(position).getDisplayNamePrefixed());
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(searchFragmentAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
}
