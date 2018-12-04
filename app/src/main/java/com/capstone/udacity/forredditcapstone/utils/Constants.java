package com.capstone.udacity.forredditcapstone.utils;

import java.util.UUID;

public class Constants {
    public static final String CLIENT_ID = "CLIENT ID";
    public static final String USER_AGENT = "User-Agent: android:com.capstone.udacity.forredditcapstone:v1.0 (by /u/mormoli)";
    public static final String OAUTH_URL = "https://www.reddit.com/api/v1/authorize.compact";
    public static final String ACCESS_TOKEN_URL = "https://www.reddit.com/api/v1/access_token";
    public static final String REDIRECT_URI = "https://github.com/mormoli/ForRedditCapstone";
    public static final String BASE_OAUTH_URL = "https://oauth.reddit.com";
    /*
    * Sample usage of state taken from reddit
    * {reddit may choose to use this ID to generate aggregate data about user counts.
    * Clients that wish to remain anonymous should use the value DO_NOT_TRACK_THIS_DEVICE.}
    * @see 'https://github.com/reddit-archive/reddit/wiki/OAuth2'
    * */
    public static final String STATE = UUID.randomUUID().toString();
    /*
    * SCOPE is most important thing here ! for instance if you are going to retrieve user subscribed subreddit list
    * you must have mysubreddits permission in the scope request. If not: even successful registration and usage of phone
    * you can't retrieve data from end point = /subreddits/mine/subscriber !!! request without permission will result
    * of 403 error screen !
    * List of scopes and usages in JSON format : https://www.reddit.com/api/v1/scopes
    * */
    public static final String SCOPE = "read identity save subscribe mysubreddits edit history report";
    public static final String APP_PREFS_NAME = "com.capstone.udacity.forredditcapstone.PREFS";
    //Reddit POST action methods
    public static final String API_HIDE = "com.capstone.udacity.forredditcapstone.HIDE";
    public static final String API_SAVE = "com.capstone.udacity.forredditcapstone.SAVE";
    public static final String API_SUBSCRIBE = "com.capstone.udacity.forredditcapstone.SUBSCRIBE";
    //Database constants
    public static final String COMMENT_DATABASE_NAME = "comment_table";
    public static final String POST_DATABASE_NAME = "post_table";
}
