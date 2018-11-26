package com.capstone.udacity.forredditcapstone.model.subreddits;

import com.google.gson.annotations.SerializedName;

public class Child {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private SubListData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public SubListData getData() {
        return data;
    }

    public void setData(SubListData data) {
        this.data = data;
    }
}
