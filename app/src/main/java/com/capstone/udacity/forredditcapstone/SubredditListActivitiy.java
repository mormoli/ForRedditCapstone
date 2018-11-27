package com.capstone.udacity.forredditcapstone;

import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.subreddits.SubListData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
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

public class SubredditListActivitiy extends AppCompatActivity implements SubredditListFragment.OnLayoutCardSelected{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = SubredditListActivitiy.class.getSimpleName();
    private String userAccessToken, userRefreshToken;
    private SharedPreferences sharedPreferences;
    private List<SubListData> subListData;
    private List<PostData> childList;
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

        /*if(savedInstanceState != null){
            subListData = savedInstanceState.getParcelableArrayList("listData");
        }*/

        if(getIntent() != null && getIntent().hasExtra("listData")){
            subListData = getIntent().getParcelableArrayListExtra("listData");
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) subListData);
            SubredditListFragment subredditListFragment = new SubredditListFragment();
            subredditListFragment.setArguments(bundle);
            subredditListFragment.setOnItemSelect(this);
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

    public void getSubredditHomePage(int position){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_OAUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheRedditApi theRedditApi = retrofit.create(TheRedditApi.class);
        String authorization = "bearer " + userAccessToken;
        String subredditName = subListData.get(position).getDisplayName();
        Map<String, String> map = new HashMap<>();
        map.put("limit", "25");
        map.put("sort", "top");
        map.put("include_over_18", "off");
        Call<SubredditList> call = theRedditApi.getSubredditHomePage(authorization, subredditName, map);

        call.enqueue(new Callback<SubredditList>() {
            @Override
            public void onResponse(@NonNull Call<SubredditList> call, @NonNull Response<SubredditList> response) {
                Log.d(TAG, " server response: " + response.toString());

                if(response.code() == 200){
                    if(childList == null) childList = new ArrayList<>();
                    assert response.body() != null;
                    if(response.body().getData().getChildren().size() > 0) {
                        for (int i = 0; i < response.body().getData().getChildren().size(); i++)
                            childList.add(response.body().getData().getChildren().get(i).getData());
                        openDetailsFragment(childList);
                    }
                } else {
                    Log.d(TAG, " server response: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubredditList> call, @NonNull Throwable t) {
                Log.d(TAG, " retrofit error: " + t.getMessage());
            }
        });
    }

    public void openDetailsFragment(List<PostData> childList){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("postData", (ArrayList<? extends Parcelable>) childList);
        SubredditDetailsFragment subredditDetailsFragment = new SubredditDetailsFragment();
        subredditDetailsFragment.setArguments(bundle);
        //Begin the transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        fragmentTransaction.replace(R.id.subreddit_list_layout, subredditDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        //Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void OnCardItemSelected(int position) {
        Log.d(TAG, "clicked: " + position);
        getSubredditHomePage(position);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*if(savedInstanceState != null) {
            subListData = savedInstanceState.getParcelableArrayList("listData");
        }*/
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) subListData);
    }
}
