package com.capstone.udacity.forredditcapstone.model.subreddits;

import com.google.gson.annotations.SerializedName;

public class SubList {
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private ChildrenData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public ChildrenData getData() {
        return data;
    }

    public void setData(ChildrenData data) {
        this.data = data;
    }
}
