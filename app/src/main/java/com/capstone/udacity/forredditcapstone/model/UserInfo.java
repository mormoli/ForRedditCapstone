package com.capstone.udacity.forredditcapstone.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserInfo implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public UserInfo createFromParcel(Parcel in){
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size){
            return new UserInfo[size];
        }
    };
    @SerializedName("name")
    private String userName;
    @SerializedName("over_18")
    private boolean over18;
    @SerializedName("id")
    private String userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isOver18() {
        return over18;
    }

    public void setOver18(boolean over18) {
        this.over18 = over18;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    private UserInfo(Parcel in){
        this.userName = in.readString();
        this.over18 = in.readByte() != 0;
        this.userId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeByte((byte) (this.over18 ? 1 : 0));
        dest.writeString(this.userId);
    }
}
