package com.capstone.udacity.forredditcapstone.utils;

import com.capstone.udacity.forredditcapstone.model.CommentList;
import com.capstone.udacity.forredditcapstone.model.CommentReader;
import com.capstone.udacity.forredditcapstone.model.RefreshToken;
import com.capstone.udacity.forredditcapstone.model.SubredditList;
import com.capstone.udacity.forredditcapstone.model.UserInfo;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
    Call<UserInfo> getUserInfo(@Header("Authorization") String authorization);
    //@see 'https://www.reddit.com/r/redditdev/comments/197x36/using_oauth_to_send_valid_requests/'
    @POST("/api/v1/access_token")
    @Headers(Constants.USER_AGENT)
    Call<RefreshToken> getAccessToken(@Header("Authorization") String auth, @Field("grant_type") String grantType,
                                      @Field("refresh_token") String refreshToken);
    //POST /api/hide : Hide a link. This removes it from the user's default view of subreddit listings.
    //POST /api/save : Save a link or comment.
    //GET /user/username/saved : saved posts or comments
    //@GET("/r/{subbreddit_name}/.json")
    //POST /api/search_subreddits : Subreddits whose names begin with query will be returned.
    // If include_over_18 is false-off, subreddits with over-18 content restrictions will be filtered from the results.
    //POST /api/comment : Submit a new comment or reply to a message.
    //POST /api/subscribe : Subscribe to or unsubscribe from a subreddit.
    @GET("/r/{subredditName}/comments/{postId}.json")
    @Headers(Constants.USER_AGENT)
    Call<CommentReader> getPostComments(@Header("Authorization") String authorization,
                                        @Path(value = "subredditName") String subredditName,
                                        @Path(value = "postId") String postId,
                                        @QueryMap Map<String , String > parameters);
}
