package com.capstone.udacity.forredditcapstone.utils;

import java.util.UUID;

public class Constants {
    public static final String CLIENT_ID = "Your client id here";
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
    public static final String SCOPE = "read identity save subscribe";
    public static final String APP_PREFS_NAME = "com.capstone.udacity.forredditcapstone.PREFS";
}
