package com.capstone.udacity.forredditcapstone;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ResponseReceiver extends ResultReceiver {
    //private static final String TAG = ResultReceiver.class.getSimpleName();
    private OnResponse mReceiver;
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     */
    public ResponseReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(OnResponse mReceiver){
        this.mReceiver = mReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null){
            mReceiver.onResponseReceived(resultCode, resultData);
        }
        super.onReceiveResult(resultCode, resultData);
    }

    public interface OnResponse{
        void onResponseReceived(int resultCode, Bundle resultData);
    }
}
