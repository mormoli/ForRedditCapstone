package com.capstone.udacity.forredditcapstone.model;

import com.google.gson.annotations.SerializedName;

public class SubredditList {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private SubredditData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public SubredditData getData() {
        return data;
    }

    public void setData(SubredditData data) {
        this.data = data;
    }
}
