package com.capstone.udacity.forredditcapstone.model;

import android.os.Parcel;
import android.os.Parcelable;
/*
* Refreshing the token
* {
    "access_token": Your access token,
    "token_type": "bearer",
    "expires_in": Unix Epoch Seconds,
    "scope": A scope string,
* }
* @see 'https://github.com/reddit-archive/reddit/wiki/OAuth2'
*/

public class RefreshToken implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public RefreshToken createFromParcel(Parcel in){
            return new RefreshToken(in);
        }

        public RefreshToken[] newArray(int size){
            return new RefreshToken[size];
        }
    };

    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    private RefreshToken(Parcel in){
        this.accessToken = in.readString();
        this.tokenType = in.readString();
        this.expiresIn = in.readString();
        this.scope = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accessToken);
        dest.writeString(this.tokenType);
        dest.writeString(this.expiresIn);
        dest.writeString(this.scope);
    }
}
