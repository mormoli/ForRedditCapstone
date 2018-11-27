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
    Call<UserInfo> getUserInfo(@Header("Authorization") String authorization, @QueryMap Map<String , String > parameters);
    //https://stackoverflow.com/questions/43798263/handling-retrofit-status-codes-without-a-pojo-class
    @GET("/subreddits/mine/subscriber")
    @Headers(Constants.USER_AGENT)
    Call<SubList> getSubredditList(@Header("Authorization") String authorization, @QueryMap Map<String, String> parameters);
    //POST /api/hide : Hide a link. This removes it from the user's default view of subreddit listings.
    //@see 'https://www.reddit.com/dev/api/oauth/#POST_api_hide'
    // id: A comma-separated list of link fullnames
    @POST("/api/hide")
    @Headers(Constants.USER_AGENT)
    Call<ResponseBody> onHideClicked(@Header("Authorization") String authorization, @QueryMap Map<String, String> parameters);
    //POST /api/save : Save a link or comment.
    //@see 'https://www.reddit.com/dev/api/oauth/#POST_api_save'
    @POST("/api/save")
    @Headers(Constants.USER_AGENT)
    Call<ResponseBody> onSaveClicked(@Header("Authorization") String authorization, @QueryMap Map<String, String> parameters);

    @GET("/r/{subredditName}/.json")
    @Headers(Constants.USER_AGENT)
    Call<SubredditList> getSubredditHomePage(@Header("Authorization") String authorization, @Path(value = "subredditName") String subredditName,
                                             @QueryMap Map<String , String > parameters);
    /*
    * POST /api/subscribe : Subscribe to or unsubscribe from a subreddit.
    * @see 'https://www.reddit.com/dev/api/oauth/#POST_api_subscribe'
    * @param action	: one of (sub, unsub)
    * @param skip_initial_defaults : param can be set to True to prevent automatically subscribing the user to the current
    *       set of defaults when they take their first subscription action.
    *       Attempting to set it for an unsubscribe action will result in an error.
    * @param sr / sr_name : A comma-separated list of subreddit fullnames (when using the "sr" parameter),
    *       or of subreddit names (when using the "sr_name" parameter).
    **/
    @POST("/api/subscribe")
    @Headers(Constants.USER_AGENT)
    Call<ResponseBody> onConfirmClicked(@Header("Authorization") String authorization, @Field("action") String action,
                                        @Field("skip_initial_defaults") boolean skipDefaults, @Field("sr_name") String srName);
    // @see 'https://www.reddit.com/dev/api/oauth/#GET_user_{username}_{where}'
    //GET /user/username/saved : saved posts or comments
    @GET("/user/{username}/saved")
    @Headers(Constants.USER_AGENT)
    Call<ResponseBody> getUserSavedData(@Header("Authorization") String authorization,@Path(value = "username") String username,
                                        @QueryMap Map<String, String > parameters);
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

}
