package com.capstone.udacity.forredditcapstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.udacity.forredditcapstone.model.PostData;
import com.capstone.udacity.forredditcapstone.utils.Constants;
import com.capstone.udacity.forredditcapstone.utils.SubredditDetailsAdapter;

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

public class SubredditDetailsFragment extends Fragment implements ResponseReceiver.OnResponse{
    //private static final String TAG = SubredditDetailsFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<PostData> postData;
    private ResponseReceiver mReceiver;
    private String userAccessToken, userRefreshToken;
    private String mActionText;
    private SharedPreferences sharedPreferences;
    private int mPosition;
    private SubredditDetailsAdapter subredditDetailsAdapter;
    /* Default constructor will be added, as java documentetion states:
     * If a class contains no constructor declarations, then a default constructor with
     * no formal parameters and no throws clause is implicitly declared.
     */

    public static SubredditDetailsFragment newInstance(List<PostData> postData){
        SubredditDetailsFragment fragment = new SubredditDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("postData", (ArrayList<? extends Parcelable>) postData);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null){
            postData = getArguments().getParcelableArrayList("postData");
        }
        if(getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFS_NAME, MODE_PRIVATE);
            userAccessToken = sharedPreferences.getString("accessToken", null);
            userRefreshToken = sharedPreferences.getString("refreshToken", null);
        }
        //setting receiver object for intent service class
        mReceiver = new ResponseReceiver(new Handler());
        mReceiver.setReceiver(this);
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddit_details, container, false);
        recyclerView = view.findViewById(R.id.sub_detail_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subredditDetailsAdapter = new SubredditDetailsAdapter(postData, new SubredditDetailsAdapter.ButtonsListener() {
            @Override
            public void onHideButtonClick(View view, int position) {
                String fullName = postData.get(position).getFullName();
                Intent intent = new Intent(getActivity(), RedditPostService.class);
                intent.putExtra("receiver", mReceiver);
                intent.putExtra("accessToken", userAccessToken);
                intent.putExtra("name", fullName);
                intent.setAction(Constants.API_HIDE);
                if(getActivity() != null) getActivity().startService(intent);
                mActionText = "hided";
                mPosition = position;
            }

            @Override
            public void onSaveButtonClick(View view, int position) {
                String fullName = postData.get(position).getFullName();
                Intent intent = new Intent(getActivity(), RedditPostService.class);
                intent.putExtra("receiver", mReceiver);
                intent.putExtra("accessToken", userAccessToken);
                intent.putExtra("name", fullName);
                intent.setAction(Constants.API_SAVE);
                if(getActivity() != null) getActivity().startService(intent);
                mActionText = "saved";
                mPosition = position;
            }

            @Override
            public void onLayoutClicked(int position) {
                //open web view
                String urlToOpen = "https://www.reddit.com" + postData.get(position).getPermalink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlToOpen));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(subredditDetailsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            postData = bundle.getParcelableArrayList("postData");
        }
    }

    /*
     * Method that refresh's the given token
     * If you request permanent access, then you will need to refresh the tokens after 1 hour.
     * */
    private void getAccessToken(){
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
                    //Log.d(TAG, "Access token: " + userAccessToken);
                    //Log.d(TAG, "Refresh token: " + userRefreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("postData", (ArrayList<? extends Parcelable>) postData);
    }


    @Override
    public void onResponseReceived(int resultCode, Bundle resultData) {
        if(resultCode == 200){
            if(mActionText.equals("saved")){
                //save action: save post to database
                Toast.makeText(getActivity(), getString(R.string.save_button_message), Toast.LENGTH_SHORT).show();
            } else {
                //hide action: remove item and notify data changed.
                postData.remove(mPosition);
                subredditDetailsAdapter.notifyItemRemoved(mPosition);
                subredditDetailsAdapter.notifyItemRangeChanged(mPosition, postData.size());
                Toast.makeText(getActivity(), getString(R.string.hide_button_message), Toast.LENGTH_SHORT).show();
            }
        }else if(resultCode == 401){
            //try to refresh token.
            Toast.makeText(getActivity(),getString(R.string.unauthorized_access_error), Toast.LENGTH_SHORT).show();
            getAccessToken();
        } else {
            //403 or something else happened, warn user.
            Toast.makeText(getActivity(),getString(R.string.unknown_access_error), Toast.LENGTH_SHORT).show();
        }
    }
}
