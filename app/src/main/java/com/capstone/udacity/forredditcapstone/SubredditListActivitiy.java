package com.capstone.udacity.forredditcapstone;

import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.capstone.udacity.forredditcapstone.model.subreddits.SubListData;
import com.capstone.udacity.forredditcapstone.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubredditListActivitiy extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = SubredditListActivitiy.class.getSimpleName();
    private String userAccessToken, userRefreshToken;
    private SharedPreferences sharedPreferences;
    private Parcelable recyclerViewState;
    private List<SubListData> subListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
        userAccessToken = sharedPreferences.getString("accessToken", null);
        userRefreshToken = sharedPreferences.getString("refreshToken", null);

        if(getIntent() != null && getIntent().hasExtra("listData")){
            subListData = getIntent().getParcelableArrayListExtra("listData");
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) subListData);
            SubredditListFragment subredditListFragment = new SubredditListFragment();
            subredditListFragment.setArguments(bundle);
            //Begin Transaction
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.subreddit_list_layout, subredditListFragment);
            fragmentTransaction.commit();
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
}
