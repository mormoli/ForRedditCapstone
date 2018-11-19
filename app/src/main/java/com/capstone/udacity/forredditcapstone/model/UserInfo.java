package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @SerializedName("name")
    private String userName;
    @SerializedName("id")
    private String userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
