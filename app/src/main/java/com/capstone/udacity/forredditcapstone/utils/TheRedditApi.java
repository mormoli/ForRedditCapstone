package com.capstone.udacity.forredditcapstone.utils;

import com.capstone.udacity.forredditcapstone.model.SubredditList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;
//https://www.reddit.com/dev/api/
public interface TheRedditApi {
    @GET("/.json")
    @Headers(Constants.USER_AGENT)
    Call<SubredditList> getHomePage(@Header("Authorization") String authorization, @QueryMap Map<String , String > options);
}
