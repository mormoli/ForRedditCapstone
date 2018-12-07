package com.capstone.udacity.forredditcapstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.subreddits.SubListData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.SubListFragmentAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

public class SubredditListFragment extends Fragment implements ResponseReceiver.OnResponse{
    private static final String TAG = SubredditListFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<SubListData> subListData;
    private SubListFragmentAdapter subListFragmentAdapter;
    private OnLayoutCardSelected onLayoutCardSelected;
    private Parcelable recyclerViewState;
    private ResponseReceiver mReceiver;
    private String userAccessToken, userRefreshToken;
    private String mActionText;
    private SharedPreferences sharedPreferences;
    //@see 'https://developers.google.com/admob/android/interstitial'
    private InterstitialAd mInterstitialAd;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubredditListFragment(){}

    public static SubredditListFragment newInstance(List<SubListData> subListData){
        SubredditListFragment subredditListFragment = new SubredditListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) subListData);
        subredditListFragment.setArguments(bundle);
        return subredditListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null){
            Log.d(TAG, "onCreate getting list!!!");
            if(subListData == null) subListData = new ArrayList<>();
            subListData = getArguments().getParcelableArrayList("listData");
        }
        if(getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
            userAccessToken = sharedPreferences.getString("accessToken", null);
            userRefreshToken = sharedPreferences.getString("refreshToken", null);
        }
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddit_list, container, false);
        // Initialize Admob
        mInterstitialAd = new InterstitialAd(view.getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
        /*if(getArguments() != null){
            Log.d(TAG, "onCreateView getting list!!!");
            if(subListData == null) subListData = new ArrayList<>();
            subListData = getArguments().getParcelableArrayList("listData");
        }*/
        recyclerView = view.findViewById(R.id.subreddit_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        subListFragmentAdapter = new SubListFragmentAdapter(subListData);
        recyclerView.setAdapter(subListFragmentAdapter);
        if (getActivity() != null)
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return view;
    }
    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subListFragmentAdapter.setOnClick(new SubListFragmentAdapter.OnItemClicked() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, " button clicked: " + ((Button) view).getText().toString());
                Log.d(TAG, " subreddit name : " + subListData.get(position).getDisplayNamePrefixed());
                Log.d(TAG, " fullname : " + subListData.get(position).getFullname());
                String name = ((Button) view).getText().toString();
                if(name.equals("subscribe")){
                    String action = "sub";
                    mActionText = "Subscribed";
                    Intent intent = new Intent(getActivity(), RedditPostService.class);
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("accessToken", userAccessToken);
                    intent.putExtra("action", action);
                    intent.putExtra("srName", subListData.get(position).getFullname());
                    intent.setAction(Constants.API_SUBSCRIBE);
                    if(getActivity() != null) getActivity().startService(intent);
                } else {
                    String action = "unsub";
                    mActionText = "Unsubscribed";
                    Intent intent = new Intent(getActivity(), RedditPostService.class);
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("accessToken", userAccessToken);
                    intent.putExtra("action", action);
                    intent.putExtra("srName", subListData.get(position).getFullname());
                    intent.setAction(Constants.API_SUBSCRIBE);
                    if(getActivity() != null) getActivity().startService(intent);
                }
            }

            @Override
            public void onLayoutClick(int position) {
                Log.d(TAG, "clicked: " + position);
                //To show an interstitial ad first checking if its loaded
                if (mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                } else {
                    Toast.makeText(getActivity(), "Admob is not ready", Toast.LENGTH_SHORT).show();
                }
                onLayoutCardSelected.OnCardItemSelected(position);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null)
            Log.d(TAG, "onActivityCreated list size: " + savedInstanceState.getParcelableArrayList("listData").size());
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if(subListData.size() > 0) {
            Log.d(TAG, "onResume restoring!!!");
            subListFragmentAdapter = new SubListFragmentAdapter(subListData);
            recyclerView.setAdapter(subListFragmentAdapter);
            subListFragmentAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "onResume restoring with arguments!!!");
            if(getArguments() != null) {
                subListData = getArguments().getParcelableArrayList("listData");
                Log.d(TAG, "onResume arguments size : " + getArguments().getParcelableArrayList("listData").size());
            }
            subListFragmentAdapter = new SubListFragmentAdapter(subListData);
            recyclerView.setAdapter(subListFragmentAdapter);
            subListFragmentAdapter.notifyDataSetChanged();
        }
    }*/

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(recyclerView != null && subListData != null) {
            Log.d(TAG, "SAVING DATA onInstanceState!!!");
            outState.putParcelableArrayList("listData", (ArrayList<? extends Parcelable>) subListData);
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable("scroll_state", recyclerViewState);
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
    public void onDetach() {
        super.onDetach();
        //if(subListData != null) subListData.clear();
    }

    @Override
    public void onResponseReceived(int resultCode, Bundle resultData) {
        if(resultCode == 200){
            Toast.makeText(getActivity(), mActionText + " successfully.", Toast.LENGTH_SHORT).show();
        }else if(resultCode == 401){
            //try to refresh token.
            Toast.makeText(getActivity(),getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
            getAccessToken();
        } else {
            //403 or something else happened, warn user.
            Toast.makeText(getActivity(),getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnLayoutCardSelected{
        void OnCardItemSelected(int position);
    }

    public void setOnItemSelect(OnLayoutCardSelected onItemSelect){
        onLayoutCardSelected = onItemSelect;
    }
}
