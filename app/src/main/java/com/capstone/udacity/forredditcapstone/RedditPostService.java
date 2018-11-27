package com.capstone.udacity.forredditcapstone;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.capstone.udacity.forredditcapstone.utils.Constants;

public class RedditPostService extends IntentService {
    private static final String TAG = RedditPostService.class.getSimpleName();

    public RedditPostService(){
        super(TAG);
        //@see 'https://developer.android.com/reference/android/app/IntentService#setIntentRedelivery(boolean)'
        setIntentRedelivery(false);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        if(intent.getAction() != null){
            switch (intent.getAction()){
                case Constants.API_HIDE :
                    break;
                case Constants.API_SAVE :
                    break;
                case Constants.API_SUBSCRIBE :
                    break;
            }
        }
    }
}
