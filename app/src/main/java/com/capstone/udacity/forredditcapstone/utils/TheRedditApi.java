package com.capstone.udacity.forredditcapstone.utils;

import com.capstone.udacity.forredditcapstone.model.CommentList;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.UserInfo;
import com.capstone.udacity.forredditcapstone.model.search.SearchList;
import com.capstone.udacity.forredditcapstone.model.subreddits.SubList;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
//https://www.reddit.com/dev/api/
/*
* Change your client's User-Agent string to something unique and descriptive,
* including the target platform, a unique application identifier, a version string,
* and your username as contact information, in the following format:
* <platform>:<app ID>:<version string> (by /u/<reddit username>)
* NOTE : NEVER lie about your user-agent.
* This includes spoofing popular browsers and spoofing other bots.
* We will ban liars with extreme prejudice.
* @see 'https://github.com/reddit-archive/reddit/wiki/API'
* */
public interface TheRedditApi {
    @GET("/.json")
    @Headers(Constants.USER_AGENT)
    Call<SubredditList> getHomePage(@Header("Authorization") String authorization, @QueryMap Map<String , String > parameters);
    //GET /api/v1/me : Returns the identity of the user.
    @GET("/api/v1/me")
    @Headers(Constants.USER_AGENT)
    Call<UserInfo> getUserInfo(@Header("Authorization") String authorization, @QueryMap Map<String , String > parameters);
    //https://stackoverflow.com/questions/43798263/handling-retrofit-status-codes-without-a-pojo-class
    @GET("/subreddits/mine/subscriber")
    @Headers(Constants.USER_AGENT)
    Call<SubList> getSubredditList(@Header("Authorization") String authorization, @QueryMap Map<String, String> parameters);
    //POST /api/hide : Hide a link. This removes it from the user's default view of subreddit listings.
    //POST /api/save : Save a link or comment.
    //GET /user/username/saved : saved posts or comments
    //@GET("/r/{subbreddit_name}/.json")
    //POST /api/search_subreddits : Subreddits whose names begin with query will be returned.
    // If include_over_18 is false-off, subreddits with over-18 content restrictions will be filtered from the results.
    //POST /api/comment : Submit a new comment or reply to a message.
    //POST /api/subscribe : Subscribe to or unsubscribe from a subreddit.
    //GET /subreddits/search : Search subreddits by title and description.
    @GET("/subreddits/search.json")
    @Headers(Constants.USER_AGENT)
    Call<SearchList> getSearchResults(@Header("Authorization") String authorization, @QueryMap Map<String, Object > parameters);

    @GET("/r/{subredditName}/comments/{postId}.json")
    @Headers(Constants.USER_AGENT)
    Call<List<CommentList>> getPostComments(@Header("Authorization") String authorization,
                                            @Path(value = "subredditName") String subredditName,
                                            @Path(value = "postId") String postId,
                                            @QueryMap Map<String , String > parameters);
    // https://www.reddit.com/dev/api/oauth/#POST_api_subscribe

}
